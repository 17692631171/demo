package com.example.demo.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dto.user.UserCreateDTO;
import com.example.demo.dto.user.UserUpdateDTO;
import com.example.demo.entity.user.User;
import com.example.demo.mapper.user.UserMapper;
import com.example.demo.service.user.UserService;
import com.example.demo.util.exception.BusinessException;
import com.example.demo.util.network.IpUtil;
import com.example.demo.util.response.BaseResponse;
import com.example.demo.util.response.ResponseCode;
import com.example.demo.util.security.JwtUtil;
import com.example.demo.util.security.PasswordUtil;
import com.example.demo.vo.user.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserDetailsService userDetailsService;
    private final HttpServletRequest request;

    // Redis key前缀
    private static final String USER_TOKEN_PREFIX = "user:token:";
    private static final String USER_REFRESH_TOKEN_PREFIX = "user:refresh_token:";
    private static final String LOGIN_FAILURE_PREFIX = "login:failure:";

    // 登录失败限制
    private static final int MAX_LOGIN_FAILURE = 5;
    private static final long LOGIN_LOCK_TIME = 15 * 60; // 15分钟

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<UserVO> register(UserCreateDTO userDTO) {
        try {
            // 1. 参数验证
            validateRegisterDTO(userDTO);

            // 2. 检查唯一性约束
            checkUniqueConstraints(userDTO);

            // 3. 创建用户实体
            User user = createUserFromDTO(userDTO);

            // 4. 保存用户
            boolean saved = this.save(user);
            if (!saved) {
                log.error("用户注册失败 - 保存到数据库失败: {}", userDTO.getUsername());
                throw new BusinessException(ResponseCode.USER_REGISTER_FAILED);
            }

            // 5. 记录注册日志
            String ip = IpUtil.getClientIp(request);
            log.info("用户注册成功 - 用户名: {}, IP: {}", user.getUsername(), ip);

            // 6. 返回响应
            UserVO userVO = convertToVO(user);
            return BaseResponse.success("注册成功", userVO);

        } catch (BusinessException e) {
            log.warn("用户注册失败 - 业务异常: {}", e.getMessage());
            return BaseResponse.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户注册失败 - 系统异常: ", e);
            return BaseResponse.fail(ResponseCode.SYSTEM_ERROR, "注册失败，请稍后重试");
        }
    }

    @Override
    public BaseResponse<Map<String, Object>> login(String username, String password) {
        try {
            // 1. 检查登录失败次数
            String failureKey = LOGIN_FAILURE_PREFIX + username;
            Integer failureCount = (Integer) redisTemplate.opsForValue().get(failureKey);
            if (failureCount != null && failureCount >= MAX_LOGIN_FAILURE) {
                log.warn("登录失败次数过多 - 用户名: {}", username);
                return BaseResponse.fail(ResponseCode.AUTH_FAILED, "登录失败次数过多，请15分钟后再试");
            }

            // 2. 验证用户
            User user = userMapper.selectByUsername(username);
            if (user == null) {
                recordLoginFailure(username, failureKey, failureCount);
                return BaseResponse.fail(ResponseCode.USER_NOT_EXIST);
            }

            // 3. 检查用户状态
            if (user.getStatus() != null && user.getStatus() == 0) {
                log.warn("用户已被禁用 - 用户名: {}", username);
                return BaseResponse.fail(ResponseCode.USER_DISABLED);
            }

            // 4. 验证密码
            if (!passwordUtil.matches(password, user.getPassword())) {
                recordLoginFailure(username, failureKey, failureCount);
                return BaseResponse.fail(ResponseCode.USER_PASSWORD_ERROR);
            }

            // 5. 清除登录失败记录
            redisTemplate.delete(failureKey);

            // 6. 生成Token
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            // 7. 保存Token到Redis
            String tokenKey = USER_TOKEN_PREFIX + username;
            String refreshTokenKey = USER_REFRESH_TOKEN_PREFIX + username;
            redisTemplate.opsForValue().set(tokenKey, accessToken, 24, TimeUnit.HOURS);
            redisTemplate.opsForValue().set(refreshTokenKey, refreshToken, 7, TimeUnit.DAYS);

            // 8. 更新最后登录时间
            userMapper.updateLastLoginTime(user.getId());

            // 9. 记录登录日志
            String ip = IpUtil.getClientIp(request);
            log.info("用户登录成功 - 用户名: {}, IP: {}", username, ip);

            // 10. 返回响应
            Map<String, Object> result = new HashMap<>();
            result.put("accessToken", accessToken);
            result.put("refreshToken", refreshToken);
            result.put("tokenType", "Bearer");
            result.put("expiresIn", 24 * 3600);
            result.put("user", convertToVO(user));

            return BaseResponse.success("登录成功", result);

        } catch (Exception e) {
            log.error("用户登录失败 - 系统异常", e);
            return BaseResponse.fail(ResponseCode.AUTH_FAILED, "登录失败，请稍后重试");
        }
    }

    @Override
    public BaseResponse<UserVO> getUserByUsername(String username) {
        try {
            User user = userMapper.selectByUsername(username);
            if (user == null) {
                return BaseResponse.fail(ResponseCode.USER_NOT_EXIST);
            }

            UserVO userVO = convertToVO(user);
            return BaseResponse.success(userVO);

        } catch (Exception e) {
            log.error("获取用户信息失败 - 用户名: {}, 异常: ", username, e);
            return BaseResponse.fail(ResponseCode.DATA_QUERY_FAILED);
        }
    }

    @Override
    public BaseResponse<UserVO> getUserById(Long userId) {
        try {
            User user = this.getById(userId);
            if (user == null) {
                return BaseResponse.fail(ResponseCode.USER_NOT_EXIST);
            }

            UserVO userVO = convertToVO(user);
            return BaseResponse.success(userVO);

        } catch (Exception e) {
            log.error("获取用户信息失败 - 用户ID: {}, 异常: ", userId, e);
            return BaseResponse.fail(ResponseCode.DATA_QUERY_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<UserVO> updateUser(Long userId, UserUpdateDTO userDTO) {
        try {
            // 1. 检查用户是否存在
            User existingUser = this.getById(userId);
            if (existingUser == null) {
                return BaseResponse.fail(ResponseCode.USER_NOT_EXIST);
            }

            // 2. 验证唯一性约束
            validateUpdateConstraints(existingUser, userDTO);

            // 3. 更新用户信息
            User updateUser = new User();
            BeanUtils.copyProperties(userDTO, updateUser, "password");
            updateUser.setId(userId);

            boolean updated = this.updateById(updateUser);
            if (!updated) {
                return BaseResponse.fail(ResponseCode.USER_UPDATE_FAILED);
            }

            // 4. 获取更新后的用户信息
            User updatedUser = this.getById(userId);
            UserVO userVO = convertToVO(updatedUser);

            // 5. 记录日志
            log.info("用户信息更新成功 - 用户ID: {}, 用户名: {}", userId, updatedUser.getUsername());

            return BaseResponse.success("更新成功", userVO);

        } catch (BusinessException e) {
            return BaseResponse.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新用户失败 - 用户ID: {}, 异常: ", userId, e);
            return BaseResponse.fail(ResponseCode.USER_UPDATE_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Void> changePassword(Long userId, String oldPassword, String newPassword) {
        try {
            // 1. 获取用户
            User user = this.getById(userId);
            if (user == null) {
                return BaseResponse.fail(ResponseCode.USER_NOT_EXIST);
            }

            // 2. 验证原密码
            if (!passwordUtil.matches(oldPassword, user.getPassword())) {
                log.warn("修改密码失败 - 原密码错误 - 用户ID: {}", userId);
                return BaseResponse.fail(ResponseCode.USER_PASSWORD_ERROR, "原密码错误");
            }

            // 3. 更新密码
            String encryptedPassword = passwordUtil.encode(newPassword);
            user.setPassword(encryptedPassword);

            boolean updated = this.updateById(user);
            if (!updated) {
                return BaseResponse.fail(ResponseCode.USER_UPDATE_FAILED);
            }

            // 4. 清除用户token，强制重新登录
            clearUserTokens(user.getUsername());

            // 5. 记录日志
            log.info("用户修改密码成功 - 用户ID: {}, 用户名: {}", userId, user.getUsername());

            return BaseResponse.success("密码修改成功");

        } catch (Exception e) {
            log.error("修改密码失败 - 用户ID: {}, 异常: ", userId, e);
            return BaseResponse.fail(ResponseCode.SYSTEM_ERROR, "密码修改失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Void> resetPassword(Long userId, String newPassword) {
        try {
            // 1. 获取用户
            User user = this.getById(userId);
            if (user == null) {
                return BaseResponse.fail(ResponseCode.USER_NOT_EXIST);
            }

            // 2. 更新密码
            String encryptedPassword = passwordUtil.encode(newPassword);
            user.setPassword(encryptedPassword);

            boolean updated = this.updateById(user);
            if (!updated) {
                return BaseResponse.fail(ResponseCode.USER_UPDATE_FAILED);
            }

            // 3. 清除用户token
            clearUserTokens(user.getUsername());

            // 4. 记录日志
            log.info("管理员重置用户密码 - 用户ID: {}, 用户名: {}", userId, user.getUsername());

            return BaseResponse.success("密码重置成功");

        } catch (Exception e) {
            log.error("重置密码失败 - 用户ID: {}, 异常: ", userId, e);
            return BaseResponse.fail(ResponseCode.SYSTEM_ERROR, "密码重置失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Void> updateUserStatus(Long userId, Integer status) {
        try {
            // 1. 验证状态值
            if (status != 0 && status != 1) {
                return BaseResponse.fail(ResponseCode.PARAM_ERROR, "状态值必须为0或1");
            }

            // 2. 获取用户
            User user = this.getById(userId);
            if (user == null) {
                return BaseResponse.fail(ResponseCode.USER_NOT_EXIST);
            }

            // 3. 更新状态
            user.setStatus(status);
            boolean updated = this.updateById(user);
            if (!updated) {
                return BaseResponse.fail(ResponseCode.USER_UPDATE_FAILED);
            }

            // 4. 如果禁用用户，清除token
            if (status == 0) {
                clearUserTokens(user.getUsername());
            }

            // 5. 记录日志
            log.info("更新用户状态 - 用户ID: {}, 用户名: {}, 新状态: {}",
                    userId, user.getUsername(), status);

            return BaseResponse.success("状态更新成功");

        } catch (Exception e) {
            log.error("更新用户状态失败 - 用户ID: {}, 异常: ", userId, e);
            return BaseResponse.fail(ResponseCode.SYSTEM_ERROR, "状态更新失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Void> batchUpdateUserStatus(List<Long> userIds, Integer status) {
        try {
            // 1. 验证状态值
            if (status != 0 && status != 1) {
                return BaseResponse.fail(ResponseCode.PARAM_ERROR, "状态值必须为0或1");
            }

            // 2. 批量更新状态
            int updated = userMapper.updateStatusBatch(userIds, status);
            if (updated <= 0) {
                return BaseResponse.fail(ResponseCode.USER_UPDATE_FAILED);
            }

            // 3. 如果禁用用户，清除token
            if (status == 0) {
                for (Long userId : userIds) {
                    User user = this.getById(userId);
                    if (user != null) {
                        clearUserTokens(user.getUsername());
                    }
                }
            }

            // 4. 记录日志
            log.info("批量更新用户状态 - 用户ID列表: {}, 新状态: {}", userIds, status);

            return BaseResponse.success("批量更新成功");

        } catch (Exception e) {
            log.error("批量更新用户状态失败 - 异常: ", e);
            return BaseResponse.fail(ResponseCode.SYSTEM_ERROR, "批量更新失败");
        }
    }

    @Override
    public boolean isUsernameExists(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username)
                .eq(User::getDeleted, 0);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public boolean isPhoneExists(String phone) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone)
                .eq(User::getDeleted, 0);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public boolean isEmailExists(String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email)
                .eq(User::getDeleted, 0);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public BaseResponse<Page<UserVO>> getUserPage(Integer page, Integer size,
                                                  String username, String phone,
                                                  Integer status, String startTime,
                                                  String endTime) {
        try {
            // 1. 创建分页对象
            Page<User> userPage = new Page<>(page, size);

            // 2. 构建查询条件
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getDeleted, 0);

            if (StringUtils.hasText(username)) {
                queryWrapper.like(User::getUsername, username);
            }
            if (StringUtils.hasText(phone)) {
                queryWrapper.like(User::getPhone, phone);
            }
            if (status != null) {
                queryWrapper.eq(User::getStatus, status);
            }
            if (StringUtils.hasText(startTime)) {
                queryWrapper.ge(User::getCreateTime, LocalDateTime.parse(startTime));
            }
            if (StringUtils.hasText(endTime)) {
                queryWrapper.le(User::getCreateTime, LocalDateTime.parse(endTime));
            }

            queryWrapper.orderByDesc(User::getCreateTime);

            // 3. 执行查询
            Page<User> result = this.page(userPage, queryWrapper);

            // 4. 转换为VO
            Page<UserVO> voPage = new Page<>();
            BeanUtils.copyProperties(result, voPage, "records");

            List<UserVO> voList = result.getRecords().stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());
            voPage.setRecords(voList);

            return BaseResponse.success(voPage);

        } catch (Exception e) {
            log.error("分页查询用户失败 - 异常: ", e);
            return BaseResponse.fail(ResponseCode.DATA_QUERY_FAILED);
        }
    }

    @Override
    public BaseResponse<Map<String, Object>> getUserStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // 1. 总用户数
            Long totalUsers = userMapper.countUsers();
            statistics.put("totalUsers", totalUsers);

            // 2. 今日新增用户
            Long todayNewUsers = userMapper.countTodayNewUsers();
            statistics.put("todayNewUsers", todayNewUsers);

            // 3. 启用用户数
            LambdaQueryWrapper<User> enabledQuery = new LambdaQueryWrapper<>();
            enabledQuery.eq(User::getStatus, 1)
                    .eq(User::getDeleted, 0);
            Long enabledUsers = this.count(enabledQuery);
            statistics.put("enabledUsers", enabledUsers);

            // 4. 禁用用户数
            LambdaQueryWrapper<User> disabledQuery = new LambdaQueryWrapper<>();
            disabledQuery.eq(User::getStatus, 0)
                    .eq(User::getDeleted, 0);
            Long disabledUsers = this.count(disabledQuery);
            statistics.put("disabledUsers", disabledUsers);

            // 5. 用户增长趋势（最近7天）
            // 这里可以添加更复杂的统计逻辑

            return BaseResponse.success(statistics);

        } catch (Exception e) {
            log.error("获取用户统计信息失败 - 异常: ", e);
            return BaseResponse.fail(ResponseCode.DATA_QUERY_FAILED);
        }
    }

    @Override
    public BaseResponse<Void> logout(String token) {
        try {
            // 1. 从token中提取用户名
            String username = jwtUtil.extractUsername(token);

            // 2. 清除用户token
            clearUserTokens(username);

            // 3. 记录日志
            log.info("用户登出 - 用户名: {}", username);

            return BaseResponse.success("登出成功");

        } catch (Exception e) {
            log.error("用户登出失败 - 异常: ", e);
            return BaseResponse.fail(ResponseCode.SYSTEM_ERROR, "登出失败");
        }
    }

    @Override
    public BaseResponse<Map<String, Object>> refreshToken(String refreshToken) {
        try {
            // 1. 验证refresh token
            if (!jwtUtil.validateToken(refreshToken)) {
                return BaseResponse.fail(ResponseCode.TOKEN_INVALID, "Refresh token无效");
            }

            // 2. 提取用户名
            String username = jwtUtil.extractUsername(refreshToken);

            // 3. 检查refresh token是否在Redis中
            String refreshTokenKey = USER_REFRESH_TOKEN_PREFIX + username;
            String storedRefreshToken = (String) redisTemplate.opsForValue().get(refreshTokenKey);
            if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
                return BaseResponse.fail(ResponseCode.TOKEN_INVALID, "Refresh token已失效");
            }

            // 4. 生成新的access token
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String newAccessToken = jwtUtil.generateAccessToken(userDetails);

            // 5. 更新Redis中的token
            String tokenKey = USER_TOKEN_PREFIX + username;
            redisTemplate.opsForValue().set(tokenKey, newAccessToken, 24, TimeUnit.HOURS);

            // 6. 返回新的token
            Map<String, Object> result = new HashMap<>();
            result.put("accessToken", newAccessToken);
            result.put("tokenType", "Bearer");
            result.put("expiresIn", 24 * 3600);

            return BaseResponse.success("Token刷新成功", result);

        } catch (Exception e) {
            log.error("刷新token失败 - 异常: ", e);
            return BaseResponse.fail(ResponseCode.SYSTEM_ERROR, "Token刷新失败");
        }
    }

    // ==================== 私有方法 ====================

    private void validateRegisterDTO(UserCreateDTO userDTO) {
        if (userDTO == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "注册信息不能为空");
        }
        if (!StringUtils.hasText(userDTO.getUsername())) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "用户名不能为空");
        }
        if (!StringUtils.hasText(userDTO.getPassword())) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "密码不能为空");
        }
        if (!StringUtils.hasText(userDTO.getName())) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "姓名不能为空");
        }
    }

    private void checkUniqueConstraints(UserCreateDTO userDTO) {
        if (isUsernameExists(userDTO.getUsername())) {
            throw new BusinessException(ResponseCode.USER_EXIST, "用户名已存在");
        }
        if (StringUtils.hasText(userDTO.getEmail()) && isEmailExists(userDTO.getEmail())) {
            throw new BusinessException(ResponseCode.USER_EXIST, "邮箱已被注册");
        }
        if (StringUtils.hasText(userDTO.getPhone()) && isPhoneExists(userDTO.getPhone())) {
            throw new BusinessException(ResponseCode.USER_EXIST, "手机号已被注册");
        }
    }

    private void validateUpdateConstraints(User existingUser, UserUpdateDTO userDTO) {
        if (StringUtils.hasText(userDTO.getEmail())
                && !userDTO.getEmail().equals(existingUser.getEmail())
                && isEmailExists(userDTO.getEmail())) {
            throw new BusinessException(ResponseCode.USER_EXIST, "邮箱已被使用");
        }
        if (StringUtils.hasText(userDTO.getPhone())
                && !userDTO.getPhone().equals(existingUser.getPhone())
                && isPhoneExists(userDTO.getPhone())) {
            throw new BusinessException(ResponseCode.USER_EXIST, "手机号已被使用");
        }
    }

    private User createUserFromDTO(UserCreateDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        // 密码加密
        String encryptedPassword = passwordUtil.encode(userDTO.getPassword());
        user.setPassword(encryptedPassword);

        // 设置默认值
        if (user.getStatus() == null) {
            user.setStatus(1); // 默认启用
        }

        return user;
    }

    private UserVO convertToVO(User user) {
        if (user == null) {
            return null;
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 格式化敏感信息
        if (StringUtils.hasText(userVO.getPhone())) {
            userVO.setPhone(maskPhone(userVO.getPhone()));
        }
        if (StringUtils.hasText(userVO.getEmail())) {
            userVO.setEmail(maskEmail(userVO.getEmail()));
        }

        return userVO;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        if (parts[0].length() <= 2) {
            return parts[0].charAt(0) + "***@" + parts[1];
        }
        return parts[0].substring(0, 2) + "***@" + parts[1];
    }

    private void recordLoginFailure(String username, String failureKey, Integer currentCount) {
        int newCount = currentCount != null ? currentCount + 1 : 1;
        redisTemplate.opsForValue().set(failureKey, newCount, LOGIN_LOCK_TIME, TimeUnit.SECONDS);
        log.warn("登录失败 - 用户名: {}, 失败次数: {}", username, newCount);
    }

    private void clearUserTokens(String username) {
        String tokenKey = USER_TOKEN_PREFIX + username;
        String refreshTokenKey = USER_REFRESH_TOKEN_PREFIX + username;
        redisTemplate.delete(tokenKey);
        redisTemplate.delete(refreshTokenKey);
    }
}
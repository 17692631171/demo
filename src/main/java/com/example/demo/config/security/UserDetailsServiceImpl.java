package com.example.demo.config.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.user.User;
import com.example.demo.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // 根据用户名查询用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, username)
                    .eq(User::getDeleted, 0);

            User user = userMapper.selectOne(queryWrapper);

            if (user == null) {
                log.warn("用户不存在: {}", username);
                throw new UsernameNotFoundException("用户不存在: " + username);
            }

            // 检查用户状态
            if (user.getStatus() != null && user.getStatus() == 0) {
                log.warn("用户已被禁用: {}", username);
                throw new UsernameNotFoundException("用户已被禁用: " + username);
            }

            log.debug("加载用户成功: {}", username);
            return new SecurityUserDetails(user);

        } catch (Exception e) {
            log.error("加载用户失败: {}", username, e);
            throw new UsernameNotFoundException("加载用户失败: " + username, e);
        }
    }

    /**
     * 根据用户ID加载用户
     */
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        try {
            User user = userMapper.selectById(userId);

            if (user == null) {
                log.warn("用户不存在: ID={}", userId);
                throw new UsernameNotFoundException("用户不存在: ID=" + userId);
            }

            log.debug("通过ID加载用户成功: ID={}", userId);
            return new SecurityUserDetails(user);

        } catch (Exception e) {
            log.error("通过ID加载用户失败: ID={}", userId, e);
            throw new UsernameNotFoundException("通过ID加载用户失败: ID=" + userId, e);
        }
    }
}
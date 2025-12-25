package com.example.demo.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.dto.user.UserCreateDTO;
import com.example.demo.dto.user.UserUpdateDTO;
import com.example.demo.entity.user.User;
import com.example.demo.util.response.BaseResponse;
import com.example.demo.vo.user.UserVO;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    BaseResponse<UserVO> register(UserCreateDTO userDTO);

    /**
     * 用户登录
     */
    BaseResponse<Map<String, Object>> login(String username, String password);

    /**
     * 根据用户名获取用户信息
     */
    BaseResponse<UserVO> getUserByUsername(String username);

    /**
     * 根据用户ID获取用户信息
     */
    BaseResponse<UserVO> getUserById(Long userId);

    /**
     * 更新用户信息
     */
    BaseResponse<UserVO> updateUser(Long userId, UserUpdateDTO userDTO);

    /**
     * 修改密码
     */
    BaseResponse<Void> changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置密码
     */
    BaseResponse<Void> resetPassword(Long userId, String newPassword);

    /**
     * 更新用户状态
     */
    BaseResponse<Void> updateUserStatus(Long userId, Integer status);

    /**
     * 批量更新用户状态
     */
    BaseResponse<Void> batchUpdateUserStatus(List<Long> userIds, Integer status);

    /**
     * 检查用户名是否已存在
     */
    boolean isUsernameExists(String username);

    /**
     * 检查手机号是否已存在
     */
    boolean isPhoneExists(String phone);

    /**
     * 检查邮箱是否已存在
     */
    boolean isEmailExists(String email);

    /**
     * 分页查询用户列表
     */
    BaseResponse<Page<UserVO>> getUserPage(Integer page, Integer size,
                                           String username, String phone,
                                           Integer status, String startTime,
                                           String endTime);

    /**
     * 获取用户统计信息
     */
    BaseResponse<Map<String, Object>> getUserStatistics();

    /**
     * 登出
     */
    BaseResponse<Void> logout(String token);

    /**
     * 刷新token
     */
    BaseResponse<Map<String, Object>> refreshToken(String refreshToken);
}
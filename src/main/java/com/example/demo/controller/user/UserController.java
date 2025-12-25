package com.example.demo.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.user.UserCreateDTO;
import com.example.demo.dto.user.UserUpdateDTO;
import com.example.demo.service.user.UserService;
import com.example.demo.util.exception.BusinessException;
import com.example.demo.util.response.BaseResponse;
import com.example.demo.util.response.ResponseCode;
import com.example.demo.vo.user.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    private final UserService userService;
    private final HttpServletRequest request;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册接口")
    public BaseResponse<UserVO> register(@Valid @RequestBody UserCreateDTO userDTO) {
        return userService.register(userDTO);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口")
    public BaseResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest.getUsername(), loginRequest.getPassword());
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出接口")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<Void> logout() {
        String token = extractTokenFromRequest();
        return userService.logout(token);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "刷新Token", description = "使用refresh token刷新access token")
    public BaseResponse<Map<String, Object>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return BaseResponse.fail(ResponseCode.PARAM_ERROR, "refreshToken不能为空");
        }
        return userService.refreshToken(refreshToken);
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<UserVO> getCurrentUserInfo() {
        // 从SecurityContext中获取当前用户信息
        // 这里简化处理，实际应从SecurityContext获取
        String username = (String) request.getAttribute("username");
        if (username == null) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED, "用户未登录");
        }
        return userService.getUserByUsername(username);
    }

    @GetMapping("/id/{userId}")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public BaseResponse<UserVO> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "根据用户名获取用户", description = "根据用户名获取用户信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public BaseResponse<UserVO> getUserByUsername(
            @Parameter(description = "用户名") @PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PutMapping("/id/{userId}")
    @Operation(summary = "更新用户信息", description = "更新指定用户的信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #userId == authentication.principal.id")
    public BaseResponse<UserVO> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Valid @RequestBody UserUpdateDTO userDTO) {
        return userService.updateUser(userId, userDTO);
    }

    @PutMapping("/id/{userId}/password")
    @Operation(summary = "修改密码", description = "用户修改自己的密码")
    @PreAuthorize("#userId == authentication.principal.id")
    public BaseResponse<Void> changePassword(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @RequestBody Map<String, String> passwordInfo) {
        String oldPassword = passwordInfo.get("oldPassword");
        String newPassword = passwordInfo.get("newPassword");

        if (oldPassword == null || oldPassword.isEmpty()) {
            return BaseResponse.fail(ResponseCode.PARAM_ERROR, "原密码不能为空");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            return BaseResponse.fail(ResponseCode.PARAM_ERROR, "新密码不能为空");
        }

        return userService.changePassword(userId, oldPassword, newPassword);
    }

    @PutMapping("/id/{userId}/reset-password")
    @Operation(summary = "重置密码", description = "管理员重置用户密码")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<Void> resetPassword(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @RequestBody Map<String, String> passwordInfo) {
        String newPassword = passwordInfo.get("newPassword");

        if (newPassword == null || newPassword.isEmpty()) {
            return BaseResponse.fail(ResponseCode.PARAM_ERROR, "新密码不能为空");
        }

        return userService.resetPassword(userId, newPassword);
    }

    @PutMapping("/id/{userId}/status")
    @Operation(summary = "更新用户状态", description = "启用或禁用用户")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public BaseResponse<Void> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @RequestBody Map<String, Integer> statusInfo) {
        Integer status = statusInfo.get("status");

        if (status == null) {
            return BaseResponse.fail(ResponseCode.PARAM_ERROR, "状态不能为空");
        }
        if (status != 0 && status != 1) {
            return BaseResponse.fail(ResponseCode.PARAM_ERROR, "状态值必须为0或1");
        }

        return userService.updateUserStatus(userId, status);
    }

    @PutMapping("/batch/status")
    @Operation(summary = "批量更新用户状态", description = "批量启用或禁用用户")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<Void> batchUpdateUserStatus(
            @RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Long> userIds = (List<Long>) request.get("userIds");
        Integer status = (Integer) request.get("status");

        if (userIds == null || userIds.isEmpty()) {
            return BaseResponse.fail(ResponseCode.PARAM_ERROR, "用户ID列表不能为空");
        }
        if (status == null) {
            return BaseResponse.fail(ResponseCode.PARAM_ERROR, "状态不能为空");
        }
        if (status != 0 && status != 1) {
            return BaseResponse.fail(ResponseCode.PARAM_ERROR, "状态值必须为0或1");
        }

        return userService.batchUpdateUserStatus(userIds, status);
    }

    @GetMapping("/check-username")
    @Operation(summary = "检查用户名", description = "检查用户名是否已存在")
    public BaseResponse<Map<String, Boolean>> checkUsername(
            @Parameter(description = "用户名") @RequestParam String username) {
        boolean exists = userService.isUsernameExists(username);
        Map<String, Boolean> result = Map.of("exists", exists, "available", !exists);
        return BaseResponse.success(result);
    }

    @GetMapping("/check-phone")
    @Operation(summary = "检查手机号", description = "检查手机号是否已存在")
    public BaseResponse<Map<String, Boolean>> checkPhone(
            @Parameter(description = "手机号") @RequestParam String phone) {
        boolean exists = userService.isPhoneExists(phone);
        Map<String, Boolean> result = Map.of("exists", exists, "available", !exists);
        return BaseResponse.success(result);
    }

    @GetMapping("/check-email")
    @Operation(summary = "检查邮箱", description = "检查邮箱是否已存在")
    public BaseResponse<Map<String, Boolean>> checkEmail(
            @Parameter(description = "邮箱") @RequestParam String email) {
        boolean exists = userService.isEmailExists(email);
        Map<String, Boolean> result = Map.of("exists", exists, "available", !exists);
        return BaseResponse.success(result);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询用户", description = "获取用户分页列表")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public BaseResponse<Page<UserVO>> getUserPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "手机号") @RequestParam(required = false) String phone,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime) {

        return userService.getUserPage(page, size, username, phone, status, startTime, endTime);
    }

    @GetMapping("/statistics")
    @Operation(summary = "用户统计", description = "获取用户统计信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public BaseResponse<Map<String, Object>> getUserStatistics() {
        return userService.getUserStatistics();
    }

    @DeleteMapping("/id/{userId}")
    @Operation(summary = "删除用户", description = "逻辑删除用户")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            // 逻辑删除用户
            boolean deleted = userService.removeById(userId);
            if (!deleted) {
                return BaseResponse.fail(ResponseCode.USER_DELETE_FAILED);
            }

            // 清除用户token
            String username = (String) request.getAttribute("username");
            if (username != null) {
                // 这里需要根据userId获取username
                // 简化处理，实际应查询数据库获取username
                log.info("删除用户 - 用户ID: {}", userId);
            }

            return BaseResponse.success("删除成功");

        } catch (Exception e) {
            log.error("删除用户失败 - 用户ID: {}, 异常: ", userId, e);
            return BaseResponse.fail(ResponseCode.SYSTEM_ERROR, "删除失败");
        }
    }

    private String extractTokenFromRequest() {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
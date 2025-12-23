package com.example.demo.dto.user;

import lombok.Data; /**
 * 用户响应DTO（不返回密码等敏感信息）
 */
@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private Integer status;
    private String createTime;
    private String updateTime;
}

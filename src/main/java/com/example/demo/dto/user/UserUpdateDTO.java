package com.example.demo.dto.user;

import com.example.demo.constant.RegexPattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = RegexPattern.PHONE, message = "手机号格式不正确")
    private String phone;

    private Integer status;
}
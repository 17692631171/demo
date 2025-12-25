package com.example.demo.util.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码工具类，提供密码加密、验证和随机密码生成功能
 * 使用Spring框架的@Component注解标记为组件，便于Spring容器管理
 */
@Component
public class PasswordUtil {

    /**
     * 密码编码器，使用BCrypt加密算法
     * BCrypt是一种安全的哈希算法，专门用于密码存储
     */
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 对原始密码进行加密
     * @param rawPassword 原始密码
     * @return 加密后的密码字符串
     */
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 验证原始密码与加密密码是否匹配
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 如果密码匹配返回true，否则返回false
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 生成指定长度的随机密码
     * 密码包含大写字母、小写字母、数字和特殊字符
     * @param length 密码长度
     * @return 生成的随机密码字符串
     */
    public String generateRandomPassword(int length) {
        // 定义密码字符集，包含大小写字母、数字和特殊字符
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        // 使用StringBuilder构建密码字符串
        StringBuilder sb = new StringBuilder();
        // 循环生成指定长度的密码
        for (int i = 0; i < length; i++) {
            // 随机选择字符索引
            int index = (int) (Math.random() * chars.length());
            // 将选中的字符追加到字符串构建器
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}
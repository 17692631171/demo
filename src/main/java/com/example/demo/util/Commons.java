package com.example.demo.util;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 常用工具类
 */
public class Commons {
    /**
     * 对用户密码进行加密处理
     * @param str 原始密码字符串
     * @return 返回经过MD5加密后的密码字符串
     */
    public static String encryptStr(String str) {
        // 这里使用简单的MD5加密，实际项目中建议使用更安全的加密方式如BCrypt
        return DigestUtils.md5DigestAsHex(str.getBytes(StandardCharsets.UTF_8));
    }
    /**
     * 加密字符串方法
     * @param str 需要加密的原始字符串
     * @param salt 加密盐值，用于增强加密安全性
     * @return 返回经过MD5加密后的字符串
     */
    public static String encryptStr(String str,String salt) {
        // 这里使用简单的MD5加密，实际项目中建议使用更安全的加密方式如BCrypt
        return DigestUtils.md5DigestAsHex((str+salt).getBytes(StandardCharsets.UTF_8));
    }
    public static void main(String[] args) {
        System.out.println(encryptStr("123456"));
        System.out.println(encryptStr("123456","123"));
    }

}

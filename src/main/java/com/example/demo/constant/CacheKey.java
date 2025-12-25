package com.example.demo.constant;

public class CacheKey {

    // 用户相关缓存
    public static final String USER_PREFIX = "user:";
    public static final String USER_DETAIL = USER_PREFIX + "detail:";
    public static final String USER_LIST = USER_PREFIX + "list";

    // 权限相关缓存
    public static final String PERMISSION_PREFIX = "permission:";
    public static final String ROLE_PREFIX = "role:";

    // 验证码相关缓存
    public static final String CAPTCHA_PREFIX = "captcha:";
    public static final String SMS_CODE_PREFIX = "sms:code:";
    public static final String EMAIL_CODE_PREFIX = "email:code:";

    // 限流相关缓存
    public static final String RATE_LIMIT_PREFIX = "rate:limit:";

    // 生成完整的缓存key
    public static String userDetailKey(Long userId) {
        return USER_DETAIL + userId;
    }

    public static String captchaKey(String sessionId) {
        return CAPTCHA_PREFIX + sessionId;
    }

    public static String smsCodeKey(String phone) {
        return SMS_CODE_PREFIX + phone;
    }

    public static String emailCodeKey(String email) {
        return EMAIL_CODE_PREFIX + email;
    }
}
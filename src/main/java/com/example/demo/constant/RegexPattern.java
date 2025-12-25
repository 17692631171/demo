package com.example.demo.constant;

public class RegexPattern {

    // 用户名正则：字母、数字、下划线，4-20位
    public static final String USERNAME = "^[a-zA-Z0-9_]{4,20}$";

    // 密码正则：至少6位，包含字母和数字
    public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{6,}$";

    // 手机号正则
    public static final String PHONE = "^1[3-9]\\d{9}$";

    // 邮箱正则
    public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // 身份证正则
    public static final String ID_CARD = "^\\d{17}[\\dXx]$";

    // URL正则
    public static final String URL = "^https?://[\\w\\-\\.]+(:\\d+)?(/[\\w\\-\\./?%&=]*)?$";

    // IP地址正则
    public static final String IP = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
}
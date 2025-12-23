package com.example.demo.util.response;

import lombok.Getter;

/**
 * 响应码枚举
 * 所有业务错误码都在这里定义，便于统一管理和维护
 */
@Getter
public enum ResponseCode {

    // ==================== 系统通用错误码 (0-999) ====================
    SUCCESS(0, "操作成功"),
    FAIL(1, "操作失败"),
    SYSTEM_ERROR(2, "系统错误"),
    PARAM_ERROR(3, "参数错误"),

    // ==================== HTTP状态码映射 (1000-1099) ====================
    BAD_REQUEST(1000, "请求参数错误"),
    UNAUTHORIZED(1001, "未授权，请先登录"),
    FORBIDDEN(1002, "禁止访问，权限不足"),
    NOT_FOUND(1003, "请求的资源不存在"),
    METHOD_NOT_ALLOWED(1004, "请求方法不允许"),
    REQUEST_TIMEOUT(1005, "请求超时"),
    TOO_MANY_REQUESTS(1006, "请求过于频繁"),
    INTERNAL_SERVER_ERROR(1007, "服务器内部错误"),
    SERVICE_UNAVAILABLE(1008, "服务暂时不可用"),

    // ==================== 用户相关错误码 (2000-2099) ====================
    USER_NOT_EXIST(2000, "用户不存在"),
    USER_PASSWORD_ERROR(2001, "密码错误"),
    USER_DISABLED(2002, "用户已被禁用"),
    USER_EXIST(2003, "用户已存在"),
    USER_NOT_LOGIN(2004, "用户未登录"),
    USER_REGISTER_FAILED(2005, "用户注册失败"),
    USER_UPDATE_FAILED(2006, "用户更新失败"),
    USER_DELETE_FAILED(2007, "用户删除失败"),

    // ==================== 认证授权错误码 (2100-2199) ====================
    AUTH_FAILED(2100, "认证失败"),
    TOKEN_INVALID(2101, "Token无效"),
    TOKEN_EXPIRED(2102, "Token已过期"),
    TOKEN_MISSING(2103, "Token缺失"),
    PERMISSION_DENIED(2104, "权限不足"),
    ROLE_NOT_EXIST(2105, "角色不存在"),
    ROLE_EXIST(2106, "角色已存在"),

    // ==================== 数据相关错误码 (2200-2299) ====================
    DATA_NOT_FOUND(2200, "数据不存在"),
    DATA_EXIST(2201, "数据已存在"),
    DATA_VALIDATE_FAIL(2202, "数据验证失败"),
    DATA_CREATE_FAILED(2203, "数据创建失败"),
    DATA_UPDATE_FAILED(2204, "数据更新失败"),
    DATA_DELETE_FAILED(2205, "数据删除失败"),
    DATA_QUERY_FAILED(2206, "数据查询失败"),

    // ==================== 文件相关错误码 (2300-2399) ====================
    FILE_UPLOAD_FAILED(2300, "文件上传失败"),
    FILE_DOWNLOAD_FAILED(2301, "文件下载失败"),
    FILE_NOT_EXIST(2302, "文件不存在"),
    FILE_TOO_LARGE(2303, "文件大小超过限制"),
    FILE_TYPE_NOT_ALLOWED(2304, "文件类型不允许"),
    FILE_READ_FAILED(2305, "文件读取失败"),
    FILE_WRITE_FAILED(2306, "文件写入失败"),

    // ==================== 业务逻辑错误码 (3000-3999) ====================
    BUSINESS_ERROR(3000, "业务逻辑错误"),
    OPERATION_FAILED(3001, "操作失败"),
    VALIDATION_FAILED(3002, "验证失败"),
    CONFIG_ERROR(3003, "配置错误"),

    // ==================== 第三方服务错误码 (4000-4999) ====================
    THIRD_PARTY_ERROR(4000, "第三方服务错误"),
    API_CALL_FAILED(4001, "API调用失败"),
    NETWORK_ERROR(4002, "网络连接失败"),

    // ==================== 数据库错误码 (5000-5099) ====================
    DATABASE_ERROR(5000, "数据库错误"),
    DATABASE_CONNECTION_FAILED(5001, "数据库连接失败"),
    SQL_EXECUTION_FAILED(5002, "SQL执行失败"),

    // ==================== 缓存错误码 (5100-5199) ====================
    CACHE_ERROR(5100, "缓存错误"),
    CACHE_CONNECTION_FAILED(5101, "缓存连接失败"),
    CACHE_KEY_NOT_EXIST(5102, "缓存键不存在"),

    // ==================== 消息队列错误码 (5200-5299) ====================
    MQ_ERROR(5200, "消息队列错误"),
    MQ_SEND_FAILED(5201, "消息发送失败"),
    MQ_RECEIVE_FAILED(5202, "消息接收失败"),

    // ==================== 未知错误 ====================
    UNKNOWN_ERROR(9999, "未知错误");

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    ResponseCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据错误码获取枚举
     */
    public static ResponseCode getByCode(Integer code) {
        if (code == null) {
            return UNKNOWN_ERROR;
        }
        for (ResponseCode responseCode : ResponseCode.values()) {
            if (responseCode.getCode().equals(code)) {
                return responseCode;
            }
        }
        return UNKNOWN_ERROR;
    }

    /**
     * 根据错误消息获取枚举
     */
    public static ResponseCode getByMessage(String message) {
        if (message == null || message.isEmpty()) {
            return UNKNOWN_ERROR;
        }
        for (ResponseCode responseCode : ResponseCode.values()) {
            if (responseCode.getMessage().equals(message)) {
                return responseCode;
            }
        }
        return UNKNOWN_ERROR;
    }

    /**
     * 判断是否为成功状态码
     */
    public boolean isSuccess() {
        return this.code == 0;
    }

    /**
     * 判断是否为客户端错误
     */
    public boolean isClientError() {
        return this.code >= 1000 && this.code < 2000;
    }

    /**
     * 判断是否为服务端错误
     */
    public boolean isServerError() {
        return this.code >= 2000;
    }
}
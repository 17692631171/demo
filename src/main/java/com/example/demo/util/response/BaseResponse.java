package com.example.demo.util.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 统一API响应类
 * 所有Controller返回的数据都应该包装成此格式
 *
 * @param <T> 响应数据类型
 */
@Data
@Accessors(chain = true)  // 支持链式调用
@JsonInclude(JsonInclude.Include.NON_NULL)  // 为null的字段不序列化
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==================== 响应字段 ====================
    /**
     * 状态码
     * 0: 成功
     * 其他: 失败，具体错误码参考ResponseCode枚举
     */
    @JsonProperty("code")
    private Integer code;

    /**
     * 响应消息
     */
    @JsonProperty("msg")
    private String message;

    /**
     * 响应数据
     */
    @JsonProperty("data")
    private T data;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 请求路径（可选）
     */
    private String path;

    /**
     * 请求ID（可选，用于追踪）
     */
    private String requestId;

    /**
     * 错误详情（可选，用于调试）
     */
    private Object errorDetail;

    // ==================== 构造方法 ====================
    /**
     * 私有构造方法，使用工厂方法创建对象
     */
    private BaseResponse() {
        // 使用当前时间作为时间戳
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    /**
     * 带参构造方法
     */
    private BaseResponse(Integer code, String message, T data) {
        this();
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 使用枚举创建响应
     */
    private BaseResponse(ResponseCode responseCode, T data) {
        this(responseCode.getCode(), responseCode.getMessage(), data);
    }

    // ==================== 成功响应工厂方法 ====================

    /**
     * 成功响应（无数据）
     */
    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(ResponseCode.SUCCESS, null);
    }

    /**
     * 成功响应（有数据）
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(ResponseCode.SUCCESS, data);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> BaseResponse<T> success(String message) {
        return new BaseResponse<>(ResponseCode.SUCCESS.getCode(), message, null);
    }

    /**
     * 成功响应（自定义消息和数据）
     */
    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<>(ResponseCode.SUCCESS.getCode(), message, data);
    }

    // ==================== 失败响应工厂方法 ====================

    /**
     * 失败响应（使用错误码枚举）
     */
    public static <T> BaseResponse<T> fail(ResponseCode responseCode) {
        return new BaseResponse<>(responseCode, null);
    }

    /**
     * 失败响应（使用错误码枚举+数据）
     */
    public static <T> BaseResponse<T> fail(ResponseCode responseCode, T data) {
        return new BaseResponse<>(responseCode, data);
    }

    /**
     * 失败响应（自定义错误码和消息）
     */
    public static <T> BaseResponse<T> fail(Integer code, String message) {
        return new BaseResponse<>(code, message, null);
    }

    /**
     * 失败响应（自定义错误码、消息和数据）
     */
    public static <T> BaseResponse<T> fail(Integer code, String message, T data) {
        return new BaseResponse<>(code, message, data);
    }

    /**
     * 失败响应（使用错误码枚举+自定义消息）
     */
    public static <T> BaseResponse<T> fail(ResponseCode responseCode, String message) {
        return new BaseResponse<>(responseCode.getCode(), message, null);
    }

    /**
     * 失败响应（使用错误码枚举+自定义消息+数据）
     */
    public static <T> BaseResponse<T> fail(ResponseCode responseCode, String message, T data) {
        return new BaseResponse<>(responseCode.getCode(), message, data);
    }

    // ==================== 参数校验异常专用方法 ====================

    /**
     * 参数校验失败（返回 Map<String, String> 类型）
     * 用于全局异常处理器的参数校验异常
     */
    public static BaseResponse<Map<String, String>> paramValidationError(Map<String, String> errors) {
        BaseResponse<Map<String, String>> response = new BaseResponse<>(
                ResponseCode.PARAM_ERROR.getCode(),
                ResponseCode.PARAM_ERROR.getMessage(),
                null);
        response.setErrorDetail(errors);
        return response;
    }

    /**
     * 参数校验失败（自定义消息）
     */
    public static BaseResponse<Map<String, String>> paramValidationError(String message, Map<String, String> errors) {
        BaseResponse<Map<String, String>> response = new BaseResponse<>(
                ResponseCode.PARAM_ERROR.getCode(),
                message,
                null);
        response.setErrorDetail(errors);
        return response;
    }

    /**
     * 参数校验失败（自定义错误码和消息）
     */
    public static BaseResponse<Map<String, String>> paramValidationError(Integer code, String message, Map<String, String> errors) {
        BaseResponse<Map<String, String>> response = new BaseResponse<>(code, message, null);
        response.setErrorDetail(errors);
        return response;
    }

    // ==================== 常用便捷方法 ====================

    /**
     * 参数错误（通用方法）
     */
    public static <T> BaseResponse<T> paramError() {
        return fail(ResponseCode.PARAM_ERROR);
    }

    public static <T> BaseResponse<T> paramError(String message) {
        return fail(ResponseCode.PARAM_ERROR, message);
    }

    public static <T> BaseResponse<T> paramError(T data) {
        return fail(ResponseCode.PARAM_ERROR, data);
    }

    /**
     * 未授权
     */
    public static <T> BaseResponse<T> unauthorized() {
        return fail(ResponseCode.UNAUTHORIZED);
    }

    public static <T> BaseResponse<T> unauthorized(String message) {
        return fail(ResponseCode.UNAUTHORIZED, message);
    }

    /**
     * 禁止访问
     */
    public static <T> BaseResponse<T> forbidden() {
        return fail(ResponseCode.FORBIDDEN);
    }

    public static <T> BaseResponse<T> forbidden(String message) {
        return fail(ResponseCode.FORBIDDEN, message);
    }

    /**
     * 资源不存在
     */
    public static <T> BaseResponse<T> notFound() {
        return fail(ResponseCode.NOT_FOUND);
    }

    public static <T> BaseResponse<T> notFound(String message) {
        return fail(ResponseCode.NOT_FOUND, message);
    }

    /**
     * 业务错误
     */
    public static <T> BaseResponse<T> businessError() {
        return fail(ResponseCode.BUSINESS_ERROR);
    }

    public static <T> BaseResponse<T> businessError(String message) {
        return fail(ResponseCode.BUSINESS_ERROR, message);
    }

    // ==================== 业务方法 ====================

    /**
     * 判断响应是否成功
     */
    public boolean isSuccess() {
        return this.code != null && this.code.equals(ResponseCode.SUCCESS.getCode());
    }

    /**
     * 获取响应数据，如果成功则返回数据，否则抛出异常
     */
    @JsonIgnore
    public T getOrThrow() {
        if (!isSuccess()) {
            throw new RuntimeException("请求失败: " + this.message + " (错误码: " + this.code + ")");
        }
        return this.data;
    }

    /**
     * 链式设置数据
     */
    public BaseResponse<T> withData(T data) {
        this.data = data;
        return this;
    }

    /**
     * 链式设置消息
     */
    public BaseResponse<T> withMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * 链式设置路径
     */
    public BaseResponse<T> withPath(String path) {
        this.path = path;
        return this;
    }

    /**
     * 链式设置请求ID
     */
    public BaseResponse<T> withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * 链式设置错误详情
     */
    public BaseResponse<T> withErrorDetail(Object errorDetail) {
        this.errorDetail = errorDetail;
        return this;
    }

    /**
     * 转换为JSON字符串
 * 重写Object类的toString()方法，用于将BaseResponse对象转换为字符串表示形式
 * 返回的字符串包含对象的各个属性值，格式为JSON风格
 *
 * @return 返回一个包含对象所有属性值的字符串，格式为"BaseResponse{属性1=值1, 属性2=值2, ...}"
     */
    @Override  // 表示重写父类的方法
    public String toString() {

    // 使用字符串拼接方式构建返回字符串
        return "BaseResponse{" +  // 返回字符串的开始部分，包含类名
                "code=" + code +  // 拼接code属性值
                ", message='" + message + '\'' +  // 拼接message属性值，使用单引号包围字符串
                ", data=" + data +  // 拼接data属性值
                ", timestamp='" + timestamp + '\'' +  // 拼接timestamp属性值，使用单引号包围字符串
                ", path='" + path + '\'' +  // 拼接path属性值，使用单引号包围字符串
                ", requestId='" + requestId + '\'' +  // 拼接requestId属性值，使用单引号包围字符串
                '}';  // 返回字符串的结束部分
    }
}
package com.example.demo.config.handler;

import com.example.demo.util.MdcUtil;
import com.example.demo.util.response.BaseResponse;
import com.example.demo.util.response.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 统一处理所有异常，并返回标准响应格式
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 创建专门的异常记录器
    private static final Logger exceptionLogger = LoggerFactory.getLogger("EXCEPTION_LOGGER");

    // ==================== 参数校验异常 ====================

    /**
     * 参数校验异常（RequestBody校验）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Map<String, String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        Map<String, String> errors = buildValidationErrors(e);
        logValidationException("参数校验失败", e, request, errors);

        // 使用 BaseResponse.paramValidationError 方法
        return BaseResponse.paramValidationError("参数校验失败", errors)
                .withPath(request.getRequestURI());
    }

    /**
     * 参数绑定异常（表单提交）
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Map<String, String>> handleBindException(
            BindException e, HttpServletRequest request) {

        Map<String, String> errors = buildValidationErrors(e);
        logValidationException("参数绑定失败", e, request, errors);

        // 使用 BaseResponse.paramValidationError 方法
        return BaseResponse.paramValidationError("参数绑定失败", errors)
                .withPath(request.getRequestURI());
    }

    /**
     * 参数校验异常（方法参数校验）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Map<String, String>> handleConstraintViolationException(
            ConstraintViolationException e, HttpServletRequest request) {

        Map<String, String> errors = buildConstraintViolationErrors(e);
        logValidationException("参数约束违反", e, request, errors);

        // 使用 BaseResponse.paramValidationError 方法
        return BaseResponse.paramValidationError("参数校验失败", errors)
                .withPath(request.getRequestURI());
    }

    // ==================== 业务异常（自定义） ====================

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        logBusinessException(e, request);
        return BaseResponse.fail(e.getCode(), e.getMessage())
                .withPath(request.getRequestURI())
                .withErrorDetail(e.getDetail());
    }

    // ==================== HTTP相关异常 ====================

    /**
     * 404异常处理
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseResponse<?> handleNotFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        logHttpException("资源不存在", e, request, HttpStatus.NOT_FOUND);
        return BaseResponse.fail(ResponseCode.NOT_FOUND, "请求的资源不存在: " + e.getRequestURL())
                .withPath(request.getRequestURI());
    }

    /**
     * 请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public BaseResponse<?> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        logHttpException("请求方法不支持", e, request, HttpStatus.METHOD_NOT_ALLOWED);
        return BaseResponse.fail(ResponseCode.METHOD_NOT_ALLOWED,
                        "请求方法 " + e.getMethod() + " 不支持")
                .withPath(request.getRequestURI());
    }

    /**
     * 文件上传过大异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException e, HttpServletRequest request) {
        logHttpException("文件过大", e, request, HttpStatus.BAD_REQUEST);
        return BaseResponse.fail(ResponseCode.FILE_TOO_LARGE, "文件大小超过限制")
                .withPath(request.getRequestURI());
    }

    /**
     * 请求参数缺失异常
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleMissingServletRequestPartException(
            MissingServletRequestPartException e, HttpServletRequest request) {
        logHttpException("请求参数缺失", e, request, HttpStatus.BAD_REQUEST);
        return BaseResponse.fail(ResponseCode.PARAM_ERROR,
                        "请求参数缺失: " + e.getRequestPartName())
                .withPath(request.getRequestURI());
    }

    // ==================== 系统异常处理 ====================

    /**
     * 非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest request) {
        logSystemException("非法参数", e, request, HttpStatus.BAD_REQUEST);
        return BaseResponse.fail(ResponseCode.PARAM_ERROR, e.getMessage())
                .withPath(request.getRequestURI());
    }

    /**
     * 权限拒绝异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseResponse<?> handleAccessDeniedException(
            AccessDeniedException e, HttpServletRequest request) {
        logSystemException("权限拒绝", e, request, HttpStatus.FORBIDDEN);
        return BaseResponse.fail(ResponseCode.FORBIDDEN, "权限不足")
                .withPath(request.getRequestURI());
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        logSystemException("运行时异常", e, request, HttpStatus.INTERNAL_SERVER_ERROR);
        return BaseResponse.fail(ResponseCode.BUSINESS_ERROR, e.getMessage())
                .withPath(request.getRequestURI());
    }

    /**
     * 处理所有异常（兜底处理）
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse<?> handleException(Exception e, HttpServletRequest request) {
        logSystemException("系统异常", e, request, HttpStatus.INTERNAL_SERVER_ERROR);

        // 生产环境下隐藏具体错误信息
        boolean isProduction = "prod".equals(System.getProperty("spring.profiles.active"));
        String message = isProduction ? "系统异常，请联系管理员" : e.getMessage();

        return BaseResponse.fail(ResponseCode.SYSTEM_ERROR, message)
                .withPath(request.getRequestURI());
    }

    // ==================== 辅助方法 ====================

    /**
     * 构建校验错误信息
     */
    private Map<String, String> buildValidationErrors(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    private Map<String, String> buildValidationErrors(BindException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    private Map<String, String> buildConstraintViolationErrors(ConstraintViolationException e) {
        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /**
     * 记录业务异常日志
     */
    private void logBusinessException(BusinessException e, HttpServletRequest request) {
        logException("业务异常", e, request, null, e.getCode());
    }

    /**
     * 记录参数校验异常日志
     */
    private void logValidationException(String type, Exception e, HttpServletRequest request,
                                        Map<String, String> errors) {
        String errorDetails = errors != null ? errors.toString() : "";
        logException(type, e, request, errorDetails, ResponseCode.PARAM_ERROR.getCode());
    }

    /**
     * 记录HTTP异常日志
     */
    private void logHttpException(String type, Exception e, HttpServletRequest request,
                                  HttpStatus status) {
        logException(type, e, request, null, status.value());
    }

    /**
     * 记录系统异常日志
     */
    private void logSystemException(String type, Exception e, HttpServletRequest request,
                                    HttpStatus status) {
        logException(type, e, request, null, status.value());
    }

    /**
     * 统一异常日志记录方法
     */
    private void logException(String type, Exception e, HttpServletRequest request,
                              String details, Integer errorCode) {
        try {
            // 设置MDC信息
            MdcUtil.setTraceId(generateTraceId());
            MdcUtil.setRequestInfo(
                    getClientIp(request),
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getQueryString(),
                    request.getHeader("User-Agent")
            );

            // 构建日志消息
            StringBuilder message = new StringBuilder()
                    .append("类型: ").append(type)
                    .append(" | 错误码: ").append(errorCode)
                    .append(" | 路径: ").append(request.getMethod()).append(" ").append(request.getRequestURI())
                    .append(" | IP: ").append(getClientIp(request))
                    .append(" | 消息: ").append(e.getMessage());

            if (details != null && !details.isEmpty()) {
                message.append(" | 详情: ").append(details);
            }

            // 根据异常类型选择日志级别
            if (type.contains("业务") || type.contains("参数") || type.contains("权限") ||
                    type.contains("资源不存在") || type.contains("请求方法") || type.contains("文件过大")) {
                exceptionLogger.warn(message.toString(), e);
            } else {
                exceptionLogger.error(message.toString(), e);
            }

        } finally {
            // 清除MDC
            MdcUtil.clear();
        }
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }

    /**
     * 生成追踪ID
     */
    private String generateTraceId() {
        // 优先从MDC获取，如果没有则生成新的
        String traceId = MdcUtil.getTraceId();
        if (traceId == null || traceId.isEmpty()) {
            traceId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }
        return traceId;
    }

    // ==================== 业务异常类 ====================

    /**
     * 业务异常类
     */
    public static class BusinessException extends RuntimeException {
        private final Integer code;
        private final Object detail;

        public BusinessException(String message) {
            super(message);
            this.code = ResponseCode.BUSINESS_ERROR.getCode();
            this.detail = null;
        }

        public BusinessException(String message, Object detail) {
            super(message);
            this.code = ResponseCode.BUSINESS_ERROR.getCode();
            this.detail = detail;
        }

        public BusinessException(Integer code, String message) {
            super(message);
            this.code = code;
            this.detail = null;
        }

        public BusinessException(Integer code, String message, Object detail) {
            super(message);
            this.code = code;
            this.detail = detail;
        }

        public BusinessException(String message, Throwable cause) {
            super(message, cause);
            this.code = ResponseCode.BUSINESS_ERROR.getCode();
            this.detail = null;
        }

        public Integer getCode() {
            return code;
        }

        public Object getDetail() {
            return detail;
        }
    }
}
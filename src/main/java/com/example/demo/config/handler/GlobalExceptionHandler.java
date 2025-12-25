package com.example.demo.config.handler;

import com.example.demo.util.exception.BusinessException;
import com.example.demo.util.response.BaseResponse;
import com.example.demo.util.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.ServletException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 处理自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return BaseResponse.fail(e.getCode(), e.getMessage());
    }

    // 2. 处理参数校验异常
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public BaseResponse<Void> handleValidException(Exception e) {
        String errorMsg = "参数校验失败";
        if (e instanceof MethodArgumentNotValidException) {
            errorMsg = ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
        } else if (e instanceof BindException) {
            errorMsg = ((BindException) e).getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
        }
        log.warn("参数校验异常: {}", errorMsg);
        return BaseResponse.fail(ResponseCode.PARAM_ERROR.getCode(), errorMsg);
    }

    // 3. 处理参数类型不匹配异常
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public BaseResponse<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("参数类型不匹配: {} = {}", e.getName(), e.getValue());
        return BaseResponse.fail(ResponseCode.PARAM_ERROR.getCode(), "参数格式错误: " + e.getName());
    }

    // 4. 处理请求方法不支持
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public BaseResponse<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持: {}", e.getMethod());
        return BaseResponse.fail(ResponseCode.METHOD_NOT_ALLOWED);
    }

    // 5. 处理权限不足
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseResponse<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return BaseResponse.fail(ResponseCode.FORBIDDEN);
    }

    // 6. 处理认证失败异常
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse<Void> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证失败: {}", e.getMessage());
        return BaseResponse.fail(ResponseCode.UNAUTHORIZED);
    }

    // 7. 处理 404
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseResponse<Void> handleNotFoundException(NoHandlerFoundException e) {
        log.warn("接口不存在: {} {}", e.getHttpMethod(), e.getRequestURL());
        return BaseResponse.fail(ResponseCode.NOT_FOUND);
    }

    // 8. 处理 Servlet 异常
    @ExceptionHandler(ServletException.class)
    public BaseResponse<Void> handleServletException(ServletException e) {
        log.error("Servlet异常: {}", e.getMessage(), e);
        return BaseResponse.fail(ResponseCode.SYSTEM_ERROR);
    }

    // 9. 兜底处理：处理所有未捕获的运行时异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse<Void> handleException(Exception e) {
        log.error("系统内部异常: ", e);
        return BaseResponse.fail(ResponseCode.SYSTEM_ERROR);
    }
}
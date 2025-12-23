package com.example.demo.util.global;

import com.example.demo.util.response.BaseResponse;
import com.example.demo.util.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有异常
     */
    @ExceptionHandler(Exception.class)
    public BaseResponse<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("全局异常: {} {}", request.getMethod(), request.getRequestURI(), e);

        // 根据不同异常类型返回不同错误码
        if (e instanceof IllegalArgumentException) {
            return BaseResponse.fail(ResponseCode.PARAM_ERROR, e.getMessage());
        }

        // 默认返回系统错误
        return BaseResponse.fail(ResponseCode.SYSTEM_ERROR);
    }
}

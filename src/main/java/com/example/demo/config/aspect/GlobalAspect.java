package com.example.demo.config.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class GlobalAspect {

    @Around("execution(* com.example.demo.controller..*.*(..))")
    public Object controllerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        log.info("请求开始: {} {}, 参数: {}", request.getMethod(), request.getRequestURI(), Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            long costTime = System.currentTimeMillis() - startTime;
            log.info("请求结束: {} {}, 耗时: {}ms", request.getMethod(), request.getRequestURI(), costTime);
            return result;
        } catch (Exception e) {
            // 【关键点】这里只记录日志，不要修改抛出的异常对象
            // 让异常继续抛出，交给 GlobalExceptionHandler 统一处理
            log.error("请求异常: {} {}, 异常类型: {}, 消息: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    e.getClass().getSimpleName(), // 只记录异常类名，避免堆栈污染日志查看
                    e.getMessage(),
                    e); // 这里传入 e，logback/error 会自动打印堆栈到文件/控制台
            throw e;
        }
    }
}
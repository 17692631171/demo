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

    /**
     * Controller层日志和异常处理
     */
    @Around("execution(* com.example.demo.controller..*.*(..))")
    public Object controllerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 记录请求日志
        log.info("请求开始: {} {}, 参数: {}",
                request.getMethod(),
                request.getRequestURI(),
                Arrays.toString(joinPoint.getArgs()));

        try {
            // 执行方法
            Object result = joinPoint.proceed();

            // 计算耗时
            long costTime = System.currentTimeMillis() - startTime;

            // 记录响应日志
            log.info("请求结束: {} {}, 耗时: {}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    costTime);

            return result;

        } catch (Exception e) {
            // 记录异常日志
            log.error("请求异常: {} {}, 异常: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    e.getMessage(), e);

            // 抛出异常，由@RestControllerAdvice处理
            throw e;
        }
    }
}
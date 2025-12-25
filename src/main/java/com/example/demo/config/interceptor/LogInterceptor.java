package com.example.demo.config.interceptor;

import com.example.demo.util.MdcUtil;
import com.example.demo.util.network.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        // 只设置MDC，不记录详细日志
        String traceId = MdcUtil.generateAndSetTraceId();
        String requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        MdcUtil.setRequestInfo(
                IpUtil.getClientIp(request),
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getHeader("User-Agent")
        );
        MdcUtil.setRequestId(requestId);

        request.setAttribute("startTime", System.currentTimeMillis());
        request.setAttribute("requestId", requestId);

        // 只记录重要信息
        if (log.isDebugEnabled()) {
            log.debug("请求开始 - URI: {}, 方法: {}, traceId: {}",
                    request.getRequestURI(), request.getMethod(), traceId);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        try {
            Long startTime = (Long) request.getAttribute("startTime");
            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;

                // 根据耗时选择日志级别
                if (duration > 3000) {
                    log.warn("慢请求 - URI: {}, 耗时: {}ms, 状态: {}",
                            request.getRequestURI(), duration, response.getStatus());
                } else if (duration > 1000) {
                    log.info("请求处理 - URI: {}, 耗时: {}ms",
                            request.getRequestURI(), duration);
                } else if (log.isDebugEnabled()) {
                    log.debug("请求完成 - URI: {}, 耗时: {}ms",
                            request.getRequestURI(), duration);
                }
            }
        } finally {
            MdcUtil.clear();
        }
    }
}
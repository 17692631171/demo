package com.example.demo.config.interceptor;

import com.example.demo.util.MdcUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

/**
 * 日志拦截器
 */
@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {

    private static final String REQUEST_START_TIME = "requestStartTime";
    private static final Logger requestLogger = LoggerFactory.getLogger("REQUEST_LOGGER");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {

        /**
         * preHandle方法是拦截器中的一个回调方法，在请求处理之前被调用。
         * @param request HttpServletRequest对象，包含请求信息
         * @param response HttpServletResponse对象，用于生成响应
         * @param handler 请求处理的方法处理器
         * @return 如果返回true，则继续流程；如果返回false，则中断流程
         */
        // 调试信息输出
        System.out.println("======= DEBUG: LogInterceptor.preHandle() 被调用 =======");
        System.out.println("请求URI: " + request.getRequestURI());
        System.out.println("请求方法: " + request.getMethod());

        // 生成请求ID和跟踪ID
        String requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String traceId = MdcUtil.generateAndSetTraceId();

        // 设置请求开始时间
        request.setAttribute(REQUEST_START_TIME, System.currentTimeMillis());
        request.setAttribute("requestId", requestId);

        // 设置响应头
        response.setHeader("X-Request-ID", requestId);
        response.setHeader("X-Trace-ID", traceId);

        // 获取客户端信息
        String clientIp = getClientIp(request);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String params = request.getQueryString() != null ? request.getQueryString() : "";
        String userAgent = request.getHeader("User-Agent");

        // 设置MDC信息
        MdcUtil.setRequestInfo(clientIp, method, uri, params, userAgent);
        MdcUtil.setRequestId(requestId);

        // 记录请求开始日志
        requestLogger.info("======= 请求开始 =======");
        requestLogger.info("请求ID: {}", requestId);
        requestLogger.info("跟踪ID: {}", traceId);
        requestLogger.info("客户端IP: {}", clientIp);
        requestLogger.info("请求方法: {}", method);
        requestLogger.info("请求URI: {}", uri);
        if (params.length() > 0) {
            requestLogger.info("请求参数: {}", params);
        }else{
            requestLogger.info("请求参数: 无");
        }
        if (userAgent != null) {
            requestLogger.info("User-Agent: {}", userAgent);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {
        // 可以在这里记录响应信息
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        try {
            Long startTime = (Long) request.getAttribute(REQUEST_START_TIME);
            String requestId = (String) request.getAttribute("requestId");

            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;

                // 记录请求完成信息
                requestLogger.info("请求耗时: {} ms", duration);
                requestLogger.info("响应状态: {}", response.getStatus());
                requestLogger.info("请求ID: {}", requestId);

                // 记录警告（如果请求耗时过长）
                if (duration > 3000) {
                    requestLogger.warn("请求处理时间过长 - URI: {}, 耗时: {} ms",
                            request.getRequestURI(), duration);
                }

                // 如果有异常，记录异常信息
                if (ex != null) {
                    requestLogger.error("请求处理异常: {}", ex.getMessage(), ex);
                }
            }

            requestLogger.info("======= 请求结束 =======\n");

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
}
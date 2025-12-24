package com.example.demo.util;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * MDC (Mapped Diagnostic Context) 工具类
 * 用于在日志中记录请求上下文信息
 */
public class MdcUtil {

    public static final String TRACE_ID = "traceId";
    public static final String IP = "ip";
    public static final String URI = "uri";
    public static final String METHOD = "method";
    public static final String PARAMS = "params";
    public static final String USER_AGENT = "userAgent";
    public static final String USER_ID = "userId";
    public static final String REQUEST_ID = "requestId";

    /**
     * 设置跟踪ID
     */
    public static void setTraceId(String traceId) {
        if (StringUtils.hasText(traceId)) {
            MDC.put(TRACE_ID, traceId);
        }
    }

    /**
     * 生成并设置跟踪ID
     */
    public static String generateAndSetTraceId() {
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        setTraceId(traceId);
        return traceId;
    }

    /**
     * 设置请求信息
     */
    public static void setRequestInfo(String ip, String method, String uri, String params, String userAgent) {
        if (StringUtils.hasText(ip)) {
            MDC.put(IP, ip);
        }
        if (StringUtils.hasText(method)) {
            MDC.put(METHOD, method);
        }
        if (StringUtils.hasText(uri)) {
            MDC.put(URI, uri);
        }
        if (StringUtils.hasText(params)) {
            MDC.put(PARAMS, params);
        }
        if (StringUtils.hasText(userAgent)) {
            MDC.put(USER_AGENT, userAgent);
        }
    }

    /**
     * 设置用户ID
     */
    public static void setUserId(String userId) {
        if (StringUtils.hasText(userId)) {
            MDC.put(USER_ID, userId);
        }
    }

    /**
     * 设置请求ID
     */
    public static void setRequestId(String requestId) {
        if (StringUtils.hasText(requestId)) {
            MDC.put(REQUEST_ID, requestId);
        }
    }

    /**
     * 获取跟踪ID
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    /**
     * 获取IP
     */
    public static String getIp() {
        return MDC.get(IP);
    }

    /**
     * 清除所有MDC信息
     */
    public static void clear() {
        MDC.clear();
    }

    /**
     * 清除指定key
     */
    public static void remove(String key) {
        MDC.remove(key);
    }
}
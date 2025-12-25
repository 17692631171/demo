package com.example.demo.util;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * MDC (Mapped Diagnostic Context) 工具类
 * 用于在日志中记录请求上下文信息
 * 该类提供了一系列静态方法，用于管理和操作MDC中的键值对
 * MDC是一种日志跟踪机制，可以在多线程环境下存储上下文信息
 */
public class MdcUtil {



    // 定义MDC中常用的键名常量
    public static final String TRACE_ID = "traceId";        // 跟踪ID，用于追踪请求链路
    public static final String IP = "ip";                   // 客户端IP地址
    public static final String URI = "uri";                 // 请求URI
    public static final String METHOD = "method";           // 请求方法
    public static final String PARAMS = "params";           // 请求参数
    public static final String USER_AGENT = "userAgent";    // 用户代理信息
    public static final String USER_ID = "userId";          // 用户ID
    public static final String REQUEST_ID = "requestId";    // 请求ID

    /**
     * 设置跟踪ID
     * @param traceId 跟踪ID字符串
     */
    public static void setTraceId(String traceId) {
        if (StringUtils.hasText(traceId)) {  // 检查字符串是否非空
            MDC.put(TRACE_ID, traceId);      // 将跟踪ID存入MDC
        }
    }

    /**
     * 生成并设置跟踪ID
     * 生成一个16位的随机字符串作为跟踪ID
     * @return 生成的跟踪ID
     */
    public static String generateAndSetTraceId() {
        // 生成UUID并去除连字符，取前16位作为跟踪ID
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        setTraceId(traceId);  // 设置生成的跟踪ID
        return traceId;       // 返回生成的跟踪ID
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
package com.example.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 */
@Slf4j
public class LogUtil {

    private static final Logger businessLogger = LoggerFactory.getLogger("BUSINESS_LOGGER");
    private static final Logger sqlLogger = LoggerFactory.getLogger("SQL_LOGGER");

    /**
     * 记录业务日志
     */
    public static void business(String module, String action, String message) {
        businessLogger.info("[{} | {}] {}", module, action, message);
    }

    /**
     * 记录业务日志（带数据）
     */
    public static void business(String module, String action, String message, Object data) {
        businessLogger.info("[{} | {}] {} - 数据: {}", module, action, message, data);
    }

    /**
     * 记录SQL日志
     */
    public static void sql(String sql, Object params) {
        sqlLogger.info("SQL: {} | 参数: {}", sql, params);
    }

    /**
     * 记录方法进入
     */
    public static void enter(String methodName, Object... params) {
        if (log.isDebugEnabled()) {
            log.debug(">>>> 进入方法: {}, 参数: {}", methodName, params);
        }
    }

    /**
     * 记录方法退出
     */
    public static void exit(String methodName, Object result) {
        if (log.isDebugEnabled()) {
            log.debug("<<<< 退出方法: {}, 结果: {}", methodName, result);
        }
    }

    /**
     * 记录性能日志
     */
    public static void performance(String operation, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        log.debug("[性能] 操作: {}, 耗时: {}ms", operation, duration);

        // 如果耗时超过阈值，记录警告
        if (duration > 1000) {
            log.warn("[慢操作] 操作: {}, 耗时: {}ms (超过1秒)", operation, duration);
        }
    }

    /**
     * 记录安全日志
     */
    public static void security(String action, String username, String ip, String result) {
        log.info("[安全] 操作: {}, 用户: {}, IP: {}, 结果: {}", action, username, ip, result);
    }

    /**
     * 记录异常（简化版）
     */
    public static void error(String message, Throwable e) {
        log.error("异常信息: {}", message, e);
    }

    /**
     * 记录请求信息
     */
    public static void request(String ip, String method, String uri, String params) {
        if (log.isDebugEnabled()) {
            log.debug("[请求] IP: {}, 方法: {}, URI: {}, 参数: {}", ip, method, uri, params);
        }
    }
}
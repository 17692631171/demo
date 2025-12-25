package com.example.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 * 提供统一的日志记录功能，包括业务日志、SQL日志、方法跟踪、性能监控等
 */
@Slf4j
public class LogUtil {

    // 业务日志记录器，用于记录业务相关的日志信息
    private static final Logger businessLogger = LoggerFactory.getLogger("BUSINESS_LOGGER");
    // SQL日志记录器，专门用于记录SQL语句及其参数
    private static final Logger sqlLogger = LoggerFactory.getLogger("SQL_LOGGER");

    /**
     * 记录业务日志
     *
     * @param module  模块名称
     * @param action  操作类型
     * @param message 日志消息
     */
    public static void business(String module, String action, String message) {
        businessLogger.info("[{} | {}] {}", module, action, message);
    }

    /**
     * 记录业务日志（带数据）
     *
     * @param module  模块名称
     * @param action  操作类型
     * @param message 日志消息
     * @param data    相关数据对象
     */
    public static void business(String module, String action, String message, Object data) {
        businessLogger.info("[{} | {}] {} - 数据: {}", module, action, message, data);
    }

    /**
     * 记录SQL日志
     *
     * @param sql    SQL语句
     * @param params SQL参数
     */
    public static void sql(String sql, Object params) {
        sqlLogger.info("SQL: {} | 参数: {}", sql, params);
    }

    /**
     * 记录方法进入日志
     *
     * @param methodName 方法名称
     * @param params     方法参数
     */
    public static void enter(String methodName, Object... params) {
        if (log.isDebugEnabled()) {
            log.debug(">>>> 进入方法: {}, 参数: {}", methodName, params);
        }
    }

    /**
     * 记录方法退出日志
     *
     * @param methodName 方法名称
     * @param result     方法返回结果
     */
    public static void exit(String methodName, Object result) {
        if (log.isDebugEnabled()) {
            log.debug("<<<< 退出方法: {}, 结果: {}", methodName, result);
        }
    }

    /**
     * 记录性能日志
     *
     * @param operation 操作名称
     * @param startTime 开始时间（毫秒级时间戳）
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
     * 该方法用于记录系统中的安全相关操作信息，包括用户操作行为、操作用户、IP地址及操作结果
     *
     * @param action   操作类型，描述用户执行的具体操作
     * @param username 执行操作的用户名
     * @param ip       执行操作的客户端IP地址
     * @param result   操作结果，通常表示成功或失败
     */
    public static void security(String action, String username, String ip, String result) { // 定义一个静态方法，用于记录安全日志
        log.info("[安全] 操作: {}, 用户: {}, IP: {}, 结果: {}", action, username, ip, result); // 使用日志框架输出格式化的安全日志信息
    }

    /**
     * 记录异常（简化版）
     * 该方法用于简化异常记录操作，通过接收异常信息和异常对象进行统一记录
     *
     * @param message 异常信息描述
     * @param e       异常对象，包含异常堆栈等详细信息
     */
    public static void error(String message, Throwable e) {
        log.error("异常信息: {}", message, e);
    }

    /**
     * 记录请求日志
     *
     * @param ip     客户端IP地址
     * @param method HTTP请求方法
     * @param uri    请求URI
     * @param params 请求参数
     */
    public static void request(String ip, String method, String uri, String params) {
        if (log.isDebugEnabled()) {
            log.debug("[请求] IP: {}, 方法: {}, URI: {}, 参数: {}", ip, method, uri, params);
        }
    }
}
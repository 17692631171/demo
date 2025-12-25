package com.example.demo.util.network;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

/**
 * IP工具类，用于获取客户端IP地址和判断IP是否为内网IP
 */
public class IpUtil {

    /**
     * 存储可能包含客户端IP的HTTP请求头名称数组
     * 这些头部在不同的代理服务器或负载均衡器中可能使用不同的名称来传递客户端真实IP
     */
    private static final String[] IP_HEADERS = {
            "X-Forwarded-For",        // Squid代理
            "X-Real-IP",              // Nginx代理
            "Proxy-Client-IP",        // Apache代理
            "WL-Proxy-Client-IP",     // WebLogic代理
            "HTTP_CLIENT_IP",         // 一些代理服务器
            "HTTP_X_FORWARDED_FOR"    // 标准的代理头部
    };

    /**
     * 获取客户端真实IP地址
     * @param request HttpServletRequest对象
     * @return 客户端IP地址字符串，如果request为null则返回"unknown"
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        // 遍历所有可能的IP请求头，获取第一个有效的IP地址
        for (String header : IP_HEADERS) {
            String ip = request.getHeader(header);
            // 检查IP是否有效（非空且不是unknown）
            if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
                // 处理可能存在的多个IP地址情况（如：X-Forwarded-For: client, proxy1, proxy2）
                // 多次反向代理后会有多个IP值，第一个为真实IP
                int index = ip.indexOf(',');
                if (index != -1) {
                    return ip.substring(0, index).trim();  // 返回第一个IP（真实IP）
                }
                return ip.trim();  // 返回单个IP
            }
        }

        // 如果没有找到任何代理头部，则使用远程地址
        return request.getRemoteAddr();
    }

    /**
     * 判断IP是否为内网IP
     * @param ip 要检查的IP地址
     * @return 如果是内网IP返回true，否则返回false
     */
    public static boolean isInternalIp(String ip) {
        // 检查输入IP是否为null或空字符串
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        // 检查常见的内网IP段，包括IPv4和IPv6的本地回环地址
        return ip.startsWith("192.168.") ||      // 私有IP段A类，通常用于家庭和小型网络
                ip.startsWith("10.") ||          // 私有IP段B
                ip.startsWith("172.16.") ||      // 私有IP段C（172.16.0.0-172.31.255.255）
                ip.startsWith("127.0.0.1") ||    // 本地回环地址
                ip.equals("0:0:0:0:0:0:0:1");    // IPv6的本地回环地址
    }
}
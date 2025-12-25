package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS跨域资源共享配置类
 * 用于配置Spring应用程序的跨域访问策略
 */
@Configuration
public class CorsConfig {

    /**
     * 允许的跨域源列表
     * 从配置文件中读取app.cors.allowed-origins属性，默认值为"*"
     */
    @Value("${app.cors.allowed-origins:*}")
    private List<String> allowedOrigins;

    /**
     * 允许的HTTP方法列表
     * 从配置文件中读取app.cors.allowed-methods属性，默认值为GET,POST,PUT,DELETE,OPTIONS
     */
    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private List<String> allowedMethods;

    /**
     * 允许的请求头列表
     * 从配置文件中读取app.cors.allowed-headers属性，默认值为"*"
     */
    @Value("${app.cors.allowed-headers:*}")
    private List<String> allowedHeaders;

    /**
     * 是否允许发送凭据信息（如cookies）
     * 从配置文件中读取app.cors.allow-credentials属性，默认值为true
     */
    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    /**
     * 预检请求的有效期，单位秒
     * 从配置文件中读取app.cors.max-age属性，默认值为3600
     */
    @Value("${app.cors.max-age:3600}")
    private Long maxAge;

    /**
     * 创建并返回CorsConfigurationSource Bean
     * 用于配置全局CORS策略，对所有的URL路径应用相同的CORS配置
     *
     * @return 配置好的CorsConfigurationSource实例
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // 创建CORS配置对象
        CorsConfiguration configuration = new CorsConfiguration();
        // 设置允许的跨域源
        configuration.setAllowedOrigins(allowedOrigins);
        // 设置允许的HTTP方法
        configuration.setAllowedMethods(allowedMethods);
        // 设置允许的请求头
        configuration.setAllowedHeaders(allowedHeaders);
        // 设置是否允许发送凭据信息
        configuration.setAllowCredentials(allowCredentials);
        // 设置预检请求的有效期
        configuration.setMaxAge(maxAge);

        // 创建基于URL的CORS配置源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 注册CORS配置，应用于所有路径（/**）
        source.registerCorsConfiguration("/**", configuration);
        // 返回配置完成的CORS源
        return source;
    }
}
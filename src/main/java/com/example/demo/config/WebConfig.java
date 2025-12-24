package com.example.demo.config;

import com.example.demo.config.interceptor.LogInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LogInterceptor logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        System.out.println("======= DEBUG: 注册 LogInterceptor =======");

        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns(
                        "/favicon.ico",
                        "/error",
                        "/static/**"
                )
                .order(1);  // 设置执行顺序
    }
}
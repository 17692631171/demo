package com.example.demo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring Boot应用程序的主启动类
 * 该类使用@SpringBootApplication注解标记，表示这是一个Spring Boot应用
 * @EnableTransactionManagement启用Spring的事务管理功能
 * @EnableConfigurationProperties启用配置属性的支持
 */
@SpringBootApplication(scanBasePackages = "com.example.demo")
@EnableTransactionManagement
@EnableConfigurationProperties
@OpenAPIDefinition(
        info = @Info(
                title = "Demo API",
                version = "1.0.0",
                description = "Demo项目API文档"
        )
)
@ServletComponentScan
public class Demo1Application {
    /**
     * 程序的主入口方法
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 使用SpringApplication.run()方法启动Spring Boot应用
        SpringApplication.run(Demo1Application.class, args);
    }
}
package com.example.demo.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson配置类
 * 用于配置ObjectMapper实例，提供JSON序列化和反序列化的定制化配置
 */
@Configuration
public class JacksonConfig {

    /**
     * 创建并配置ObjectMapper Bean
     * ObjectMapper是Jackson库的核心类，用于处理JSON数据的读写操作
     *
     * @return 配置好的ObjectMapper实例
     */
    @Bean
    public ObjectMapper objectMapper() {
        // 创建ObjectMapper实例
        ObjectMapper objectMapper = new ObjectMapper();

        // 配置序列化相关特性
        // 禁止将日期序列化为时间戳格式，而是使用自定义的日期格式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 禁止在序列化遇到空Bean时抛出异常
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // 配置反序列化相关特性
        // 允许反序列化时忽略未知属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 禁止在反序列化时将null值映射到基本类型
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

        // 注册Java 8时间模块，以支持Java 8引入的新时间API
        objectMapper.registerModule(new JavaTimeModule());

        // 设置日期格式
        objectMapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        return objectMapper;
    }
}

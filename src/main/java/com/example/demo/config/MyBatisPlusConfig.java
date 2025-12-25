package com.example.demo.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus配置类
 * 用于配置MyBatis-Plus的各种插件和自动填充功能
 */
@Configuration
public class MyBatisPlusConfig implements MetaObjectHandler {

    /**
     * 配置MyBatis-Plus的插件
     * @return MybatisPlusInterceptor MyBatis-Plus插件拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInterceptor.setMaxLimit(1000L);
        paginationInterceptor.setOverflow(true);
        interceptor.addInnerInterceptor(paginationInterceptor);

        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 防止全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "createBy", Long.class, getCurrentUserId());
        this.strictInsertFill(metaObject, "updateBy", Long.class, getCurrentUserId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", Long.class, getCurrentUserId());
    }

/**
 * 获取当前登录用户的ID
 * 该方法用于从Spring Security的安全上下文中获取当前用户的ID
 * 
 * @return Long 返回当前用户的ID，如果用户未登录则返回0L
 *         注意：当前实现为临时方案，返回固定值0
 *         在实际项目中应该从认证对象中获取真实用户ID
 */
    private Long getCurrentUserId() {
        // 从SecurityContext获取当前用户ID的代码被注释掉
        // 原本实现是通过Spring Security的安全上下文获取认证信息
        // 然后从UserDetails对象中提取用户ID
        // return SecurityContextHolder.getContext().getAuthentication() != null ?
        //        ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId() : 0L;
        return 0L; // 暂时返回0，实际项目中需要实现
    }
}
package com.example.demo.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 插件配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 分页插件（必须放在第一位）
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        paginationInterceptor.setDbType(DbType.MYSQL); // 数据库类型
        paginationInterceptor.setMaxLimit(1000L); // 单页最大记录数
        paginationInterceptor.setOverflow(true); // 超过最大页数后回到首页
        interceptor.addInnerInterceptor(paginationInterceptor);

        // 2. 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 3. 防止全表更新/删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        // 4. 多租户插件（如果需要）
        // interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
        //     @Override
        //     public Expression getTenantId() {
        //         // 返回租户ID
        //         return new StringValue(TenantContext.getCurrentTenantId());
        //     }
        //
        //     @Override
        //     public String getTenantIdColumn() {
        //         return "tenant_id";
        //     }
        //
        //     @Override
        //     public boolean ignoreTable(String tableName) {
        //         // 忽略不需要租户隔离的表
        //         return "sys_tenant".equals(tableName);
        //     }
        // }));

        return interceptor;
    }
}
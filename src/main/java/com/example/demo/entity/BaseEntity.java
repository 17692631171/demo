package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * MyBatis-Plus 基础实体类
 * 所有实体类应继承此类，获得通用字段和功能
 */
@Data
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==================== 主键字段 ====================
    /**
     * 主键ID
     * @TableId 注解指定主键和生成策略
     * type = IdType.ASSIGN_ID: 雪花算法生成ID（分布式ID）
     * type = IdType.AUTO: 数据库自增（单机推荐）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    // ==================== 时间字段 ====================
    /**
     * 创建时间
     * @TableField 的 fill 属性指定自动填充策略
     * FieldFill.INSERT: 只在插入时填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     * FieldFill.INSERT_UPDATE: 插入和更新时都填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // ==================== 操作人字段 ====================
    /**
     * 创建人ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 更新人ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    // ==================== 逻辑删除字段 ====================
    /**
     * 逻辑删除标志
     * @TableLogic 注解启用逻辑删除
     * 默认值: 0-未删除, 1-已删除
     */
    @TableLogic
    private Integer deleted = 0;

    // ==================== 乐观锁字段 ====================
    /**
     * 版本号（乐观锁）
     * @Version 注解启用乐观锁
     * 更新时会自动+1，如果版本不匹配则更新失败
     */
    @Version
    private Integer version = 0;

    // ==================== 其他字段（可选）====================
    /**
     * 租户ID（多租户系统）
     * 如果启用多租户，需要添加此字段
     */
    // @TableField(fill = FieldFill.INSERT)
    // private String tenantId;

    /**
     * 备注
     */
    private String remark;

    // ==================== 业务方法 ====================
    /**
     * 判断是否为新建实体（未持久化）
     * @return true-新实体，false-已持久化
     */
    public boolean isNew() {
        return this.id == null;
    }

    /**
     * 逻辑删除方法
     */
    public void markAsDeleted() {
        this.deleted = 1;
    }

    /**
     * 恢复删除方法
     */
    public void markAsActive() {
        this.deleted = 0;
    }
}
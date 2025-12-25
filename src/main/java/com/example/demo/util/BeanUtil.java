package com.example.demo.util;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean 工具类
 * 提供对象属性复制的方法，支持单个对象和列表的复制操作
 */
public class BeanUtil {

    /**
     * 复制对象属性
     * 将源对象的属性值复制到目标对象的新实例中
     *
     * @param source      源对象，不能为null
     * @param targetClass 目标对象的Class对象，用于创建新实例
     * @param <T>         目标对象的类型
     * @return 返回复制后的新对象实例，如果源对象为null则返回null
     * @throws RuntimeException 如果对象复制过程中发生异常
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            // 使用新的方式创建实例，避免使用过时的 newInstance() 方法
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("对象复制失败: " + e.getMessage(), e);
        }
    }

    /**
     * 复制对象列表
     * 将源列表中的每个元素复制为目标类型的新列表
     *
     * @param sourceList  源对象列表，不能为null
     * @param targetClass 目标对象的Class对象，用于创建新实例
     * @param <T>         目标对象的类型
     * @return 返回复制后的新列表，如果源列表为null则返回null
     */
    public static <T> List<T> copyList(List<?> sourceList, Class<T> targetClass) {
        if (sourceList == null) {
            return null;
        }
        // 创建新列表，初始容量与源列表相同，避免扩容操作
        List<T> targetList = new ArrayList<>(sourceList.size());
        // 遍历源列表，复制每个元素
        for (Object source : sourceList) {
            targetList.add(copyProperties(source, targetClass));
        }
        return targetList;
    }

    /**
     * 安全的复制属性方法（避免异常）
     * 如果复制失败，返回null
     */
    public static <T> T copyPropertiesSafely(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            return copyProperties(source, targetClass);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 复制属性并忽略特定字段
     */
    public static <T> T copyPropertiesIgnoreFields(Object source, Class<T> targetClass, String... ignoreFields) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target, ignoreFields);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("对象复制失败: " + e.getMessage(), e);
        }
    }

    /**
     * 判断两个对象是否属性相同
     */
    public static boolean isPropertiesEqual(Object source, Object target, String... excludeFields) {
        if (source == null && target == null) {
            return true;
        }
        if (source == null || target == null) {
            return false;
        }
        if (!source.getClass().equals(target.getClass())) {
            return false;
        }

        // 这里简化实现，实际可能需要更复杂的比较逻辑
        // 可以使用反射比较每个字段，或使用序列化后比较字符串
        try {
            Object temp = source.getClass().getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, temp);
            // 实际项目中可以使用更精确的比较方式
            return temp.equals(target);
        } catch (Exception e) {
            return false;
        }
    }
}
package com.example.demo.util;

import org.springframework.beans.BeanUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean 工具类
 */
public class BeanUtil {

    /**
     * 复制对象属性
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("对象复制失败", e);
        }
    }

    /**
     * 复制对象列表
     */
    public static <T> List<T> copyList(List<?> sourceList, Class<T> targetClass) {
        if (sourceList == null) {
            return null;
        }
        List<T> targetList = new ArrayList<>(sourceList.size());
        for (Object source : sourceList) {
            targetList.add(copyProperties(source, targetClass));
        }
        return targetList;
    }
}
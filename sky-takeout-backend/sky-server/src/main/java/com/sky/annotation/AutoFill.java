package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解用于：标识需要进行公共字段填充逻辑处理的方法
 */
@Target(ElementType.METHOD) // 指定注解要加载方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    // 指定当前数据库操作的类型 UPDATE、INSERT
    OperationType value();
}

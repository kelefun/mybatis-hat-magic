package com.jedijava.mybatis.hat.annotation;

import com.jedijava.mybatis.hat.enums.DatabaseType;

import java.lang.annotation.*;

/**
 * 序列主键策略
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface KeySequence {

    /**
     * 序列名
     */
    String value() default "";

    /**
     * 数据库类型，未配置默认使用注入 IKeyGenerator 实现，多个实现必须指定
     */
    DatabaseType dbType() default DatabaseType.UNKNOWN;
}

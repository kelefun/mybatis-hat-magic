package com.jedijava.mybatis.hat.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liukaiyang
 * @since 2019/9/9 14:01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HatField {
    /**
     * 查询/新增/更新 通过对象构建sql时是否忽略此字段
     */
    boolean ignore() default false;

    /**
     * 数据库别名
     */
    boolean name() default false;

    /**
     * 新增/更新 时的默认值
     * @return
     */
    String defaultValue();
}

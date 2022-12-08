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
@Target(ElementType.TYPE)
public @interface HatTable {
    /**
     * 数据表的名称
     */
    String value();
}

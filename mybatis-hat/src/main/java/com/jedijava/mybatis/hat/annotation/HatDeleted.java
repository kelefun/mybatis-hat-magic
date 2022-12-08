package com.jedijava.mybatis.hat.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记字段是否为逻辑删除
 * (避免使用此注解,尽量使用deleted 作为默认的标记字段) {@link com.jedijava.mybatis.hat.face.entity.BaseEntity}#deleted
 * @author liukaiyang
 * @since 2019/9/9 14:01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HatDeleted {
}

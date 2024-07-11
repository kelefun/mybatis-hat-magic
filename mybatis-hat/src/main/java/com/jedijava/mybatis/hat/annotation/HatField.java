package com.jedijava.mybatis.hat.annotation;

import com.jedijava.mybatis.hat.enums.FieldFill;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

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
    FieldFill fill() default FieldFill.DEFAULT;
    JdbcType jdbcType() default JdbcType.UNDEFINED;
    /**
     * 新增/更新 时的默认值
     * @return
     */
//    String defaultValue();

    /**
     * 类型处理器 (该默认值不代表会按照该值生效),
     * <p>
     * {@link ResultMapping#typeHandler} and {@link ParameterMapping#typeHandler}
     *
     * @since 3.1.2
     */
    Class<? extends TypeHandler> typeHandler() default UnknownTypeHandler.class;
}

package com.jedijava.mybatis.hat.sql;

import java.util.function.Consumer;

/**
 * @author liukaiyang
 * @since 2019/9/6 16:12
 */
@FunctionalInterface
public interface QuerySql extends Consumer<SqlBuilder> {
}

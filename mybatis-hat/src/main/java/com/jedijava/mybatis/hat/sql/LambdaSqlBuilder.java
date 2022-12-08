package com.jedijava.mybatis.hat.sql;

import com.jedijava.mybatis.hat.face.entity.QueryEntity;
import com.jedijava.mybatis.hat.override.HatFunction;
import com.jedijava.mybatis.hat.utils.Lambdas;

/**
 * sql构建器
 *
 * @author liukaiyang
 * @since 2019/9/6 14:23
 */
public class LambdaSqlBuilder<Q extends QueryEntity> {
    SqlBuilder sqlBuilder;
    Q queryEntity;

    public LambdaSqlBuilder(Q q, SqlBuilder sqlBuilder) {
        this.queryEntity = q;
        this.sqlBuilder = sqlBuilder;
    }

    public SqlBuilder normal() {
        return sqlBuilder;
    }

    public LambdaSqlBuilder<Q> gt(HatFunction<Q, Object> function) {
        Object value = function.apply(queryEntity);
        String columnName=Lambdas.fnToName(function);
        sqlBuilder.gt(columnName,value);
        return getThis();
    }
    public LambdaSqlBuilder<Q> eq(HatFunction<Q, Object> function) {
        Object value = function.apply(queryEntity);
        String columnName=Lambdas.fnToName(function);
        sqlBuilder.eq(columnName,value);
        return getThis();
    }

    protected LambdaSqlBuilder<Q> getThis() {
        return this;
    }
}

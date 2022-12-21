package com.jedijava.mybatis.hat.sql;

import com.jedijava.mybatis.hat.face.entity.QueryEntity;
import com.jedijava.mybatis.hat.override.HatFunction;
import com.jedijava.mybatis.hat.utils.HatStringUtil;
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

    public void page(int pageIndex, int pageSize) {
        sqlBuilder.page(pageIndex, pageSize);
    }

    public LambdaSqlBuilder<Q> gt(HatFunction<Q, Object> function) {
        Object value = function.apply(queryEntity);
        String columnName = Lambdas.fnToName(function);
        sqlBuilder.gt(columnName, value);
        return getLambda();
    }

    /**
     * query.eq(Entity::getName)
     * 如果getName==null,则跳过
     *
     * @param function
     * @return
     */
    public LambdaSqlBuilder<Q> eq(HatFunction<Q, Object> function) {
      return  eq(true,function);
    }

    /**
     * query.eq(StringUtil.isNotBlank(name),Entity::getName)
     * @param handleBlank true ：属性等于 null或""或" "时则跳过，false 属性可以为 “”" "，但是不管true和false都不能是null,如果想查询为null的字段，请使用isNull或 notNull
     * @param function
     * @return
     */
    public LambdaSqlBuilder<Q> eq(boolean handleBlank, HatFunction<Q, Object> function) {
        Object value = function.apply(queryEntity);
        if (handleBlank && HatStringUtil.isBlank(value)) {
            return getLambda();
        }
        String columnName = Lambdas.fnToName(function);
        sqlBuilder.eq(columnName, value);
        return getLambda();
    }

    protected LambdaSqlBuilder<Q> getLambda() {
        return this;
    }
}

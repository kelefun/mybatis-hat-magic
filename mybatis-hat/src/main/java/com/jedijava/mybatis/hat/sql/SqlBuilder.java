package com.jedijava.mybatis.hat.sql;

import com.google.common.collect.Lists;
import com.jedijava.mybatis.hat.constants.SymbolConst;
import com.jedijava.mybatis.hat.enums.SqlKeyword;
import com.jedijava.mybatis.hat.face.entity.QueryEntity;
import com.jedijava.mybatis.hat.utils.HatStringUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author liukaiyang
 * @since 2019/9/6 16:12
 */
public abstract class SqlBuilder extends WhereClauseBuilder<SqlBuilder> {

    private String columnClause;
    private List<String> orderClause;
    private List<String> groupClause;
    private String havingClause;
    private LambdaSqlBuilder lsBuilder;
    /**
     * 查询数据的条数，本项目中一般取值于pageSize
     */
    protected Integer limit;
    /**
     * 查询数据偏移量，即从第几条数据开始查 ，一般情况下等于 (pageIndex-1)*pageSize
     */
    protected Integer offset;

    public SqlBuilder select(String... columns) {
        if (columns != null && columns.length > 0) {
            columnClause = String.join(",", columns);
        }
        return getThis();
    }

    public <Q extends QueryEntity> LambdaSqlBuilder<Q> lambda(Q queryEntity) {
        if(Objects.nonNull(lsBuilder)){
            return lsBuilder;
        }
        lsBuilder=new LambdaSqlBuilder<>(queryEntity, getThis());
        return lsBuilder;
    }

    // TODO 后期再考虑是否加入这些复杂的查询
//    public QuerySqlBuilder join() {
//        return getThis();
//    }
//
//    public QuerySqlBuilder leftJoin() {
//        return getThis();
//    }
//
//    public QuerySqlBuilder rightJoin() {
//        return getThis();
//    }
//
//    public QuerySqlBuilder union() {
//        return getThis();
//    }
//
//    public QuerySqlBuilder unionAll() {
//        return getThis();
//    }
//
//    public QuerySqlBuilder unionDistinct() {
//        return getThis();
//    }
//
    public SqlBuilder group(String columnName, String... columnNames) {
        if (groupClause == null) {
            groupClause = Lists.newLinkedList();
        }
        groupClause.add("@[" + columnName + "] ");
        if (columnNames != null && columnNames.length > 0) {
            for (String col : columnNames) {
                groupClause.add(", @[" + col + "] ");
            }
        }
        return getThis();

    }

    /**
     * @param expression 示例: SUM(@[salary])>15000
     * @return
     */
    public SqlBuilder having(String expression) {
        if (HatStringUtil.isNotEmpty(expression)) {
            havingClause = expression;
        }
        return getThis();
    }

    /**
     * @param columnName 字段名称
     * @param order      排序方式 {@link com.jedijava.mybatis.hat.constants.Order}
     * @return
     */
    public SqlBuilder order(String columnName, String order) {
        if (orderClause == null) {
            orderClause = Lists.newLinkedList();
        }
        orderClause.add(" @[" + columnName + "] " + order);
        return getThis();
    }

    public void page(int pageIndex, int pageSize) {
        this.offset = pageSize * (pageIndex - 1);
        this.limit = pageSize;
    }

    public void limit(Integer limit) {
        this.limit = limit;
    }

    /**
     * @param offset offset = pageSize* (pageIndex-1)
     */
    public void offset(Integer offset) {
        this.offset = offset;
    }


    String getGroupBy() {
        if (groupClause == null) {
            return "";
        }
        return SqlKeyword.GROUP.getVal() + " " +String.join(SymbolConst.COMMA, groupClause);
    }


    String getHaving() {
        if (HatStringUtil.isEmpty(havingClause)) {
            return "";
        }
        return SqlKeyword.HAVING.getVal() + " " + havingClause;
    }

    String getOrderBy() {
        if (orderClause == null) {
            return "";
        }
        return SqlKeyword.ORDER.getVal() + " " + String.join(SymbolConst.COMMA, orderClause);
    }

    protected abstract String getLimit();


    String getColumn() {
        return this.columnClause;
    }

    @Override
    protected SqlBuilder getThis() {
        return this;
    }
}

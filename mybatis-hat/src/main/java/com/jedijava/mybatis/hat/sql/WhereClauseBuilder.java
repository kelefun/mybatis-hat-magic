package com.jedijava.mybatis.hat.sql;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jedijava.mybatis.hat.enums.SqlKeyword;
import com.jedijava.mybatis.hat.exception.SqlException;
import com.jedijava.mybatis.hat.parsing.NameTokenHandler;
import com.jedijava.mybatis.hat.utils.HatStringUtil;
import org.apache.ibatis.parsing.GenericTokenParser;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * sql构建器
 *
 * @author liukaiyang
 * @since 2019/9/6 14:23
 */
public abstract class WhereClauseBuilder<T extends WhereClauseBuilder<T>> {

    protected abstract T getThis();

    private String sqlSegment;
    //最近一次操作是否为  or
    protected boolean or = false;

    private Map<String, Object> paramMap = Maps.newConcurrentMap();

    Map<String, Object> getParamMap() {
        return this.paramMap;
    }

    private Set<String> columnList = Sets.newHashSet();

    /**
     * 加括号的sql
     * 备注: 字段名称最好用 @[] 标记起来,便于解析对象属性与数据库字段的对应关系.
     *
     * @param whereClause 示例1:  id=#{id} or account_type = #{accountType}
     *                    示例2:  @[id]=#{id} or @[accountType]=#{accountType}
     * @param args        占位符的值(变量数目要与占位符数目一致
     */
    public T parentheses(String whereClause, Object... args) {
        if (HatStringUtil.isEmpty(whereClause) || whereClause.contains("$")) {
            throw new SqlException("mybatis-hat 暂不支持 $ 操作符");
        }
        NameTokenHandler tokenHandler = new NameTokenHandler();
        GenericTokenParser parser = new GenericTokenParser("#{", "}", tokenHandler);
        parser.parse(whereClause);

        List<String> nameList = tokenHandler.getNameList();
        for (int i = 1; i < nameList.size(); i++) {
            if (i <= args.length) {
                addValue(nameList.get(i), args[i]);
            }
        }
        addWhereClause(whereClause);
        return getThis();
    }

    /**
     * 最近的一个操作变为 or
     *
     * @return
     */
    public T or() {
        or = true;
        return getThis();
    }

    /**
     * greater than / 大于
     *
     * @param value 占位符变量的值
     */
    public T gt(String columnName, Object value) {
        addSqlSegment(columnName, SqlKeyword.GREATER_THAN, value);
        return getThis();
    }

    /**
     * greater or equals / 大于等于
     */
    public T ge(String columnName, Object value) {
        addSqlSegment(columnName, SqlKeyword.GREATER_EQUAL, value);
        return getThis();
    }

    /**
     * less than / 小于
     */
    public T lt(String columnName, Object value) {
        addSqlSegment(columnName, SqlKeyword.LESS_THAN, value);
        return getThis();
    }

    /**
     * less or equals / 小于等于
     */
    public T le(String columnName, Object value) {
        addSqlSegment(columnName, SqlKeyword.LESS_EQUAL, value);
        return getThis();
    }

    /**
     * equals / 等于
     */
    public T eq(String columnName, Object value) {
        addSqlSegment(columnName, SqlKeyword.EQUAL, value);
        return getThis();
    }


    /**
     * not equals / 不等于
     */
    public T notEq(String columnName, Object value) {
        addSqlSegment(columnName, SqlKeyword.NOT_EQUAL, value);
        return getThis();
    }

    public T like(String columnName, Object value) {
        addSqlSegment(columnName, SqlKeyword.LIKE, value);
        return getThis();
    }

    public T notLike(String columnName, Object value) {
        addSqlSegment(columnName, SqlKeyword.NOT_LIKE, value);
        return getThis();
    }

    /**
     * is <code>null</code> /为 null
     */
    public T isNull(String columnName) {
        addSqlSegmentWithout(columnName, SqlKeyword.IS_NULL);
        return getThis();
    }

    public T notNull(String columnName) {
        addSqlSegmentWithout(columnName, SqlKeyword.NOT_NULL);
        return getThis();
    }

    public T in(String columnName, List<?> valueList) {
        StringBuilder sb = new StringBuilder("@[").append(columnName).append("] IN ( ");
        int size = valueList.size();
        for (int i = 0; i < size; i++) {
            sb.append("#{").append(columnName).append(i).append('}');
            addValue(columnName + i, valueList.get(i));
            if (i + 1 != size) {
                sb.append(',');
            }
        }
        sb.append(')');
        addWhereClause(sb.toString());
        return getThis();
    }

    public T notIn(String columnName, List<?> valueList) {
        if (valueList == null || valueList.isEmpty()) {
            return getThis();
        }
        StringBuilder sb = new StringBuilder("@[").append(columnName).append("] NOT IN ( ");
        int size = valueList.size();
        for (int i = 0; i < size; i++) {
            sb.append("#{").append(columnName).append(i).append('}');
            addValue(columnName + i, valueList.get(i));
            if (i + 1 != size) {
                sb.append(',');
            }
        }
        sb.append(')');
        addWhereClause(sb.toString());
        return getThis();
    }

    public T between(String columnName, Object start, Object end) {
        String startPlaceholder = columnName + "BetweenStart";
        String endPlaceholder = columnName + "BetweenEnd";
        String sb = "@[" + columnName + "] BETWEEN #{" + startPlaceholder + "} AND #{" + endPlaceholder + "} ";
        addWhereClause(sb);
        addValue(startPlaceholder, start);
        addValue(endPlaceholder, end);
        return getThis();
    }

    public T notBetween(String columnName, Object start, Object end) {
        String startPlaceholder = columnName + "NotBetweenStart";
        String endPlaceholder = columnName + "NotBetweenEnd";
        String sb = "@[" + columnName + "] NOT BETWEEN #{" + startPlaceholder + "} AND #{" + endPlaceholder + "} ";
        addWhereClause(sb);
        addValue(startPlaceholder, start);
        addValue(endPlaceholder, end);
        return getThis();
    }

    private void addSqlSegment(String columnName, SqlKeyword keyword, Object value) {
        if (value == null) return;
        addValue(columnName, value);
        String sql = "@[" + columnName + "] " + keyword.getVal() + " #{" + columnName + "} ";
        addWhereClause(sql);
    }

    /**
     * 没有占位符的查询条件
     *
     * @param columnName
     * @param keyword
     */
    private void addSqlSegmentWithout(String columnName, SqlKeyword keyword) {
        String sql = "@[" + columnName + "]  " + keyword.getVal() + " ";
        addWhereClause(sql);
    }

    private void addWhereClause(String whereClause) {
        if (columnList.contains(whereClause)) {
            return;
        }
        columnList.add(whereClause);
        String prefix = or ? SqlKeyword.OR.getVal() : SqlKeyword.AND.getVal();
        if (sqlSegment != null) {
            sqlSegment = sqlSegment + " " + prefix + " " + whereClause;
        } else {
            sqlSegment = whereClause;
        }
        resetOr();
    }


    private void addValue(String key, Object value) {
        paramMap.putIfAbsent(key, value);
    }

    private void resetOr() {
        if (or) {
            or = false;
        }
    }

    @Override
    public String toString() {
        if (HatStringUtil.isEmpty(sqlSegment)) {
            return " 1=1 ";
        }
        return sqlSegment;
    }


}

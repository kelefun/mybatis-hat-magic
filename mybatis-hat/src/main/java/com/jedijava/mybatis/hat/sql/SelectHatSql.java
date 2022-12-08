package com.jedijava.mybatis.hat.sql;

import com.google.common.collect.Lists;
import com.jedijava.mybatis.hat.annotation.SelectKeyEntity;
import com.jedijava.mybatis.hat.enums.SqlKeyword;
import com.jedijava.mybatis.hat.exception.ConfigException;
import com.jedijava.mybatis.hat.exception.UnsupportedException;
import com.jedijava.mybatis.hat.meta.TableColumn;
import com.jedijava.mybatis.hat.meta.TableSchema;
import com.jedijava.mybatis.hat.parsing.ColumnTokenHandler;
import com.jedijava.mybatis.hat.sql.hsql.HsqlBuilder;
import com.jedijava.mybatis.hat.sql.mysql.MysqlBuilder;
import com.jedijava.mybatis.hat.sql.pgsql.PgsqlBuilder;
import com.jedijava.mybatis.hat.utils.HatStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.parsing.GenericTokenParser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
 *
 * @author liukaiyang
 * @since 2019/9/26 13:49
 */
@Slf4j
public class SelectHatSql implements HatSql {

    private TableSchema tableSchema;
    private boolean countQuery;
    private Class<?> tableClazz;
    private Map<String, Object> extParamMap;

    public SelectHatSql(TableSchema tableSchema, Class<?> tableClazz, boolean countQuery) {
        this.tableSchema = tableSchema;
        this.countQuery = countQuery;
        this.tableClazz = tableClazz;
    }

    @Override
    public String getSql(Object parameterObject) {
        String tableName = tableSchema.getTableName();
        StringBuilder sb = new StringBuilder(SqlKeyword.SELECT.getVal());
        if (parameterObject instanceof QuerySql) {
            QuerySql querySql = (QuerySql) parameterObject;
            SqlBuilder sqlBuilder;
            switch (tableSchema.getDatabaseType()){
                case POSTGRESQL:
                    sqlBuilder=new PgsqlBuilder();
                    break;
                case MYSQL:
                    //因为使用Jdbc3KeyGenerator需要指定keyProperties,但生成语句不方便指定此属性 ,所以还是用selectKey
                    sqlBuilder = new MysqlBuilder();
                    break;
                case HSQL:
                    sqlBuilder=new HsqlBuilder();
                    break;
                case ORACLE:
                default:
                    throw new UnsupportedException("暂不支持此数据库="+tableSchema.getDatabaseType().name());
            }
            querySql.accept(sqlBuilder);

            extParamMap = sqlBuilder.getParamMap();

            String column;
            if (countQuery) {
                column = "COUNT(1)";
            } else {
                column = sqlBuilder.getColumn();
                if (HatStringUtil.isEmpty(column)) {
                    column = getSelectColumn();
                }
            }
            String whereClause = sqlBuilder.toString();


            sb
                    .append(" ")
                    .append(column)
                    .append(" ")
                    .append(SqlKeyword.FROM.getVal())
                    .append(" ")
                    .append(tableSchema.getTableName())
                    .append(" ")
                    .append(SqlKeyword.WHERE)
                    .append(" ")
                    .append(whereClause);

            if (!countQuery) {
                sb
                        .append(" ")
                        .append(sqlBuilder.getGroupBy())
                        .append(" ")
                        .append(sqlBuilder.getHaving())
                        .append(" ")
                        .append(sqlBuilder.getOrderBy())
                        .append(" ")
                        .append(sqlBuilder.getLimit());
            }
            ColumnTokenHandler tokenHandler = new ColumnTokenHandler(tableSchema);
            GenericTokenParser parser = new GenericTokenParser("@[", "]", tokenHandler);
            return parser.parse(sb.toString());
        }
        TableColumn pkColumn = tableSchema.getPrimaryColumn();
        if (pkColumn == null) {
            throw new ConfigException("数据表" + tableSchema.getTableName() + "未设置主键");
        }
        String where = pkColumn.getColumnName() + " = #{" + HatStringUtil.toFieldName(pkColumn.getColumnName()) + "}";
        String column = getSelectColumn();
        return SqlKeyword.SELECT + " " + column + " " + SqlKeyword.FROM + " " + tableName
                + " " + SqlKeyword.WHERE + " " + where;
    }

    private String getSelectColumn() {
        Class<?> clazz = tableClazz;
        List<String> selectColumn = Lists.newLinkedList();
        while (clazz != null && !Objects.equals(Object.class.getSimpleName(), clazz.getSimpleName())) {
            Field[] fields = clazz.getDeclaredFields();
            //获取数据表字段信息
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                String fieldName = field.getName();
                TableColumn tableColumn = tableSchema.getMatchColumn(fieldName);
                if (tableColumn != null) {
                    selectColumn.add(tableColumn.getColumnName());
                }
//                else {
//                    log.warn("数据表对象{}#{},未在数据表{}查询到映射字段, Continue ...", clazzName, fieldName, tableName);
//                }
            }
            clazz = clazz.getSuperclass();
        }
        return String.join(",", selectColumn);
    }

    @Override
    public Map<String, Object> getExtParamMap() {
        if (extParamMap == null) {
            return Collections.emptyMap();
        }
        return extParamMap;
    }
}

package com.jedijava.mybatis.hat.sql;

import com.google.common.collect.Lists;
import com.jedijava.mybatis.hat.enums.SqlKeyword;
import com.jedijava.mybatis.hat.exception.UnsupportedException;
import com.jedijava.mybatis.hat.meta.TableColumn;
import com.jedijava.mybatis.hat.meta.TableSchema;
import com.jedijava.mybatis.hat.override.HatConfig;
import lombok.extern.slf4j.Slf4j;

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
public class InsertHatSql implements HatSql {
    private HatConfig configuration;

    private TableSchema tableSchema;

    public InsertHatSql(HatConfig configuration, TableSchema tableSchema) {
        this.configuration = configuration;
        this.tableSchema = tableSchema;
    }

    @Override
    public String getSql(Object parameterObject) {
        Class<?> paramClazz = parameterObject.getClass();
        List<String> insertColumn = Lists.newLinkedList();
        List<String> insertValue = Lists.newLinkedList();
        while (paramClazz != null && !Objects.equals(Object.class.getSimpleName(), paramClazz.getSimpleName())) {
            Field[] fields = paramClazz.getDeclaredFields();
            //获取数据表字段信息
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                String fieldName = field.getName();
                TableColumn tableColumn = tableSchema.getMatchColumn(fieldName);
                if (tableColumn == null) {
                    log.warn("对象{}#{},未在数据表{}查询到映射字段,跳过", paramClazz.getName(), fieldName, tableSchema.getTableName());
                    continue;
                }
                try {
                    field.setAccessible(true);
                    //TODO 复合对象如何处理?
                    Object value = field.get(parameterObject);
                    if (Objects.isNull(value)) {
                        if (tableColumn.isPrimaryKey() && configuration.isUseGeneratedKeys()) {
                            switch (tableSchema.getDatabaseType()) {
                                case MYSQL:
                                    insertColumn.add(tableColumn.getColumnName());
                                    insertValue.add("#{" + fieldName + "}");
                                    break;
                                case ORACLE:
                                    break;
                                case POSTGRESQL:
                                    throw new UnsupportedException("暂未实现");
//                                    break;
                                case HSQL:
                                    insertColumn.add(tableColumn.getColumnName());
                                    insertValue.add("DEFAULT");
                                    break;
                                default:
                                    log.warn("未匹配到insert语句主键策略,暂不支持的数据库类型");
                            }
                        } else if (Objects.equals(fieldName, "createDateTime") || Objects.equals(fieldName, "updateDateTime")) {
                            insertColumn.add(tableColumn.getColumnName());
                            insertValue.add("CURRENT_TIMESTAMP");
                        }
                    } else {
                        insertColumn.add(tableColumn.getColumnName());
                        insertValue.add("#{" + fieldName + "}");
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            paramClazz = paramClazz.getSuperclass();
        }

        return SqlKeyword.INSERT.getVal() + " " + tableSchema.getTableName() + " ( " + String.join(",", insertColumn) + " ) "
                + SqlKeyword.VALUES + " ( " + String.join(",", insertValue) + ") ";
    }

    @Override
    public Map<String, Object> getExtParamMap() {
        return Collections.emptyMap();
    }
}

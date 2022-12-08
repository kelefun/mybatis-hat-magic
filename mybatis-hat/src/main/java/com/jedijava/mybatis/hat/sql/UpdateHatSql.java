package com.jedijava.mybatis.hat.sql;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jedijava.mybatis.hat.annotation.HatDeleted;
import com.jedijava.mybatis.hat.enums.SqlKeyword;
import com.jedijava.mybatis.hat.exception.ConfigException;
import com.jedijava.mybatis.hat.exception.UnsupportedException;
import com.jedijava.mybatis.hat.face.entity.HatEntity;
import com.jedijava.mybatis.hat.meta.TableColumn;
import com.jedijava.mybatis.hat.meta.TableSchema;
import com.jedijava.mybatis.hat.utils.HatStringUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.jedijava.mybatis.hat.constants.NameConst.*;

/*
 *
 * @author liukaiyang
 * @since 2019/9/26 13:49
 */
@Slf4j
public class UpdateHatSql implements HatSql {

    private TableSchema tableSchema;
    private String methodName;
    private Class<?> tableClazz;
    private Map<String, Object> extParamMap;

    public UpdateHatSql(TableSchema tableSchema, Class<?> tableClazz, String methodName) {
        this.tableSchema = tableSchema;
        this.methodName = methodName;
        this.tableClazz = tableClazz;
    }

    @Override
    public String getSql(Object parameterObject) {
        String tableName = tableSchema.getTableName();
        TableColumn pkColumn = tableSchema.getPrimaryColumn();
        if (pkColumn == null) {
            throw new ConfigException("数据表" + tableSchema.getTableName() + "未设置主键");
        }
        String where = pkColumn.getColumnName() + " = #{" + HatStringUtil.toFieldName(pkColumn.getColumnName()) + "}";
        if (Objects.equals(methodName, DELETE_LOGIC)) {
            TableColumn deleteFlagColumn = getDeleteFlagColumn();
            if (deleteFlagColumn == null) {
                throw new ConfigException("未获取到逻辑删除标记字段");
            }
            return SqlKeyword.UPDATE + " " + tableName + " " + SqlKeyword.SET + " " + deleteFlagColumn.getColumnName() + "= 1 "
                    + SqlKeyword.WHERE + " " + where;
        }
        if (!Objects.equals(methodName, UPDATE_BY_KEY)) {
            throw new UnsupportedException("暂不支持除" + UPDATE_BY_KEY + "之外的更新方法,建议使用xml等传统方式");
        }

        List<String> updateColumn = Lists.newLinkedList();
        if (parameterObject instanceof HashMap) {
            HashMap<String, Object> map = (HashMap) parameterObject;
            if (map.containsKey(SECOND_PARAM) && map.containsKey(IGNORE_NULL)) {
                //表明接口多个参数 {@link org.apache.ibatis.reflection.ParamNameResolver#getNamedParams }
                map.putIfAbsent(IGNORE_NULL, true);
                boolean ignoreNull = (boolean) map.get(IGNORE_NULL);
                map.forEach((key, value) -> {
                    if (!key.startsWith(GENERIC_NAME_PREFIX)) {
                        return;
                    }
                    if (value instanceof HatEntity) {
                        applyClazzUpdateColumn(value, updateColumn, ignoreNull);
                    } else if (value instanceof Map) {
                        applyMapUpdateColumn(map, updateColumn);
                    }
                });
            } else {
                //只有一个参数,且参数为Map
                applyMapUpdateColumn(map, updateColumn);
            }

        } else {
            applyClazzUpdateColumn(parameterObject, updateColumn, true);
        }
        return SqlKeyword.UPDATE + " " + tableName + " " + SqlKeyword.SET + " " + String.join(",", updateColumn) + " "
                + SqlKeyword.WHERE + " " + where;
    }

    private void applyClazzUpdateColumn(Object entity, List<String> updateColumn, boolean ignoreNull) {
        Class<?> paramClazz = entity.getClass();
        while (paramClazz != null && !Objects.equals(Object.class.getSimpleName(), paramClazz.getSimpleName())) {
            Field[] fields = paramClazz.getDeclaredFields();
            //获取数据表字段信息
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                String fieldName = field.getName();
                TableColumn tableColumn = tableSchema.getMatchColumn(fieldName);
                if (tableColumn != null) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(entity);

                        //默认更新update_date_time字段为当前时间
                        if (Objects.equals(tableColumn.getColumnName(), UPDATE_COLUMN_NAME)) {
                            updateColumn.add(tableColumn.getColumnName() + " = CURRENT_TIMESTAMP");
                        } else {
                            if (Objects.isNull(value)) {
                                if (ignoreNull == false && !tableColumn.isPrimaryKey()) {
                                    updateColumn.add(tableColumn.getColumnName() + " = NULL");
                                }
                            } else {
                                addParam(fieldName, value);
                                if (!tableColumn.isPrimaryKey()) {
                                    updateColumn.add(tableColumn.getColumnName() + "= #{" + fieldName + "}");
                                }
                            }
                        }

                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            paramClazz = paramClazz.getSuperclass();
        }
    }

    private void applyMapUpdateColumn(Map<String, Object> map, List<String> updateColumn) {
        map.forEach((key, value) -> {
            TableColumn tableColumn = tableSchema.getMatchColumn(key);
            if (tableColumn != null && !tableColumn.isPrimaryKey()) {
                updateColumn.add(tableColumn.getColumnName() + "= #{" + key + "}");
            }
        });
    }


    private TableColumn getDeleteFlagColumn() {
        Class clazz = tableClazz;
        while (clazz != null && !Objects.equals(Object.class.getName(), clazz.getName())) {
            Field[] fields = clazz.getDeclaredFields();
            //获取数据表字段信息
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (field.isAnnotationPresent(HatDeleted.class)) {
                    TableColumn tableColumn = tableSchema.getMatchColumn(field.getName());
                    if (tableColumn != null) {
                        return tableColumn;
                    }
                }
                if (DEFAULT_DELETE_FLAG_COLUMN.equals(field.getName())) {
                    TableColumn tableColumn = tableSchema.getMatchColumn(field.getName());
                    if (tableColumn != null) {
                        return tableColumn;
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    @Override
    public Map<String, Object> getExtParamMap() {
        if (extParamMap == null) {
            return Collections.emptyMap();
        }
        return extParamMap;
    }

    public void addParam(String key, Object value) {
        if (extParamMap == null) {
            extParamMap = Maps.newConcurrentMap();
        }
        extParamMap.put(key, value);
    }
}

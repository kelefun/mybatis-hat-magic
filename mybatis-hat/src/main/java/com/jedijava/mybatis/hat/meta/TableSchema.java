package com.jedijava.mybatis.hat.meta;

import com.google.common.collect.Maps;
import com.jedijava.mybatis.hat.enums.DatabaseType;
import com.jedijava.mybatis.hat.utils.HatStringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author liukaiyang
 * @since 2019/8/26 21:13
 */
@Slf4j
public class TableSchema {
    /**
     * 使用的数据库类型
     */
    private DatabaseType databaseType;
    /**
     * 库名称
     */
    private String catalog;
    /**
     * 表名
     */
    private String tableName;

    /**
     * 表的主键
     */
    private TableColumn primaryColumn;

//    private List<TableColumn> uniqueColumnList = Lists.newArrayList();

    private Map<String, TableColumn> columnMap = Maps.newConcurrentMap();
    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    /**
     * @param column
     * @return
     */
    public void addColumn(TableColumn column) {
        if (column == null || HatStringUtil.isEmpty(column.getColumnName())) {
            log.warn("return,illegal column/入参字段错误");
            return;
        }
        if (columnMap.containsKey(column.getColumnName())) {
            log.warn("return,this column already exist/该字段已存在");
            return;
        }
        if (column.isPrimaryKey()) {
            this.primaryColumn = column;
        }
        columnMap.put(column.getColumnName().toUpperCase(), column);
    }

    /**
     * 备注: 设计表时 注意避免出现相似命名的字段(例如  show_name 和 showname)/即去掉下划线后相同
     *
     * @param columnName class中的属性名称 / 表字段名称
     * @return
     */
    public TableColumn getMatchColumn(String columnName) {
        if (HatStringUtil.isEmpty(columnName)) {
            return null;
        }
        TableColumn column = columnMap.get(columnName.toUpperCase());

        if (column == null) {
            //驼峰转下划线
            columnName = HatStringUtil.toUnderline(columnName).toUpperCase();
            column = columnMap.get(columnName);
        }
        return column;
    }

//    /**
//     * 根据输入属性名，获取匹配到的数据库字段名
//     *
//     * @return
//     */
//    public String getMatchColumnName(String fieldName) {
//        if (fieldName == null) {
//            return null;
//        }
//        TableColumn column = getMatchColumn(fieldName);
//        if (column == null) {
//            throw new TypeException("can not match column " + fieldName + " from table " + tableName);
//        }
//        return column.getColumnName();
//    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public TableColumn getPrimaryColumn() {
        return primaryColumn;
    }

    public void setPrimaryColumn(TableColumn primaryColumn) {
        this.primaryColumn = primaryColumn;
    }

    @Override
    public String toString() {
        return "TableSchema{" +
                "catalog='" + catalog + "\',\n" +
                "tableName='" + tableName + "\',\n" +
                "primaryColumn=" + primaryColumn + ",\n" +
                columns() +
                "}";
    }

    public String columns() {
        StringBuilder sb = new StringBuilder("columnList=[\n");
        columnMap.forEach((k, v) -> {
            sb.append(v).append("\n");
        });
        sb.append("]");
        return sb.toString();
    }

}
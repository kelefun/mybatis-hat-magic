package com.jedijava.mybatis.hat.meta;

import com.jedijava.mybatis.hat.enums.DatabaseType;
import com.jedijava.mybatis.hat.utils.HatStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO 后续需要处理多数据源
 *
 * @author liukaiyang
 * @since 2019/9/5 9:54
 */
@Slf4j
public class TableMetaFactory {

    private final static String COLUMN_NAME = "COLUMN_NAME";
    //    private final static String TABLE_NAME = "TABLE_NAME";
    private final static String DATA_TYPE = "DATA_TYPE";
    private final static String TYPE_NAME = "TYPE_NAME";
    private final static String NULL_ABLE = "IS_NULLABLE";
    public static DatabaseType databaseType;

    //multi datasource is shit
    //key=tableName
    protected final static Map<String, TableSchema> TABLE_SCHEMA_MAP = new ConcurrentHashMap<>();

    /**
     * 根据表名，获取表结构信息
     *
     * @param tableName
     * @return
     */
    public static TableSchema getTableSchema(String tableName) {
        if (HatStringUtil.isEmpty(tableName)) {
            return null;
        }
        TableSchema schema = TABLE_SCHEMA_MAP.get(tableName);
        if (Objects.isNull(schema)) {
            //部分数据库不区分大小写
            schema = TABLE_SCHEMA_MAP.get(tableName.toUpperCase());
        }
//        List<TableColumn> columns = getTableColumns(tableName);
//        if (CollectionUtil.isEmpty(columns)) {
//            throw new TypeException("can not find schema for table " + tableName);
//        }
//        schema.setColumnList(columns);
//        schema.setTableName(tableName);

        return schema;
    }

    /**
     * 加载表结构信息
     *
     * @author liukaiyang
     * @since 2019/9/6 9:10
     */
    public static void initTableSchema(SqlSession sqlSession) throws SQLException {
        if (!TABLE_SCHEMA_MAP.isEmpty()) {
            return;
        }
        Connection connection = sqlSession.getConnection();
        String catalog = connection.getCatalog();
        String schema = connection.getSchema();
        DatabaseMetaData metaData = connection.getMetaData();
        String productName = metaData.getDatabaseProductName();
        String productVersion = metaData.getDatabaseProductVersion();
        log.info("数据库属性信息 {},版本号 {}", productName, productVersion);
        //数据类型
//        ResultSet info = metaData.getTypeInfo();
//        while (info.next()) {
//            String TYPE_NAME = info.getString("TYPE_NAME");
//            String DATA_TYPE = info.getString("DATA_TYPE");
//            String PRECISION = info.getString("PRECISION");
//            String AUTO_INCREMENT = info.getString("AUTO_INCREMENT");
//            log.info("{} , {} , {} , {}", TYPE_NAME, DATA_TYPE, PRECISION, AUTO_INCREMENT);
//        }
        //一般都会有表(指VIEW,TABLE这一类中的TABLE),不再做校验
//        List<String> typeList = new ArrayList<>();
//        ResultSet type = metaData.getTableTypes();
//        while (type.next()) {
//            typeList.add(type.getString("TABLE_TYPE"));
//        }
//        if (!typeList.contains("TABLE")) {
//            throw new PersistenceException("数据库错误,没有表信息");
//        }
        //判断数据库类型(为了区分语法,影响生成的sql语句)
        for (DatabaseType dbType : DatabaseType.values()) {
            String name = dbType.name();
            if (productName.toUpperCase().contains(name)) {
                databaseType = dbType;
                break;
            }
        }
        ResultSet table = metaData.getTables(catalog, schema, null, new String[]{"TABLE"});
        while (table.next()) {
            String tableName = table.getString("TABLE_NAME");
            if (HatStringUtil.isEmpty(tableName)) {
                continue;
            }
            TableSchema tableSchema = new TableSchema();
            tableSchema.setDatabaseType(databaseType);
            tableSchema.setCatalog(catalog);
            tableSchema.setTableName(tableName);

            //查找所有字段
            ResultSet col = metaData.getColumns(catalog, schema, tableName, null);
            while (col.next()) {
                TableColumn column = new TableColumn();
                column.setTableSchema(tableSchema);
                column.setColumnName(col.getString(COLUMN_NAME));
                column.setColumnTypeCode(col.getInt(DATA_TYPE));
                column.setColumnTypeName(col.getString(TYPE_NAME));
                column.setNullAble(col.getString(NULL_ABLE));

                tableSchema.addColumn(column);
            }
            //查找primaryKey
            ResultSet pk = metaData.getPrimaryKeys(catalog, schema, tableName);
            while (pk.next()) {
                String pkColName = pk.getString(COLUMN_NAME);
                TableColumn pkColumn = tableSchema.getMatchColumn(pkColName);
                if (pkColumn != null) {
                    pkColumn.setPrimaryKey(true);
                    tableSchema.setPrimaryColumn(pkColumn);
                }
            }
            //查找uniqueKey
//            ResultSet uk = metaData.getIndexInfo(catalog, schema, tableName, true, false);
//            while (uk.next()) {
//                System.out.println("Unique_COLUMN_NAME:" + uk.getString(COLUMN_NAME));
//            }
            TABLE_SCHEMA_MAP.put(tableName, tableSchema);
        }

    }

}

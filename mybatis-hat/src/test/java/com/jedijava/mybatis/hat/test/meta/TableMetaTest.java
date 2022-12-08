package com.jedijava.mybatis.hat.test.meta;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 * @author liukaiyang
 * @since 2019/9/5 16:45
 */
public class TableMetaTest {

    private static final String JDBC_URL = "jdbc:hsqldb:mem:testdb";
    private static final String JDBC_DRIVER = "org.hsqldb.jdbc.JDBCDriver";

    @Test
    public void testCatalog() throws Exception {
        Class.forName(JDBC_DRIVER);
        InputStream is = getClass().getResourceAsStream("/sql/user.sql");
        //初始化数据
        try (Connection connection = DriverManager.getConnection(JDBC_URL, "SA", "")) {
            ScriptRunner sr = new ScriptRunner(connection);
            sr.setLogWriter(null);
            sr.runScript(new InputStreamReader(is));
            String catalog = connection.getCatalog();
            String schema = connection.getSchema();

            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet type = metaData.getTableTypes();
            while (type.next()) {
                System.out.println(type.getString("TABLE_TYPE"));
            }
            ResultSet table = metaData.getTables(catalog, schema, null, null);
            while (table.next()) {
                String tableName= table.getString("TABLE_NAME");
                ResultSet pk = metaData.getPrimaryKeys(catalog, schema, tableName);
                while (pk.next()) {
                    System.out.println("TABLE_CAT:" + pk.getString(1));
                    System.out.println("TABLE_SCHEMA:" + pk.getString(2));
                    System.out.println("TABLE_NAME:" + pk.getString(3));
                    System.out.println("COLUMN_NAME:" + pk.getString(4));
                    System.out.println("KEY_SEQ:" + pk.getString(5));
                    System.out.println("PK_NAME:" + pk.getString(6));
                }
            }
        }
    }
}

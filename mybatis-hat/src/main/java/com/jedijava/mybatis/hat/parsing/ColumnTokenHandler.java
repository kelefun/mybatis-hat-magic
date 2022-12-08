package com.jedijava.mybatis.hat.parsing;

import com.jedijava.mybatis.hat.exception.SqlException;
import com.jedijava.mybatis.hat.meta.TableColumn;
import com.jedijava.mybatis.hat.meta.TableSchema;
import org.apache.ibatis.parsing.TokenHandler;

/**
 * @author liukaiyang
 * @since 2019/9/18 17:12
 */
public class ColumnTokenHandler implements TokenHandler {
    TableSchema tableSchema;

    public ColumnTokenHandler(TableSchema tableSchema) {
        this.tableSchema = tableSchema;
    }

    @Override
    public String handleToken(String columnName) {
        TableColumn tableColumn = tableSchema.getMatchColumn(columnName);
        if (tableColumn != null) {
            return tableColumn.getColumnName();
        }
        throw new SqlException(String.format("未在表%s查询到字段%s,含有字段列表=%s",tableSchema.getTableName(),columnName,tableSchema.columns()));
    }
}

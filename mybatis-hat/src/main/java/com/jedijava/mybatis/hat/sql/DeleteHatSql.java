package com.jedijava.mybatis.hat.sql;

import com.jedijava.mybatis.hat.enums.SqlKeyword;
import com.jedijava.mybatis.hat.exception.ConfigException;
import com.jedijava.mybatis.hat.meta.TableColumn;
import com.jedijava.mybatis.hat.meta.TableSchema;
import com.jedijava.mybatis.hat.utils.HatStringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;

/*
 *
 * @author liukaiyang
 * @since 2019/9/26 13:49
 */
@Slf4j
public class DeleteHatSql implements HatSql {

    private TableSchema tableSchema;

    public DeleteHatSql(TableSchema tableSchema) {
        this.tableSchema = tableSchema;
    }

    @Override
    public String getSql(Object parameterObject) {
        TableColumn pkColumn = tableSchema.getPrimaryColumn();
        if (pkColumn == null) {
            throw new ConfigException("数据表" + tableSchema.getTableName() + "未设置主键");
        }
        String where = pkColumn.getColumnName() + " = #{" + HatStringUtil.toFieldName(pkColumn.getColumnName()) + "}";
        return SqlKeyword.DELETE + " " + SqlKeyword.FROM + " " + tableSchema.getTableName() + " "
                + SqlKeyword.WHERE + " " + where;
    }

    @Override
    public Map<String, Object> getExtParamMap() {
        return Collections.emptyMap();
    }
}

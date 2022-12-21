package com.jedijava.mybatis.hat.sql.mysql;

import com.jedijava.mybatis.hat.constants.SymbolConst;
import com.jedijava.mybatis.hat.sql.SqlBuilder;

public class MysqlBuilder extends SqlBuilder {

    @Override
    public String getLimit() {
        String page = " ";
        if (super.limit != null && offset != null) {
            page = page + "limit " + limit + SymbolConst.COMMA + offset;
        }
        return page;
    }
}

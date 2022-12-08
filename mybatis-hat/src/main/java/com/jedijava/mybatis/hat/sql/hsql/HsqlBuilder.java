package com.jedijava.mybatis.hat.sql.hsql;

import com.jedijava.mybatis.hat.constants.SymbolConst;
import com.jedijava.mybatis.hat.sql.SqlBuilder;

public class HsqlBuilder extends SqlBuilder {
    @Override
    public String getLimit() {

        String page = " ";
        if (super.limit != null && offset != null) {
            page = page + "limit " + (offset < 0 ? 0 : offset) + SymbolConst.COMMA + (limit < 0 ? 1000 : limit);
        }
        return page;
    }
}

package com.jedijava.mybatis.hat.sql.pgsql;

import com.jedijava.mybatis.hat.sql.SqlBuilder;

public class PgsqlBuilder extends SqlBuilder {
    @Override
    public String getLimit() {

        String page = " ";
        if (super.limit != null && super.offset != null) {
            page = page + "limit " + limit + " offset " + offset;
        }
        return page;
    }
}

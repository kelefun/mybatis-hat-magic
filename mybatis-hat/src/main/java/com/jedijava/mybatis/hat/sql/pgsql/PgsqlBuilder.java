package com.jedijava.mybatis.hat.sql.pgsql;

import com.google.common.base.Objects;
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
//    @Override
//    public void page(int pageIndex, int pageSize) {
//        super.offset = pageSize*pageIndex;
//        super.limit = pageSize;
//    }
}

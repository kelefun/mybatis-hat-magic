package com.jedijava.mybatis.hat.sql;

import java.util.Map;

public interface HatSql {
    Map<String, Object> getExtParamMap();

    String getSql(Object parameterObject);
}

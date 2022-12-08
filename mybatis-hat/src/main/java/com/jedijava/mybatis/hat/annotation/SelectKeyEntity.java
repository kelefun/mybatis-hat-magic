package com.jedijava.mybatis.hat.annotation;

import com.jedijava.mybatis.hat.utils.HatStringUtil;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.mapping.StatementType;

import java.lang.annotation.Annotation;

public class SelectKeyEntity implements SelectKey {
    private String[] statement;
//    private String keyProperty;
    private String keyColumn;
    private boolean before;

    public SelectKeyEntity(String[] statement,String keyColumn,boolean before){
        this.statement=statement;
        this.keyColumn=keyColumn;
        this.before=before;
    }

    @Override
    public String[] statement() {
        return statement;
    }

    @Override
    public String keyProperty() {
        return HatStringUtil.toCamel(keyColumn);
    }

    @Override
    public String keyColumn() {
        return keyColumn;
    }

    @Override
    public boolean before() {
        return before;
    }

    @Override
    public Class<?> resultType() {
        return Long.class;
    }

    @Override
    public StatementType statementType() {
        return StatementType.STATEMENT;
    }

    @Override
    public String databaseId() {
        return "";
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return SelectKey.class;
    }
}

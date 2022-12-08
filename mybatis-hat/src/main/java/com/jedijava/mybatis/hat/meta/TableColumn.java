package com.jedijava.mybatis.hat.meta;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author liukaiyang
 * @since 2019/8/26 21:13
 */
@Getter
@Setter
public class TableColumn implements Serializable {

    private static final long serialVersionUID = -3141860913890454221L;
    /**
     * 字段名称
     */
    private String columnName;
    /**
     * 字段类型(varchar,int等)
     */
    private String columnTypeName;
    /**
     * @see java.sql.Types
     */
    private int columnTypeCode;
    /**
     *
     * YES/NO/<code>null</code>
     */
    private String nullAble;

    private TableSchema tableSchema;

    private boolean primaryKey=false;
//    private boolean uniqueKey=false;


    @Override
    public String toString() {
        return "TableColumn{" +
                "columnName='" + columnName + '\'' +
                ", columnTypeName='" + columnTypeName + '\'' +
                ", columnTypeCode=" + columnTypeCode +
                ", nullAble=" + nullAble +
                ", primaryKey=" + primaryKey +
                '}';
    }
}

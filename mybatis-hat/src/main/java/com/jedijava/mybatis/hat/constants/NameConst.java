package com.jedijava.mybatis.hat.constants;

/*
 *
 * @author liukaiyang
 * @since 2019/9/25 21:27
 */
public interface NameConst {
    String GENERIC_NAME_PREFIX = "param";
    String FIRST_PARAM="param0";
    String SECOND_PARAM="param1";


    String UPDATE_BY_KEY="updateByKey";
    //逻辑删除方法名
    String DELETE_LOGIC="deleteLogic";
    //删除标记字段
    String DEFAULT_DELETE_FLAG_COLUMN="deleted";
    String IGNORE_NULL="ignoreNull";
    String UPDATE_COLUMN_NAME="update_date_time";
}

package com.jedijava.mybatis.hat.repository;


import com.jedijava.mybatis.hat.annotation.HatMethod;
import com.jedijava.mybatis.hat.face.entity.HatEntity;
import com.jedijava.mybatis.hat.sql.QuerySql;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.List;

/**
 * @author liukaiyang
 * @since 2019/8/29 14:14
 */
public interface QueryRepository<E extends HatEntity> extends BaseRepository<E> {
    List<E> selectList(QuerySql querySql);

    /**
     * 约定查询语句以select开头,如果不按此命名方式,则需要用注解{@link SqlCommandType#SELECT}标记
     * @param querySql
     * @return
     */
    @HatMethod(type = SqlCommandType.SELECT)
    int countList(QuerySql querySql);
}

package com.jedijava.mybatis.hat.face.service;


import com.jedijava.mybatis.hat.face.entity.PageQuery;
import com.jedijava.mybatis.hat.face.entity.HatEntity;
import com.jedijava.mybatis.hat.face.entity.PageResult;

import java.util.List;
import java.util.Optional;

/**
 * @author liukaiyang
 * @since  2019/8/29 14:14
 */
public interface QueryService<Q extends PageQuery, E extends HatEntity> extends BaseService<E> {
    /**
     * @param q
     * @return 不会返回null, 如果查询结果为空, 返回的是空集合
     */
    List<E> selectList(Q q);

    /**
     * 如果查询到多条记录,返回第一个
     * @param q
     * @return
     */
    Optional<E> selectOne(Q q);

    Integer countList(Q q);

    PageResult<E> pageList(Q q);
}

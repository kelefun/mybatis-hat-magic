package com.jedijava.mybatis.hat.face.service;


import com.jedijava.mybatis.hat.face.entity.HatEntity;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author liukaiyang
 * @since 2019/8/29 14:14
 */
public interface BaseService<E extends HatEntity>{
    /**
     * @param entity
     * @return entity with id
     */
    E insert(E entity);

    /**
     * @param key 主键
     * @return
     */
    int deleteByKey(Serializable key);

    /**
     * 逻辑删除(默认标记字段为 deleted: 1删除, 0正常),如果是其它字段,请在实体类中用注解标明
     *
     * @param key 主键
     * @return
     */
    int deleteLogic(Serializable key);

    int updateByKey(E entity);

    Optional<E> selectByKey(Serializable key);

}

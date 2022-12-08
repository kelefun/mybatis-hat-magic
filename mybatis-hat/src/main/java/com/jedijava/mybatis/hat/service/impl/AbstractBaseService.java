package com.jedijava.mybatis.hat.service.impl;

import com.jedijava.mybatis.hat.face.entity.HatEntity;
import com.jedijava.mybatis.hat.face.service.BaseService;
import com.jedijava.mybatis.hat.repository.BaseRepository;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author liukaiyang
 * @since 2019/11/26 18:19
 */
public abstract class AbstractBaseService<E extends HatEntity> implements BaseService<E> {
    private BaseRepository baseRepository;

    public AbstractBaseService(BaseRepository repository) {
        this.baseRepository = repository;
    }

    @Override
    public E insert(E entity) {
        baseRepository.insert(entity);
        return entity;
    }

    @Override
    public int deleteByKey(Serializable key) {
        return baseRepository.deleteByKey(key);
    }

    @Override
    public int deleteLogic(Serializable key) {
        return baseRepository.deleteLogic(key);
    }

    @Override
    public int updateByKey(E entity) {
        return baseRepository.updateByKey(entity,true);
    }

    @Override
    public Optional<E> selectByKey(Serializable key) {
        return baseRepository.selectByKey(key);
    }
}

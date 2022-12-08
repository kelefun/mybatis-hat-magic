package com.jedijava.mybatis.hat.repository;

import com.jedijava.mybatis.hat.face.entity.HatEntity;

/**
 * 继承此接口的类 方法,如果mapper.xml没有对应的statementId,会自动构建sql
 * @author liukaiyang
 * @since 2019/9/3 17:19
 */
public interface HatRepository<E extends HatEntity> {
}

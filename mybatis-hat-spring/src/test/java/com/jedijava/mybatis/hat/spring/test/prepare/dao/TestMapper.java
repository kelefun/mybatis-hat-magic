package com.jedijava.mybatis.hat.spring.test.prepare.dao;

import com.jedijava.mybatis.hat.repository.QueryRepository;
import com.jedijava.mybatis.hat.spring.test.prepare.obj.TestEntity;
import com.jedijava.mybatis.hat.spring.test.prepare.obj.TestQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liukaiyang
 * @since 2019/9/4 19:37
 */
@Repository
public interface TestMapper extends QueryRepository<TestEntity> {
    List<TestEntity> selectListQuery(TestQuery querySql);

}

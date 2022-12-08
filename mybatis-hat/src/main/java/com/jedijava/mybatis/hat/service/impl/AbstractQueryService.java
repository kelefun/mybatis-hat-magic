package com.jedijava.mybatis.hat.service.impl;

import com.jedijava.mybatis.hat.face.entity.HatEntity;
import com.jedijava.mybatis.hat.face.entity.PageQuery;
import com.jedijava.mybatis.hat.face.entity.PageResult;
import com.jedijava.mybatis.hat.face.service.QueryService;
import com.jedijava.mybatis.hat.repository.QueryRepository;
import com.jedijava.mybatis.hat.sql.QuerySql;

import java.util.List;
import java.util.Optional;

/**
 * @author liukaiyang
 * @since 2019/11/26 18:19
 */
public abstract class AbstractQueryService<Q extends PageQuery, E extends HatEntity> extends AbstractBaseService<E> implements QueryService<Q, E> {
    private QueryRepository queryRepository;

    public AbstractQueryService(QueryRepository repository) {
        super(repository);
        this.queryRepository = repository;
    }

    @Override
    public List<E> selectList(Q q) {
        return queryRepository.selectList(getQuerySql(q));
    }

    @Override
    public Optional<E> selectOne(Q q) {
        List<E> list = selectList(q);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Integer countList(Q q) {
        return queryRepository.countList(getQuerySql(q));
    }

    @Override
    public PageResult<E> pageList(Q q) {
        Integer count = countList(q);
        if (count == null || count == 0) {
            return PageResult.empty();
        }
        return new PageResult<>(count,q.getPageIndex(),q.getPageSize(),selectList(q));
    }

    /**
     * <code>
     * return sqlBuilder->{
     *     sqlBuilder.lambda().
     * }
     * </code>
     */
    public abstract QuerySql getQuerySql(Q q);
}

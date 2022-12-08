package com.jedijava.mybatis.hat.repository;

import com.jedijava.mybatis.hat.annotation.HatMethod;
import com.jedijava.mybatis.hat.constants.NameConst;
import com.jedijava.mybatis.hat.face.entity.HatEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.SqlCommandType;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

/**
 * 基本的查询方法
 * 注: 泛型 <code>E</code>对象 对应数据表
 *
 * @author liukaiyang
 * @since 2019/8/29 14:14
 */
public interface BaseRepository<E extends HatEntity> extends HatRepository<E> {
    /**
     * @param entity
     * @return 影响的条数(如果需要获取自增主键, 从入参实体中获取)
     */
    int insert(E entity);

    /**
     * @param value 主键的值
     * @return 影响的条目数
     */
    int deleteByKey(Serializable value);

    /**
     * 逻辑删除(默认标记字段为 deleted: 1删除, 0正常),如果是其它字段,请在实体类中用注解表明
     *
     * @param value 主键的值
     * @return
     */
    @HatMethod(type = SqlCommandType.UPDATE)
    int deleteLogic(Serializable value);

    /**
     * 更新
     *
     * @param entity     数据表映射对象
     * @param ignoreNull 是否忽略null字段(比如 ignoreNull=true 则对象entity中为null时忽略此字段)
     * @return
     */
    int updateByKey(E entity, @Param(NameConst.IGNORE_NULL) Boolean ignoreNull);

    /**
     * 更新map中的key对应的数据库字段
     * @param map
     * @return
     */
    int updateByKey(Map<String, Object> map);

    /**
     * 根据主键查询(设计数据表时请保持
     *
     * @param value
     * @return
     */
    Optional<E> selectByKey(Serializable value);

    /**
     * @param sql 可执行sql
     * @return
     */
//    <T> T executeSql(String sql);
}

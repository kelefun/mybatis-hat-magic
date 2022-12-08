package com.jedijava.mybatis.hat.override;


import com.jedijava.mybatis.hat.enums.DatabaseType;
import com.jedijava.mybatis.hat.repository.HatRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

/**
 * @author liukaiyang
 * @since 2019/9/3 20:01
 */
@Slf4j
public class HatConfig extends Configuration {
    public HatConfig() {
        super();

//        mapUnderscoreToCamelCase=true;//TODO 需要验证此配置
        useGeneratedKeys = false;
    }

    public HatConfig(Environment environment) {
        this();
        super.environment = environment;
    }

//    protected DatabaseType databaseType;

    protected final MapperHatRegistry hatMapperRegistry = new MapperHatRegistry(this);

    /**
     * 优先级:  xml  > annotation(类似{@link Select} > interface(继承 {@link HatRepository})
     */
    @Override
    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.putIfAbsent(ms.getId(), ms);
    }

    @Override
    public MapperRegistry getMapperRegistry() {
        return hatMapperRegistry;
    }

    @Override
    public <T> void addMapper(Class<T> type) {
        hatMapperRegistry.addMapper(type);
    }

    @Override
    public void addMappers(String packageName, Class<?> superType) {
        hatMapperRegistry.addMappers(packageName, superType);
    }

    @Override
    public void addMappers(String packageName) {
        hatMapperRegistry.addMappers(packageName);
    }

    @Override
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return hatMapperRegistry.getMapper(type, sqlSession);
    }

    @Override
    public boolean hasMapper(Class<?> type) {
        return hatMapperRegistry.hasMapper(type);
    }


}

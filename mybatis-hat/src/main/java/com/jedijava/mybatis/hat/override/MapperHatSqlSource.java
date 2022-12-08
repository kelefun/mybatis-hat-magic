package com.jedijava.mybatis.hat.override;

import com.jedijava.mybatis.hat.annotation.HatTable;
import com.jedijava.mybatis.hat.exception.ArgumentException;
import com.jedijava.mybatis.hat.exception.HatException;
import com.jedijava.mybatis.hat.meta.TableMetaFactory;
import com.jedijava.mybatis.hat.meta.TableSchema;
import com.jedijava.mybatis.hat.sql.*;
import com.jedijava.mybatis.hat.utils.HatStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.type.TypeException;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author liukaiyang
 * @since 2019/9/9 13:50
 */
@Slf4j
public class MapperHatSqlSource implements SqlSource {
    private HatConfig configuration;
    private Class<?> mapperType;
    private SqlCommandType sqlCommandType;
    private LanguageDriver languageDriver;
    private Method method;
    private Class<?> tableClazz;
    private TableSchema tableSchema;

    public TableSchema getTableSchema() {
        return tableSchema;
    }

    public MapperHatSqlSource(HatConfig configuration, Class<?> mapperType, Method method, SqlCommandType sqlCommandType, LanguageDriver lang) {
        this.configuration = configuration;
        this.mapperType = mapperType;
        this.sqlCommandType = sqlCommandType;
        this.languageDriver = lang;
        this.method = method;
        this.tableClazz = initTableClazz();
        String tableName = initTableName();
        this.tableSchema = TableMetaFactory.getTableSchema(tableName);
        if (this.tableSchema == null) {
            throw new HatException("数据表" + tableName + "不存在,或程序初始化错误,未正确获取数据表信息");
        }
    }

    /*
     * 接口中多个参数时,何时把参数合为Map的?  {@link org.apache.ibatis.reflection.ParamNameResolver#getNamedParams }
     *
     * @author liukaiyang
     * @since 2019/9/25 20:54
     */
    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        if (parameterObject == null) {
            throw new ArgumentException("暂不支持 NULL 参数");
        }
        try {
            HatSql hatSql;
            switch (sqlCommandType) {
                case INSERT:
                    hatSql = new InsertHatSql(configuration, tableSchema);
                    break;
                case DELETE:
                    hatSql = new DeleteHatSql(tableSchema);
                    break;
                case UPDATE:
                    hatSql = new UpdateHatSql(tableSchema, tableClazz, method.getName());
                    break;
                case SELECT:
                    boolean countQuery = method.getReturnType().isPrimitive();
                    hatSql = new SelectHatSql(tableSchema, tableClazz, countQuery);
                    break;
                default:
                    throw new TypeException("暂不支持此类型" + sqlCommandType);
            }
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, hatSql.getSql(parameterObject), MapperMethod.ParamMap.class);
            BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
            if (!hatSql.getExtParamMap().isEmpty()) {
                hatSql.getExtParamMap().forEach(boundSql::setAdditionalParameter);
            }
            return boundSql;
        } catch (BuilderException e) {
            throw e;
        } catch (Exception e) {
            throw new BuilderException("sql构建异常:" + e.getMessage(), e);
        }
    }

    /**
     * 从dao接口泛型获取数据表对象
     */
    private Class<?> initTableClazz() {
        Type[] superInterface = mapperType.getGenericInterfaces();
        if (superInterface == null || superInterface.length == 0) {
            throw new TypeException("this type has not extends anything, should not goes here /继承了基础接口类SuperRepository的才有可能执行到这");
        }
        Type repositoryType = superInterface[0];
        if (repositoryType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) repositoryType;

            Type type = parameterizedType.getActualTypeArguments()[0];
            if (type instanceof Class<?>) {
                return (Class<?>) type;
            }
        }
        throw new TypeException(mapperType.getName() + "泛型参数错误");
    }

    private String initTableName() {
        Class<?> clazz = tableClazz;
        String tableName = null;
        String simpleName = clazz.getSimpleName();
        while (clazz != null && !Objects.equals(Object.class.getSimpleName(), clazz.getSimpleName())) {
            HatTable hatTable = clazz.getAnnotation(HatTable.class);
            if (hatTable != null) {
                tableName = hatTable.value();
            }
            clazz = clazz.getSuperclass();
        }
        if (tableName == null) {
            tableName = simpleName;
        }
        return HatStringUtil.toUnderline(tableName);
    }


}

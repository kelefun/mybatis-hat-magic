package com.jedijava.mybatis.hat.override;

import com.jedijava.mybatis.hat.annotation.HatField;
import com.jedijava.mybatis.hat.annotation.HatMethod;
import com.jedijava.mybatis.hat.annotation.SelectKeyEntity;
import com.jedijava.mybatis.hat.exception.UnsupportedException;
import com.jedijava.mybatis.hat.meta.TableSchema;
import com.jedijava.mybatis.hat.sql.QuerySql;
import com.jedijava.mybatis.hat.utils.HatStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeException;

import java.lang.reflect.*;
import java.util.*;

/**
 * 如果没有写xml或注解情况下 ,继承 SuperRepository的接口自动生成sql语句
 * 备注: 在没有使用mybatis-spring 组件时 ,需要有xml文件来声明namespace
 *
 * @author liukaiyang
 * @since 2019/9/3 20:50
 */
@Slf4j
public class MapperHatBuilder {
    private final HatConfig configuration;
    private final MapperBuilderAssistant assistant;
    //dao接口类
    private final Class<?> type;

    public MapperHatBuilder(HatConfig configuration, Class<?> type) {
        String resource = type.getName().replace('.', '/') + ".java (best guess)";
        this.assistant = new MapperBuilderAssistant(configuration, resource);
        this.configuration = configuration;
        this.type = type;
    }

    public void parse() {
        String resource = type.toString();
        if (!configuration.isResourceLoaded(resource)) {
            throw new TypeException("resource has not been loaded");
        }
        assistant.setCurrentNamespace(type.getName());
        Method[] methods = type.getMethods();
        for (Method method : methods) {
            if (!method.isBridge()) {
                final String mappedStatementId = type.getName() + "." + method.getName();
                if (!configuration.getMappedStatementNames().contains(mappedStatementId)) {
                    parseStatement(method);
                }
            }
        }
    }

    private void parseStatement(Method method) {
        Class<?> parameterType = getParameterType(method);
        LanguageDriver languageDriver = getLanguageDriver(method);
        SqlCommandType sqlCommandType = getSqlCommandType(method);
        final String mappedStatementId = type.getName() + "." + method.getName();
        //构建sql(核心)
        MapperHatSqlSource hatSqlSource = new MapperHatSqlSource(configuration, type, method, sqlCommandType, languageDriver);
        Integer fetchSize = null;
        Integer timeout = null;
        StatementType statementType = StatementType.PREPARED;
        ResultSetType resultSetType = configuration.getDefaultResultSetType();

        boolean isSelect = Objects.equals(sqlCommandType, SqlCommandType.SELECT);
        boolean flushCache = !isSelect;
        boolean useCache = isSelect;
        //新增sql语句占比较少,先初始化一个NoKeyGenerator默认值
        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
        String keyProperty = null;
        String keyColumn = null;
        if (SqlCommandType.INSERT.equals(sqlCommandType)) {
            SelectKey selectKey = method.getAnnotation(SelectKey.class);
            if (selectKey != null) {
                keyGenerator = handleSelectKeyAnnotation(selectKey, mappedStatementId, parameterType, languageDriver);
                keyProperty = selectKey.keyProperty();
            } else {
                //主要是为了把自增的id,赋值到对象中去
                if (configuration.isUseGeneratedKeys()) {
                    TableSchema tableSchema = hatSqlSource.getTableSchema();
                    switch (tableSchema.getDatabaseType()) {
                        case MYSQL:
                            //因为使用Jdbc3KeyGenerator需要指定keyProperties,但生成语句不方便指定此属性 ,所以还是用selectKey
                            selectKey = new SelectKeyEntity(new String[]{"SELECT LAST_INSERT_ID()"}, tableSchema.getPrimaryColumn().getColumnName(), false);
                            break;
                        case HSQL:
                            selectKey = new SelectKeyEntity(new String[]{"CALL IDENTITY()"}, tableSchema.getPrimaryColumn().getColumnName(), false);
                            break;
                        case ORACLE:
                        case POSTGRESQL:
                            //TODO PGSQL 主键自增长
//                            selectKey = new SelectKeyEntity(new String[]{"SELECT NEXTVAL('seq_table_name')"}, tableSchema.getPrimaryColumn().getColumnName(), false);
//                            break;
                        default:
                            log.warn("未匹配到insert语句主键策略,此数据库类型暂不支持主键自增id");
                            throw new UnsupportedException("暂未实现");
                    }
                }

                if (selectKey != null) {
                    keyGenerator = handleSelectKeyAnnotation(selectKey, mappedStatementId, parameterType, languageDriver);
                }
            }
        }
        Class<?> returnType = getReturnType(method);
        String resultMapId = parseResultMap(method, returnType);
        assistant.addMappedStatement(
                mappedStatementId,
                hatSqlSource,
                statementType,
                sqlCommandType,
                fetchSize,
                timeout,
                // ParameterMapID
                null,
                parameterType,
                resultMapId,
                returnType,
                resultSetType,
                flushCache,
                useCache,
                false,
                keyGenerator,
                keyProperty,
                keyColumn,
                // DatabaseID
                null,
                languageDriver,
                // ResultSets
                null);
    }


    private Class<?> getParameterType(Method method) {
        Class<?> parameterType = null;
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> currentParameterType : parameterTypes) {
            if (!RowBounds.class.isAssignableFrom(currentParameterType) && !ResultHandler.class.isAssignableFrom(currentParameterType)) {
                if (parameterType == null && !QuerySql.class.isAssignableFrom(currentParameterType)) {
                    parameterType = currentParameterType;
                } else {
                    // issue #135
                    parameterType = MapperMethod.ParamMap.class;
                }
            }
        }
        return parameterType;
    }

    private LanguageDriver getLanguageDriver(Method method) {
        Lang lang = method.getAnnotation(Lang.class);
        Class<? extends LanguageDriver> langClass = null;
        if (lang != null) {
            langClass = lang.value();
        }
        return configuration.getLanguageDriver(langClass);
    }

    private String parseResultMap(Method method, Class<?> returnType) {
        String resultMapId = generateResultMapName(method);
        applyResultMap(resultMapId, returnType);
        return resultMapId;
    }

    private void applyResultMap(String resultMapId, Class<?> returnType) {
        List<ResultMapping> resultMappings = new ArrayList<>();
        applyResults(returnType, resultMappings);
        assistant.addResultMap(resultMapId, returnType, null, null, resultMappings, true);
    }


    private void applyResults(Class<?> resultType, List<ResultMapping> resultMappings) {
        if(resultType.isPrimitive()){
            return;
        }
        while (resultType != null && !Objects.equals(resultType.getName(), Object.class.getName())) {
            Field[] fields = resultType.getDeclaredFields();
            resultType = resultType.getSuperclass();

            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                String fieldName = field.getName();

                String columnName;
                HatField hatField = field.getAnnotation(HatField.class);
                if (hatField != null) {
                    Object value = hatField.name();
                    columnName = value.toString().trim().length() == 0 ? null : value.toString();
                } else {
                    if (HatStringUtil.isLowerCase(fieldName)) {
                        continue;
                    }
                    columnName = HatStringUtil.toUnderline(fieldName).toUpperCase();
                }
                ResultMapping resultMapping = assistant.buildResultMapping(
                        resultType,
                        fieldName,
                        columnName,
                        field.getType(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        false);
                resultMappings.add(resultMapping);
            }
        }
    }
    private SqlCommandType getSqlCommandType(Method method) {
        HatMethod type = method.getAnnotation(HatMethod.class);
        if (type != null) {
            return type.type();
        }
        String methodName = method.getName();
        if (methodName.startsWith("update")) {
            return SqlCommandType.UPDATE;
        }
        if (methodName.startsWith("insert")) {
            return SqlCommandType.INSERT;
        }
        if (methodName.startsWith("select")) {
            return SqlCommandType.SELECT;
        }
        if (methodName.startsWith("delete")) {
            return SqlCommandType.DELETE;
        }
        return SqlCommandType.UNKNOWN;
    }

    private String generateResultMapName(Method method) {
        Results results = method.getAnnotation(Results.class);
        if (results != null && !results.id().isEmpty()) {
            return type.getName() + "." + results.id();
        }
        StringBuilder suffix = new StringBuilder();
        for (Class<?> c : method.getParameterTypes()) {
            suffix.append("-");
            suffix.append(c.getSimpleName());
        }
        if (suffix.length() < 1) {
            suffix.append("-void");
        }
        return type.getName() + "." + method.getName() + suffix;
    }

    private Class<?> getReturnType(Method method) {
        Class<?> returnType = method.getReturnType();
        Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, type);
        if (resolvedReturnType instanceof Class) {
            returnType = (Class<?>) resolvedReturnType;
            if (returnType.isArray()) {
                returnType = returnType.getComponentType();
            }
            // gcode issue #508
            if (void.class.equals(returnType)) {
                ResultType rt = method.getAnnotation(ResultType.class);
                if (rt != null) {
                    returnType = rt.value();
                }
            }
        } else if (resolvedReturnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) resolvedReturnType;
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if (Collection.class.isAssignableFrom(rawType) || Cursor.class.isAssignableFrom(rawType)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                    Type returnTypeParameter = actualTypeArguments[0];
                    if (returnTypeParameter instanceof Class<?>) {
                        returnType = (Class<?>) returnTypeParameter;
                    } else if (returnTypeParameter instanceof ParameterizedType) {
                        // (gcode issue #443) actual type can be a also a parameterized type
                        returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
                    } else if (returnTypeParameter instanceof GenericArrayType) {
                        Class<?> componentType = (Class<?>) ((GenericArrayType) returnTypeParameter).getGenericComponentType();
                        // (gcode issue #525) support List<byte[]>
                        returnType = Array.newInstance(componentType, 0).getClass();
                    }
                }
            } else if (Optional.class.equals(rawType)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Type returnTypeParameter = actualTypeArguments[0];
                if (returnTypeParameter instanceof Class<?>) {
                    returnType = (Class<?>) returnTypeParameter;
                }
            }
        }
        return returnType;
    }

//    private String nullOrEmpty(String value) {
//        return value == null || value.trim().length() == 0 ? null : value;
//    }

    private KeyGenerator handleSelectKeyAnnotation(SelectKey selectKeyAnnotation, String baseStatementId, Class<?> parameterTypeClass, LanguageDriver languageDriver) {
        String id = baseStatementId + SelectKeyGenerator.SELECT_KEY_SUFFIX;
        Class<?> resultTypeClass = selectKeyAnnotation.resultType();
        StatementType statementType = selectKeyAnnotation.statementType();
        String keyProperty = selectKeyAnnotation.keyProperty();
        String keyColumn = selectKeyAnnotation.keyColumn();
        boolean executeBefore = selectKeyAnnotation.before();

        // defaults
        boolean useCache = false;
        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
        Integer fetchSize = null;
        Integer timeout = null;
        boolean flushCache = false;
        String parameterMap = null;
        String resultMap = null;
        ResultSetType resultSetTypeEnum = null;

        SqlSource sqlSource = buildSqlSourceFromStrings(selectKeyAnnotation.statement(), parameterTypeClass, languageDriver);
        SqlCommandType sqlCommandType = SqlCommandType.SELECT;

        assistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType, fetchSize, timeout, parameterMap, parameterTypeClass, resultMap, resultTypeClass, resultSetTypeEnum,
                flushCache, useCache, false,
                keyGenerator, keyProperty, keyColumn, null, languageDriver, null);

        id = assistant.applyCurrentNamespace(id, false);

        MappedStatement keyStatement = configuration.getMappedStatement(id, false);
        SelectKeyGenerator answer = new SelectKeyGenerator(keyStatement, executeBefore);
        configuration.addKeyGenerator(id, answer);
        return answer;
    }

    private SqlSource buildSqlSourceFromStrings(String[] strings, Class<?> parameterTypeClass, LanguageDriver languageDriver) {
        final StringBuilder sql = new StringBuilder();
        for (String fragment : strings) {
            sql.append(fragment);
            sql.append(" ");
        }
        return languageDriver.createSqlSource(configuration, sql.toString().trim(), parameterTypeClass);
    }
}

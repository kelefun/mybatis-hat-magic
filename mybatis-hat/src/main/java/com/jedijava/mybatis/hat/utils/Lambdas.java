package com.jedijava.mybatis.hat.utils;

import com.jedijava.mybatis.hat.override.HatFunction;
import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*
 *
 * @author liukaiyang
 * @since 2019/9/27 14:17
 */
@Slf4j
public class Lambdas {
    private Lambdas() {
    }
    /**
     * 获取lambda表达式的方法名称
     *
     * @param function
     * @return
     */
    public static String fnToName(HatFunction function) {
        try {
            Method method = function.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            Object obj = method.invoke(function);
            SerializedLambda serializedLambda = (SerializedLambda) obj;
            return HatStringUtil.methodToFieldName(serializedLambda.getImplMethodName());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error("获取方法名称异常", e);
        }
        return null;
    }


}

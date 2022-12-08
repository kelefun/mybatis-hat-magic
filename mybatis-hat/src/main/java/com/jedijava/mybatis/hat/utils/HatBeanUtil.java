package com.jedijava.mybatis.hat.utils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

/**
 * @author liukaiyang
 * @since 2019/9/3 18:11
 */
public class HatBeanUtil {

    public static <T> T copyProperties(Object orig, Class<T> clazz) {
        if (orig == null) return null;
        try {
            T target = clazz.newInstance();
            return copyProperties(orig, target);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T copyProperties(Object original, T target) {
        if (target == null || original == null) return target;

        if (original instanceof Map) {
            Map map = ((Map) original);
            for (Object o : map.keySet()) {
                setProperty(target, (String) o, map.get(o));
            }
        } else {
            Class<?> clazz = original.getClass();
            while (clazz != null && !Objects.equals(clazz.getName(), Object.class.getName())) {
                Field[] fields = clazz.getDeclaredFields();
                clazz = clazz.getSuperclass();
                for (Field originalField : fields) {
                    Object value = null;
                    try {
                        originalField.setAccessible(true);
                        value = originalField.get(original);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    setProperty(target, originalField.getName(), value);
                }
            }
        }
        return target;
    }

    /**
     * 给对象某属性赋值
     */
    public static void setProperty(Object bean, String fieldName, Object fieldValue) {
        if (fieldValue == null) return;
        Class<?> clazz = bean.getClass();
        while (clazz != null && !Objects.equals(clazz.getName(), Object.class.getName())) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    try {
                        if (field.getType().isInstance(fieldValue)) {
                            field.setAccessible(true);
                            field.set(bean, fieldValue);
                        } else if (field.getType().isPrimitive()
                                && field.getType().hashCode() == fieldValue.getClass().hashCode()) {
                            field.setAccessible(true);
                            field.set(bean, fieldValue);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}

package com.jedijava.mybatis.hat.utils;

import java.util.Collection;
import java.util.Map;

/**
 * @author liukaiyang
 * @since 2019/8/26 21:33
 */
public final class CollectionUtil {

    /**
     * map是否为空
     */
    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    /**
     * map是否不为空
     */
    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

}

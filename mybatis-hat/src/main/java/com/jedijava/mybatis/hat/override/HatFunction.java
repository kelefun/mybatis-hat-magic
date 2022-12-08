package com.jedijava.mybatis.hat.override;

import java.io.Serializable;
import java.util.function.Function;

/*
 *
 * @author liukaiyang
 * @since 2019/9/27 14:25
 */
public interface HatFunction<T, R> extends Function<T, R>, Serializable {
}

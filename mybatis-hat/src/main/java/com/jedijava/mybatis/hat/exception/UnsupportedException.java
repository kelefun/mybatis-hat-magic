package com.jedijava.mybatis.hat.exception;

import org.apache.ibatis.exceptions.PersistenceException;

/**
 * @author liukaiyang
 * @since 2019/8/29 12:16
 */
public class UnsupportedException extends PersistenceException {

    private static final long serialVersionUID = 3379733458031244536L;

    public UnsupportedException() {
        super();
    }

    public UnsupportedException(String message) {
        super(message);
    }

    public UnsupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedException(Throwable cause) {
        super(cause);
    }
}
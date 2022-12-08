package com.jedijava.mybatis.hat.exception;

import org.apache.ibatis.exceptions.PersistenceException;

/**
 * @author liukaiyang
 * @since 2019/8/29 12:16
 */
public class SqlException extends PersistenceException {

    private static final long serialVersionUID = 3379733458031244536L;

    public SqlException() {
        super();
    }

    public SqlException(String message) {
        super(message);
    }

    public SqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlException(Throwable cause) {
        super(cause);
    }
}
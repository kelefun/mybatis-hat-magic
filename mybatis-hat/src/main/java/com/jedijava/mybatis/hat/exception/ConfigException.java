package com.jedijava.mybatis.hat.exception;

import org.apache.ibatis.exceptions.PersistenceException;

/**
 * @author liukaiyang
 * @since 2019/8/29 12:16
 */
public class ConfigException extends PersistenceException {

    private static final long serialVersionUID = 3379733458031244536L;

    public ConfigException() {
        super();
    }

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigException(Throwable cause) {
        super(cause);
    }
}
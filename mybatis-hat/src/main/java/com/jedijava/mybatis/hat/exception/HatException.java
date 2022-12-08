package com.jedijava.mybatis.hat.exception;

import org.apache.ibatis.exceptions.PersistenceException;

/**
 * @author liukaiyang
 * @since 2019/8/29 12:16
 */
public class HatException extends PersistenceException {

    private static final long serialVersionUID = 3379733458031244536L;

    public HatException() {
        super();
    }

    public HatException(String message) {
        super(message);
    }

    public HatException(String message, Throwable cause) {
        super(message, cause);
    }

    public HatException(Throwable cause) {
        super(cause);
    }
}
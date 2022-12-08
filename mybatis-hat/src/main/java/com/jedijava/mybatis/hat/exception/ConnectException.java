package com.jedijava.mybatis.hat.exception;

import org.apache.ibatis.exceptions.PersistenceException;

/**
 * @author liukaiyang
 * @since 2022/12/06 12:16
 */
public class ConnectException extends PersistenceException {


    public ConnectException() {
        super();
    }

    public ConnectException(String message) {
        super(message);
    }

    public ConnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectException(Throwable cause) {
        super(cause);
    }
}
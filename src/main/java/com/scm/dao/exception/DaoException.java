package com.scm.dao.exception;

/**
 * Custom exception for DAO layer errors
 */
public class DaoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new DaoException with the specified detail message.
     * 
     * @param message the detail message
     */
    public DaoException(String message) {
        super(message);
    }

    /**
     * Constructs a new DaoException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DaoException with the specified cause.
     * 
     * @param cause the cause
     */
    public DaoException(Throwable cause) {
        super(cause);
    }
} 
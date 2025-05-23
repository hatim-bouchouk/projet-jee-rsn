package com.scm.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for handling exceptions and providing user-friendly error messages.
 */
public class ExceptionHandler {
    
    private static final Logger LOGGER = Logger.getLogger(ExceptionHandler.class.getName());
    private static final Map<Class<? extends Exception>, String> ERROR_MESSAGES = new HashMap<>();
    
    // Initialize error messages map
    static {
        // Database related exceptions
        ERROR_MESSAGES.put(javax.persistence.PersistenceException.class, "Database operation failed. Please try again later.");
        ERROR_MESSAGES.put(org.hibernate.exception.JDBCConnectionException.class, "Unable to connect to the database. Please try again later.");
        ERROR_MESSAGES.put(org.hibernate.exception.ConstraintViolationException.class, "Data validation failed. Please check your input.");
        
        // Authentication/Authorization exceptions
        ERROR_MESSAGES.put(javax.security.auth.login.LoginException.class, "Authentication failed. Please check your credentials.");
        ERROR_MESSAGES.put(java.security.AccessControlException.class, "You don't have permission to perform this action.");
        
        // General exceptions
        ERROR_MESSAGES.put(java.lang.IllegalArgumentException.class, "Invalid input provided. Please check your data.");
        ERROR_MESSAGES.put(java.lang.NullPointerException.class, "System error occurred. Please contact support.");
        ERROR_MESSAGES.put(java.io.IOException.class, "File operation failed. Please try again.");
    }
    
    // Private constructor to prevent instantiation
    private ExceptionHandler() {
    }
    
    /**
     * Handle an exception by logging it and returning a user-friendly message.
     * 
     * @param exception the exception to handle
     * @return a user-friendly error message
     */
    public static String handleException(Exception exception) {
        logException(exception);
        return getUserFriendlyMessage(exception);
    }
    
    /**
     * Handle a Throwable by logging it and returning a user-friendly message.
     * This method is useful for handling errors like OutOfMemoryError.
     * 
     * @param throwable the throwable to handle
     * @return a user-friendly error message
     */
    public static String handleThrowable(Throwable throwable) {
        logThrowable(throwable);
        
        // If in development mode, return more details
        if (AppConfig.isDevelopmentProfile()) {
            return "Error: " + throwable.getMessage();
        }
        
        // Default generic message for production
        return "A critical system error occurred. Please contact support.";
    }
    
    /**
     * Log an exception with appropriate level and details.
     * 
     * @param exception the exception to log
     */
    public static void logException(Exception exception) {
        if (isServerError(exception)) {
            LOGGER.log(Level.SEVERE, "Server error occurred", exception);
        } else if (isClientError(exception)) {
            LOGGER.log(Level.WARNING, "Client error occurred: " + exception.getMessage(), exception);
        } else {
            LOGGER.log(Level.INFO, "Exception occurred: " + exception.getMessage(), exception);
        }
    }
    
    /**
     * Log a throwable with severe level.
     * 
     * @param throwable the throwable to log
     */
    public static void logThrowable(Throwable throwable) {
        LOGGER.log(Level.SEVERE, "Critical error occurred", throwable);
    }
    
    /**
     * Get a user-friendly error message for an exception.
     * 
     * @param exception the exception
     * @return a user-friendly error message
     */
    public static String getUserFriendlyMessage(Exception exception) {
        // Check if we have a specific message for this exception type
        for (Map.Entry<Class<? extends Exception>, String> entry : ERROR_MESSAGES.entrySet()) {
            if (entry.getKey().isInstance(exception)) {
                return entry.getValue();
            }
        }
        
        // If in development mode, return more details
        if (AppConfig.isDevelopmentProfile()) {
            return "Error: " + exception.getMessage();
        }
        
        // Default generic message for production
        return "An unexpected error occurred. Please try again later.";
    }
    
    /**
     * Determine if an exception represents a server error (5xx).
     * 
     * @param exception the exception to check
     * @return true if it's a server error
     */
    private static boolean isServerError(Exception exception) {
        return exception instanceof NullPointerException ||
               exception instanceof org.hibernate.exception.JDBCConnectionException;
    }
    
    /**
     * Determine if an exception represents a client error (4xx).
     * 
     * @param exception the exception to check
     * @return true if it's a client error
     */
    private static boolean isClientError(Exception exception) {
        return exception instanceof IllegalArgumentException ||
               exception instanceof IllegalStateException ||
               exception instanceof javax.security.auth.login.LoginException ||
               exception instanceof java.security.AccessControlException ||
               exception instanceof org.hibernate.exception.ConstraintViolationException;
    }
} 
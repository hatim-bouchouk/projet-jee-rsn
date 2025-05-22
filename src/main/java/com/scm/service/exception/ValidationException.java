package com.scm.service.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception for validation errors in service layer.
 */
public class ValidationException extends ServiceException {

    private static final long serialVersionUID = 1L;
    
    private final Map<String, String> validationErrors = new HashMap<>();

    /**
     * Constructs a new ValidationException with the specified detail message.
     * 
     * @param message the detail message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new ValidationException with the specified detail message and validation errors.
     * 
     * @param message the detail message
     * @param validationErrors map of field names to error messages
     */
    public ValidationException(String message, Map<String, String> validationErrors) {
        super(message);
        if (validationErrors != null) {
            this.validationErrors.putAll(validationErrors);
        }
    }

    /**
     * Add a validation error.
     * 
     * @param field the field name
     * @param errorMessage the error message
     * @return this ValidationException for method chaining
     */
    public ValidationException addValidationError(String field, String errorMessage) {
        validationErrors.put(field, errorMessage);
        return this;
    }

    /**
     * Get all validation errors.
     * 
     * @return map of field names to error messages
     */
    public Map<String, String> getValidationErrors() {
        return new HashMap<>(validationErrors);
    }

    /**
     * Check if there are any validation errors.
     * 
     * @return true if there are validation errors, false otherwise
     */
    public boolean hasErrors() {
        return !validationErrors.isEmpty();
    }
} 
package com.scm.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for loading database configuration properties.
 * This class provides access to database connection settings and other
 * configuration parameters defined in the database.properties file.
 */
public class DatabaseConfig {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    private static final String PROPERTIES_FILE = "database.properties";
    private static final Properties properties = new Properties();
    private static boolean isInitialized = false;
    
    // Private constructor to prevent instantiation
    private DatabaseConfig() {
    }
    
    /**
     * Initialize the configuration by loading properties from file.
     * This method is automatically called when the class is first accessed.
     */
    private static synchronized void initialize() {
        if (isInitialized) {
            return;
        }
        
        try (InputStream inputStream = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            
            if (inputStream == null) {
                LOGGER.log(Level.SEVERE, "Unable to find {0} file", PROPERTIES_FILE);
                throw new RuntimeException("Unable to find " + PROPERTIES_FILE);
            }
            
            properties.load(inputStream);
            isInitialized = true;
            LOGGER.log(Level.INFO, "Database configuration loaded successfully");
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading database configuration", e);
            throw new RuntimeException("Error loading database configuration", e);
        }
    }
    
    /**
     * Get a property value by key.
     * 
     * @param key the property key
     * @return the property value
     */
    public static String getProperty(String key) {
        if (!isInitialized) {
            initialize();
        }
        return properties.getProperty(key);
    }
    
    /**
     * Get a property value by key with a default value if not found.
     * 
     * @param key the property key
     * @param defaultValue the default value to return if key is not found
     * @return the property value or default value
     */
    public static String getProperty(String key, String defaultValue) {
        if (!isInitialized) {
            initialize();
        }
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Get a property as boolean.
     * 
     * @param key the property key
     * @param defaultValue the default value to return if key is not found
     * @return the property value as boolean
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    /**
     * Get a property as integer.
     * 
     * @param key the property key
     * @param defaultValue the default value to return if key is not found
     * @return the property value as integer
     */
    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        try {
            return (value != null) ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid integer property value for {0}: {1}", new Object[]{key, value});
            return defaultValue;
        }
    }
    
    /**
     * Check if database initialization is enabled.
     * 
     * @return true if database initialization is enabled
     */
    public static boolean isDatabaseInitEnabled() {
        return getBooleanProperty("app.init.database", false);
    }
    
    /**
     * Check if sample data initialization is enabled.
     * 
     * @return true if sample data initialization is enabled
     */
    public static boolean isSampleDataInitEnabled() {
        return getBooleanProperty("app.init.sample_data", false);
    }
    
    /**
     * Get the current environment (development, test, production).
     * 
     * @return the current environment
     */
    public static String getEnvironment() {
        return getProperty("environment", "development");
    }
    
    /**
     * Check if the current environment is development.
     * 
     * @return true if the current environment is development
     */
    public static boolean isDevelopmentEnvironment() {
        return "development".equalsIgnoreCase(getEnvironment());
    }
} 
package com.scm.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application configuration utility class that loads properties from the
 * application.properties file and profile-specific properties files.
 */
public class AppConfig {
    
    private static final Logger LOGGER = Logger.getLogger(AppConfig.class.getName());
    private static final String BASE_PROPERTIES_FILE = "application.properties";
    private static final Properties properties = new Properties();
    private static boolean isInitialized = false;
    private static String activeProfile;
    
    // Private constructor to prevent instantiation
    private AppConfig() {
    }
    
    /**
     * Initialize the configuration by loading properties from files.
     * This method is automatically called when the class is first accessed.
     */
    private static synchronized void initialize() {
        if (isInitialized) {
            return;
        }
        
        try {
            // Load base properties first
            loadPropertiesFile(BASE_PROPERTIES_FILE);
            
            // Determine active profile
            activeProfile = getProperty("spring.profiles.active", "development");
            
            // Load profile-specific properties (these will override base properties)
            String profilePropertiesFile = "application-" + activeProfile + ".properties";
            loadPropertiesFile(profilePropertiesFile);
            
            isInitialized = true;
            LOGGER.log(Level.INFO, "Application configuration loaded successfully. Active profile: {0}", activeProfile);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading application configuration", e);
            throw new RuntimeException("Error loading application configuration", e);
        }
    }
    
    /**
     * Load properties from a specific file.
     * 
     * @param filename the properties file name
     * @throws IOException if an error occurs loading the file
     */
    private static void loadPropertiesFile(String filename) throws IOException {
        try (InputStream inputStream = AppConfig.class.getClassLoader().getResourceAsStream(filename)) {
            if (inputStream == null) {
                LOGGER.log(Level.WARNING, "Unable to find {0} file", filename);
                return;
            }
            
            Properties fileProps = new Properties();
            fileProps.load(inputStream);
            
            // Add all properties to the main properties object
            properties.putAll(fileProps);
            LOGGER.log(Level.INFO, "Loaded configuration from {0}", filename);
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
     * Get a property as long.
     * 
     * @param key the property key
     * @param defaultValue the default value to return if key is not found
     * @return the property value as long
     */
    public static long getLongProperty(String key, long defaultValue) {
        String value = getProperty(key);
        try {
            return (value != null) ? Long.parseLong(value) : defaultValue;
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid long property value for {0}: {1}", new Object[]{key, value});
            return defaultValue;
        }
    }
    
    /**
     * Get the active profile.
     * 
     * @return the active profile name
     */
    public static String getActiveProfile() {
        if (!isInitialized) {
            initialize();
        }
        return activeProfile;
    }
    
    /**
     * Check if the current profile is development.
     * 
     * @return true if the current profile is development
     */
    public static boolean isDevelopmentProfile() {
        return "development".equals(getActiveProfile());
    }
    
    /**
     * Check if the current profile is production.
     * 
     * @return true if the current profile is production
     */
    public static boolean isProductionProfile() {
        return "production".equals(getActiveProfile());
    }
    
    /**
     * Check if the current profile is test.
     * 
     * @return true if the current profile is test
     */
    public static boolean isTestProfile() {
        return "test".equals(getActiveProfile());
    }
    
    /**
     * Reload all configuration properties.
     * This can be useful when properties have been changed at runtime.
     */
    public static synchronized void reload() {
        isInitialized = false;
        properties.clear();
        initialize();
    }
} 
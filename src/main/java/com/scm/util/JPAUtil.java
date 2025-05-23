package com.scm.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Utility class for JPA operations
 */
public class JPAUtil {
    private static final Logger LOGGER = Logger.getLogger(JPAUtil.class.getName());
    private static final String PERSISTENCE_UNIT_NAME = "scmPU";
    private static EntityManagerFactory factory;
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 2000; // 2 seconds
    
    /**
     * Initialize the EntityManagerFactory
     * 
     * @return true if initialization was successful, false otherwise
     */
    public static synchronized boolean initialize() {
        return initialize(null);
    }
    
    /**
     * Initialize the EntityManagerFactory with custom properties
     * 
     * @param properties Additional properties to override default configuration
     * @return true if initialization was successful, false otherwise
     */
    public static synchronized boolean initialize(Map<String, Object> properties) {
        if (factory != null && factory.isOpen()) {
            return true;
        }
        
        int attempts = 0;
        boolean success = false;
        
        while (!success && attempts < MAX_RETRY_ATTEMPTS) {
            attempts++;
            
            try {
                LOGGER.log(Level.INFO, "Initializing EntityManagerFactory for persistence unit: {0} (Attempt {1}/{2})", 
                        new Object[]{PERSISTENCE_UNIT_NAME, attempts, MAX_RETRY_ATTEMPTS});
                
                Map<String, Object> configProperties = getConfigurationProperties();
                
                // Override with custom properties if provided
                if (properties != null) {
                    configProperties.putAll(properties);
                }
                
                factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, configProperties);
                LOGGER.log(Level.INFO, "EntityManagerFactory created successfully");
                success = true;
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error creating EntityManagerFactory (Attempt " + attempts + "): " + e.getMessage(), e);
                
                if (attempts < MAX_RETRY_ATTEMPTS) {
                    LOGGER.log(Level.INFO, "Retrying in {0} ms...", RETRY_DELAY_MS);
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        return success;
    }
    
    /**
     * Get configuration properties from database.properties
     * 
     * @return Map of configuration properties
     */
    private static Map<String, Object> getConfigurationProperties() {
        Map<String, Object> properties = new HashMap<>();
        
        // Use properties from DatabaseConfig
        if (DatabaseConfig.isDevelopmentEnvironment()) {
            // Development environment specific settings
            properties.put("hibernate.show_sql", "true");
            properties.put("hibernate.format_sql", "true");
            properties.put("hibernate.hbm2ddl.auto", "update");
        } else {
            // Production environment settings
            properties.put("hibernate.show_sql", "false");
            properties.put("hibernate.format_sql", "false");
            properties.put("hibernate.hbm2ddl.auto", "validate");
        }
        
        // JDBC connection properties (as fallback if JNDI is not available)
        properties.put("javax.persistence.jdbc.driver", DatabaseConfig.getProperty("db.driver"));
        properties.put("javax.persistence.jdbc.url", DatabaseConfig.getProperty("db.url"));
        properties.put("javax.persistence.jdbc.user", DatabaseConfig.getProperty("db.username"));
        properties.put("javax.persistence.jdbc.password", DatabaseConfig.getProperty("db.password"));
        
        // Connection pool settings
        properties.put("hibernate.hikari.minimumIdle", 
                String.valueOf(DatabaseConfig.getIntProperty("db.pool.minIdle", 5)));
        properties.put("hibernate.hikari.maximumPoolSize", 
                String.valueOf(DatabaseConfig.getIntProperty("db.pool.maxTotal", 20)));
        properties.put("hibernate.hikari.idleTimeout", 
                String.valueOf(DatabaseConfig.getIntProperty("db.pool.maxWaitMillis", 30000)));
        
        // Timezone handling
        properties.put("hibernate.jdbc.time_zone", "UTC");
        
        return properties;
    }
    
    /**
     * Get a new EntityManager instance
     * 
     * @return EntityManager instance
     */
    public static EntityManager getEntityManager() {
        if (factory == null || !factory.isOpen()) {
            initialize();
        }
        
        if (factory == null) {
            throw new IllegalStateException("EntityManagerFactory is not initialized");
        }
        
        return factory.createEntityManager();
    }
    
    /**
     * Close the EntityManagerFactory
     */
    public static void closeEntityManagerFactory() {
        if (factory != null && factory.isOpen()) {
            factory.close();
            LOGGER.log(Level.INFO, "EntityManagerFactory closed successfully");
        }
    }
    
    /**
     * Check if the database connection is valid
     * 
     * @return true if connection is valid, false otherwise
     */
    public static boolean isConnectionValid() {
        if (factory == null || !factory.isOpen()) {
            return false;
        }
        
        EntityManager em = null;
        try {
            em = factory.createEntityManager();
            em.createNativeQuery("SELECT 1").getSingleResult();
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Database connection validation failed", e);
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
} 
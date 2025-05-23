package com.scm.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.scm.model.User;
import com.scm.model.User.Role;

/**
 * Database initializer that runs when the application starts.
 * This class handles database schema creation/update and sample data initialization.
 */
@WebListener
public class DatabaseInitializer implements ServletContextListener {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());
    private static final String PERSISTENCE_UNIT_NAME = "scmPU";
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.log(Level.INFO, "Initializing database...");
        
        try {
            // Check if database initialization is enabled
            if (DatabaseConfig.isDatabaseInitEnabled()) {
                initializeDatabase();
            } else {
                LOGGER.log(Level.INFO, "Database initialization is disabled. Skipping.");
            }
            
            // Initialize the JPAUtil
            boolean jpaInitialized = JPAUtil.initialize();
            if (!jpaInitialized) {
                LOGGER.log(Level.SEVERE, "Failed to initialize JPA. Application may not function correctly.");
            }
            
            // Check if sample data initialization is enabled
            if (DatabaseConfig.isSampleDataInitEnabled() && jpaInitialized) {
                initializeSampleData();
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during database initialization", e);
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.log(Level.INFO, "Shutting down database connections...");
        JPAUtil.closeEntityManagerFactory();
    }
    
    /**
     * Initialize the database schema.
     */
    private void initializeDatabase() {
        LOGGER.log(Level.INFO, "Creating/updating database schema...");
        
        EntityManagerFactory emf = null;
        EntityManager em = null;
        
        try {
            // Create a special configuration for schema creation
            Map<String, Object> properties = new HashMap<>();
            properties.put("hibernate.hbm2ddl.auto", "update");
            properties.put("hibernate.show_sql", "true");
            
            // Use properties from DatabaseConfig
            properties.put("javax.persistence.jdbc.driver", DatabaseConfig.getProperty("db.driver"));
            properties.put("javax.persistence.jdbc.url", DatabaseConfig.getProperty("db.url"));
            properties.put("javax.persistence.jdbc.user", DatabaseConfig.getProperty("db.username"));
            properties.put("javax.persistence.jdbc.password", DatabaseConfig.getProperty("db.password"));
            
            // Create a temporary EMF just for schema initialization
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
            em = emf.createEntityManager();
            
            // Test the connection
            em.createNativeQuery("SELECT 1").getSingleResult();
            
            LOGGER.log(Level.INFO, "Database schema created/updated successfully");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating/updating database schema", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }
    
    /**
     * Initialize sample data in the database.
     */
    private void initializeSampleData() {
        LOGGER.log(Level.INFO, "Initializing sample data...");
        
        EntityManager em = null;
        
        try {
            em = JPAUtil.getEntityManager();
            
            // Begin transaction
            em.getTransaction().begin();
            
            // Check if data already exists
            Long userCount = (Long) em.createQuery("SELECT COUNT(u) FROM User u").getSingleResult();
            
            if (userCount == 0) {
                LOGGER.log(Level.INFO, "No existing users found. Creating sample data...");
                
                // Execute the SQL script
                executeSqlScript(em);
                
                LOGGER.log(Level.INFO, "Sample data created successfully");
            } else {
                LOGGER.log(Level.INFO, "Sample data already exists. Skipping initialization.");
            }
            
            // Commit transaction
            em.getTransaction().commit();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing sample data", e);
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    /**
     * Execute the SQL script to initialize sample data
     * 
     * @param em EntityManager to use for executing SQL
     */
    private void executeSqlScript(EntityManager em) {
        try {
            LOGGER.log(Level.INFO, "Executing SQL script for sample data initialization...");
            
            // Load the SQL script from resources using standard Java I/O
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("database/init_data.sql");
            if (inputStream == null) {
                LOGGER.log(Level.SEVERE, "Could not find init_data.sql in classpath");
                return;
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            
            StringBuilder sqlBatch = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                // Skip comments and empty lines
                if (line.trim().isEmpty() || line.trim().startsWith("--")) {
                    continue;
                }
                
                sqlBatch.append(line);
                
                // Execute when we find a semicolon
                if (line.trim().endsWith(";")) {
                    String sql = sqlBatch.toString();
                    
                    try {
                        em.createNativeQuery(sql).executeUpdate();
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Error executing SQL: " + sql, e);
                    }
                    
                    // Reset for the next statement
                    sqlBatch = new StringBuilder();
                }
            }
            
            reader.close();
            LOGGER.log(Level.INFO, "SQL script executed successfully");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing SQL script", e);
        }
    }
    
    /**
     * Create default admin user if no users exist
     * This method is not used as we're using SQL script instead
     */
    private void createDefaultAdminUser(EntityManager em) {
        try {
            LOGGER.log(Level.INFO, "Creating default admin user...");
            
            // Create admin user with pre-hashed password
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword("$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS"); // admin123
            adminUser.setEmail("admin@scm.com");
            adminUser.setRole(Role.admin);
            
            // Save the user
            em.persist(adminUser);
            
            // Create manager user with pre-hashed password
            User managerUser = new User();
            managerUser.setUsername("manager");
            managerUser.setPassword("$2a$10$FKdvC4NZ5/3hx.A.Z.yfO.KeUuV.crs5g4eAoR.I6NQnoGiIUCnxy"); // manager123
            managerUser.setEmail("manager@scm.com");
            managerUser.setRole(Role.manager);
            
            // Save the user
            em.persist(managerUser);
            
            // Create client user with pre-hashed password
            User clientUser = new User();
            clientUser.setUsername("client");
            clientUser.setPassword("$2a$10$UYrGNY9bT2TQC8A.Ql8jR.Xf0aYLxFG4KgbBy5GqB.NJA1CV1nKZ2"); // client123
            clientUser.setEmail("client@scm.com");
            clientUser.setRole(Role.user);
            
            // Save the user
            em.persist(clientUser);
            
            LOGGER.log(Level.INFO, "Default users created successfully");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating default users", e);
        }
    }
} 
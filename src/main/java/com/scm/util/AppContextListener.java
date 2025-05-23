package com.scm.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application context listener that initializes and closes the EntityManagerFactory
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Initialize EntityManagerFactory
        System.out.println("Initializing EntityManagerFactory from AppContextListener");
        if (JPAUtil.initialize()) {
            System.out.println("EntityManagerFactory initialized successfully from AppContextListener");
        } else {
            System.err.println("Failed to initialize EntityManagerFactory from AppContextListener");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Close EntityManagerFactory
        try {
            JPAUtil.closeEntityManagerFactory();
            System.out.println("EntityManagerFactory closed successfully from AppContextListener");
        } catch (Exception e) {
            System.err.println("Error closing EntityManagerFactory from AppContextListener: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 
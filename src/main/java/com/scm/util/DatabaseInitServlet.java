package com.scm.util;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that initializes the database
 */
@WebServlet(urlPatterns = "/init-db", loadOnStartup = 1)
public class DatabaseInitServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static boolean initialized = false;

    @Override
    public void init() throws ServletException {
        System.out.println("DatabaseInitServlet: Initializing database...");
        
        if (initialized) {
            System.out.println("DatabaseInitServlet: Database already initialized, skipping");
            return;
        }
        
        // Make sure EntityManagerFactory is initialized
        if (!JPAUtil.initialize()) {
            throw new ServletException("Failed to initialize EntityManagerFactory");
        }
        
        EntityManager em = null;
        EntityTransaction tx = null;
        
        try {
            em = JPAUtil.getEntityManager();
            tx = em.getTransaction();
            tx.begin();
            
            // Add any initialization logic here
            // For example, check if admin user exists, if not create it
            
            tx.commit();
            initialized = true;
            System.out.println("DatabaseInitServlet: Database initialized successfully");
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println("DatabaseInitServlet: Database initialization failed: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Database initialization failed", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.getWriter().append("Database initialization status: " + (initialized ? "Success" : "Not initialized"));
    }
} 
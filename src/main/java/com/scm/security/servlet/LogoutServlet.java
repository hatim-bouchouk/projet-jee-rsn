package com.scm.security.servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.scm.security.model.UserPrincipal;
import com.scm.security.util.SessionManager;

/**
 * Servlet for handling user logout.
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LogoutServlet.class.getName());
    
    /**
     * Handles GET requests for user logout.
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @throws ServletException If an error occurs during request handling
     * @throws IOException If an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get current user before logging out
        UserPrincipal currentUser = SessionManager.getCurrentUser(request);
        
        // Invalidate the session
        SessionManager.invalidateSession(request);
        
        if (currentUser != null) {
            LOGGER.log(Level.INFO, "User {0} logged out successfully", currentUser.getUsername());
        }
        
        // Redirect to login page with a logout message
        response.sendRedirect(request.getContextPath() + "/login?message=You have been logged out successfully");
    }
    
    /**
     * Handles POST requests for user logout (useful for logout forms).
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @throws ServletException If an error occurs during request handling
     * @throws IOException If an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Just delegate to doGet
        doGet(request, response);
    }
} 
package com.scm.controller;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.scm.security.model.UserPrincipal;
import com.scm.security.util.SessionManager;

/**
 * Base servlet class with common functionality for all controllers.
 */
public abstract class BaseServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    protected final Logger logger = Logger.getLogger(getClass().getName());
    
    /**
     * Get the current authenticated user
     * 
     * @param request The HTTP request
     * @return The authenticated user, or null if not authenticated
     */
    protected UserPrincipal getCurrentUser(HttpServletRequest request) {
        return SessionManager.getCurrentUser(request);
    }
    
    /**
     * Check if the current user has the specified permission
     * 
     * @param request The HTTP request
     * @param permission The permission to check
     * @return true if the user has the permission, false otherwise
     */
    protected boolean hasPermission(HttpServletRequest request, String permission) {
        UserPrincipal user = getCurrentUser(request);
        return user != null && user.hasPermission(permission);
    }
    
    /**
     * Check if the current user has any of the specified roles
     * 
     * @param request The HTTP request
     * @param roles The roles to check
     * @return true if the user has any of the roles, false otherwise
     */
    protected boolean hasAnyRole(HttpServletRequest request, String... roles) {
        UserPrincipal user = getCurrentUser(request);
        return user != null && user.hasAnyRole(roles);
    }
    
    /**
     * Redirect to the login page if the user is not authenticated
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @return true if the user is authenticated, false if redirected to login
     * @throws IOException If an I/O error occurs
     */
    protected boolean requireLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (getCurrentUser(request) == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }
    
    /**
     * Redirect to the access denied page if the user doesn't have the specified permission
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @param permission The permission to check
     * @return true if the user has the permission, false if redirected to access denied
     * @throws IOException If an I/O error occurs
     */
    protected boolean requirePermission(HttpServletRequest request, HttpServletResponse response, String permission) 
            throws IOException {
        if (!requireLogin(request, response)) {
            return false;
        }
        
        if (!hasPermission(request, permission)) {
            response.sendRedirect(request.getContextPath() + "/access-denied");
            return false;
        }
        
        return true;
    }
    
    /**
     * Set a message attribute in the request
     * 
     * @param request The HTTP request
     * @param message The message to set
     */
    protected void setMessage(HttpServletRequest request, String message) {
        request.setAttribute("message", message);
    }
    
    /**
     * Set an error attribute in the request
     * 
     * @param request The HTTP request
     * @param error The error message to set
     */
    protected void setError(HttpServletRequest request, String error) {
        request.setAttribute("error", error);
    }
    
    /**
     * Forward to a JSP view
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @param viewPath The path to the JSP view
     * @throws ServletException If an error occurs during forwarding
     * @throws IOException If an I/O error occurs
     */
    protected void forwardToView(HttpServletRequest request, HttpServletResponse response, String viewPath) 
            throws ServletException, IOException {
        request.getRequestDispatcher(viewPath).forward(request, response);
    }
} 
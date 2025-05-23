package com.scm.security.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.scm.security.model.UserPrincipal;
import com.scm.security.util.SessionManager;

/**
 * Filter for authentication and authorization.
 * Intercepts all requests to secure resources and validates user authentication and authorization.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class.getName());
    
    // Paths that don't require authentication
    private static final Set<String> PUBLIC_PATHS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("/login", "/logout", "/register", "/error", "/access-denied", "/resources", "/assets", 
                        "/index.jsp", "/jsp/common", "/.js", "/.css", "/.png", "/.jpg", "/.gif", "/favicon.ico")));
    
    // Resources that require specific roles
    private static final Set<String> ADMIN_PATHS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("/admin", "/users", "/settings")));
    
    private static final Set<String> MANAGER_PATHS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("/reports", "/dashboard")));
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("AuthenticationFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestPath = getRequestPath(httpRequest);
        
        // Check if this path is public
        if (isPublicPath(requestPath)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Validate session and authentication
        if (!SessionManager.isSessionActive(httpRequest)) {
            // Session is not active, redirect to login
            LOGGER.log(Level.INFO, "Inactive session detected. Redirecting to login.");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?redirect=" + requestPath);
            return;
        }
        
        // Get current user from session
        UserPrincipal currentUser = SessionManager.getCurrentUser(httpRequest);
        
        // Validate authorization for protected paths
        if (!isAuthorized(currentUser, requestPath)) {
            LOGGER.log(Level.WARNING, "Access denied for user {0} trying to access {1}", 
                    new Object[]{currentUser.getUsername(), requestPath});
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/access-denied");
            return;
        }
        
        // All checks passed, continue the filter chain
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        LOGGER.info("AuthenticationFilter destroyed");
    }
    
    /**
     * Get the request path without the context path
     * 
     * @param request HTTP request
     * @return The request path
     */
    private String getRequestPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();
        
        if (contextPath.length() > 0) {
            return requestUri.substring(contextPath.length());
        }
        
        return requestUri;
    }
    
    /**
     * Check if the path is public (doesn't require authentication)
     * 
     * @param path The request path
     * @return true if the path is public, false otherwise
     */
    private boolean isPublicPath(String path) {
        // Check if path starts with any of the public paths
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if the user is authorized to access the requested path
     * 
     * @param user The user principal
     * @param path The request path
     * @return true if the user is authorized, false otherwise
     */
    private boolean isAuthorized(UserPrincipal user, String path) {
        if (user == null) {
            return false;
        }
        
        // Check admin paths
        for (String adminPath : ADMIN_PATHS) {
            if (path.startsWith(adminPath) && !user.hasRole("ADMIN")) {
                return false;
            }
        }
        
        // Check manager paths
        for (String managerPath : MANAGER_PATHS) {
            if (path.startsWith(managerPath) && !user.hasAnyRole("ADMIN", "MANAGER")) {
                return false;
            }
        }
        
        // Check for resource-based permissions
        if (path.startsWith("/products") && path.contains("/delete") && !user.hasPermission("product:delete")) {
            return false;
        }
        
        if (path.startsWith("/suppliers") && path.contains("/edit") && !user.hasPermission("supplier:edit")) {
            return false;
        }
        
        if (path.startsWith("/orders") && path.contains("/edit") && !user.hasPermission("order:edit")) {
            return false;
        }
        
        return true;
    }
} 
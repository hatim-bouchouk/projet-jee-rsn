package com.scm.security.filter;

import java.io.IOException;
import java.util.Arrays;
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

import com.scm.security.util.SessionManager;

/**
 * Filter for CSRF protection.
 * Intercepts all state-changing requests (POST, PUT, DELETE) and validates the CSRF token.
 */
@WebFilter(filterName = "CsrfFilter", urlPatterns = {"/*"})
public class CsrfFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(CsrfFilter.class.getName());
    
    // HTTP methods that change state and require CSRF protection
    private static final Set<String> PROTECTED_METHODS = new HashSet<>(
            Arrays.asList("POST", "PUT", "DELETE", "PATCH"));
    
    // Paths exempt from CSRF protection (e.g., API endpoints with their own protection)
    private static final Set<String> CSRF_EXEMPT_PATHS = new HashSet<>(
            Arrays.asList("/api/", "/login", "/logout"));
    
    // Name of the CSRF token field in forms
    private static final String CSRF_TOKEN_FIELD = "_csrf";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("CsrfFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String method = httpRequest.getMethod();
        
        // Only check CSRF for state-changing methods
        if (PROTECTED_METHODS.contains(method)) {
            String requestPath = getRequestPath(httpRequest);
            
            // Skip CSRF check for exempt paths
            if (!isCsrfExemptPath(requestPath)) {
                // Validate CSRF token
                if (!validateCsrfToken(httpRequest)) {
                    LOGGER.log(Level.WARNING, "CSRF token validation failed for path: {0}", requestPath);
                    
                    // Respond with a 403 Forbidden status
                    httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token validation failed");
                    return;
                }
            }
        }
        
        // All checks passed, continue the filter chain
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        LOGGER.info("CsrfFilter destroyed");
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
     * Check if the path is exempt from CSRF protection
     * 
     * @param path The request path
     * @return true if the path is exempt, false otherwise
     */
    private boolean isCsrfExemptPath(String path) {
        // Check if path starts with any of the exempt paths
        for (String exemptPath : CSRF_EXEMPT_PATHS) {
            if (path.startsWith(exemptPath)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Validate the CSRF token in the request
     * 
     * @param request HTTP request
     * @return true if the token is valid, false otherwise
     */
    private boolean validateCsrfToken(HttpServletRequest request) {
        // Get the token from the request
        String requestToken = request.getParameter(CSRF_TOKEN_FIELD);
        
        // If no token in request parameters, check for token in header (for AJAX requests)
        if (requestToken == null || requestToken.isEmpty()) {
            requestToken = request.getHeader("X-CSRF-TOKEN");
        }
        
        // Validate the token against the session token
        return SessionManager.validateCsrfToken(request, requestToken);
    }
} 
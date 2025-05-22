package com.scm.security.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.scm.model.User;
import com.scm.security.model.UserPrincipal;

/**
 * Utility class for managing user sessions.
 */
public class SessionManager {
    
    private static final String USER_PRINCIPAL_KEY = "userPrincipal";
    private static final String CSRF_TOKEN_KEY = "csrfToken";
    private static final int SESSION_MAX_INACTIVE_INTERVAL = 30 * 60; // 30 minutes
    private static final int SESSION_TIMEOUT_MINUTES = 30;
    
    // In-memory store of active sessions for additional security checks
    // In a clustered environment, this would need to be stored in a distributed cache like Redis
    private static final Map<String, UserPrincipal> ACTIVE_SESSIONS = new ConcurrentHashMap<>();
    
    private SessionManager() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Create a new session for the authenticated user
     * 
     * @param request HTTP request
     * @param response HTTP response
     * @param user The authenticated user entity
     * @return The user principal created for the session
     */
    public static UserPrincipal createSession(HttpServletRequest request, HttpServletResponse response, User user) {
        // Invalidate any existing session
        HttpSession existingSession = request.getSession(false);
        if (existingSession != null) {
            ACTIVE_SESSIONS.remove(existingSession.getId());
            existingSession.invalidate();
        }
        
        // Create a new session
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(SESSION_MAX_INACTIVE_INTERVAL);
        
        // Create a new user principal
        UserPrincipal principal = UserPrincipal.from(user);
        principal.setSessionId(session.getId());
        
        // Store in the session
        session.setAttribute(USER_PRINCIPAL_KEY, principal);
        
        // Generate and store CSRF token
        String csrfToken = generateCsrfToken();
        session.setAttribute(CSRF_TOKEN_KEY, csrfToken);
        
        // Store in active sessions map
        ACTIVE_SESSIONS.put(session.getId(), principal);
        
        return principal;
    }
    
    /**
     * Get the user principal from the session
     * 
     * @param request HTTP request
     * @return The user principal, or null if not authenticated
     */
    public static UserPrincipal getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        
        Object principal = session.getAttribute(USER_PRINCIPAL_KEY);
        if (principal instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            
            // Update last activity
            userPrincipal.updateLastActivity();
            
            // Verify session integrity
            if (!ACTIVE_SESSIONS.containsKey(session.getId()) ||
                    !session.getId().equals(userPrincipal.getSessionId())) {
                // Session integrity check failed - potentially hijacked session
                invalidateSession(request);
                return null;
            }
            
            return userPrincipal;
        }
        
        return null;
    }
    
    /**
     * Invalidate the current session
     * 
     * @param request HTTP request
     */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            ACTIVE_SESSIONS.remove(session.getId());
            session.invalidate();
        }
    }
    
    /**
     * Check if the user's session is active and not timed out
     * 
     * @param request HTTP request
     * @return true if the session is active, false otherwise
     */
    public static boolean isSessionActive(HttpServletRequest request) {
        UserPrincipal user = getCurrentUser(request);
        if (user == null) {
            return false;
        }
        
        // Check if session has timed out due to inactivity
        LocalDateTime lastActivity = user.getLastActivity();
        if (lastActivity == null) {
            return false;
        }
        
        long minutesSinceLastActivity = ChronoUnit.MINUTES.between(lastActivity, LocalDateTime.now());
        return minutesSinceLastActivity < SESSION_TIMEOUT_MINUTES;
    }
    
    /**
     * Get the CSRF token from the session
     * 
     * @param request HTTP request
     * @return The CSRF token, or null if not available
     */
    public static String getCsrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        
        Object token = session.getAttribute(CSRF_TOKEN_KEY);
        return token instanceof String ? (String) token : null;
    }
    
    /**
     * Validate the CSRF token from a request against the one stored in the session
     * 
     * @param request HTTP request
     * @param requestToken The token from the request
     * @return true if the tokens match, false otherwise
     */
    public static boolean validateCsrfToken(HttpServletRequest request, String requestToken) {
        String sessionToken = getCsrfToken(request);
        if (sessionToken == null || requestToken == null) {
            return false;
        }
        
        return sessionToken.equals(requestToken);
    }
    
    /**
     * Generate a new CSRF token
     * 
     * @return A new random CSRF token
     */
    private static String generateCsrfToken() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Get the count of active sessions
     * 
     * @return The number of active sessions
     */
    public static int getActiveSessionCount() {
        return ACTIVE_SESSIONS.size();
    }
} 
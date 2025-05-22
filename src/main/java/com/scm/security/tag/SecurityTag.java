package com.scm.security.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.scm.security.model.UserPrincipal;
import com.scm.security.util.SessionManager;

/**
 * Custom JSP tag for role-based and permission-based security checks.
 * This allows conditional rendering in JSP views based on user roles and permissions.
 */
public class SecurityTag extends TagSupport {
    
    private static final long serialVersionUID = 1L;
    
    private String hasRole;
    private String hasAnyRole;
    private String hasPermission;
    
    @Override
    public int doStartTag() throws JspException {
        boolean allowed = evaluateAccess();
        
        // SKIP_BODY if not allowed, EVAL_BODY_INCLUDE if allowed
        return allowed ? EVAL_BODY_INCLUDE : SKIP_BODY;
    }
    
    /**
     * Evaluate if the current user is allowed access based on roles or permissions
     * 
     * @return true if access is allowed, false otherwise
     */
    private boolean evaluateAccess() {
        // Get current user
        UserPrincipal currentUser = SessionManager.getCurrentUser(
                (javax.servlet.http.HttpServletRequest) pageContext.getRequest());
        
        if (currentUser == null) {
            return false; // No authenticated user
        }
        
        // Check by single role
        if (hasRole != null && !hasRole.isEmpty()) {
            return currentUser.hasRole(hasRole);
        }
        
        // Check by any of multiple roles
        if (hasAnyRole != null && !hasAnyRole.isEmpty()) {
            String[] roles = hasAnyRole.split(",");
            for (String role : roles) {
                if (currentUser.hasRole(role.trim())) {
                    return true;
                }
            }
            return false;
        }
        
        // Check by permission
        if (hasPermission != null && !hasPermission.isEmpty()) {
            return currentUser.hasPermission(hasPermission);
        }
        
        // No criteria specified
        return false;
    }
    
    // Getters and setters
    
    public String getHasRole() {
        return hasRole;
    }
    
    public void setHasRole(String hasRole) {
        this.hasRole = hasRole;
    }
    
    public String getHasAnyRole() {
        return hasAnyRole;
    }
    
    public void setHasAnyRole(String hasAnyRole) {
        this.hasAnyRole = hasAnyRole;
    }
    
    public String getHasPermission() {
        return hasPermission;
    }
    
    public void setHasPermission(String hasPermission) {
        this.hasPermission = hasPermission;
    }
} 
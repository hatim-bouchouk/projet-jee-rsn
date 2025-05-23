package com.scm.security.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.scm.model.User;
import com.scm.model.User.Role;

/**
 * Represents the authenticated user principal stored in the session.
 * Contains user information and role-based permissions.
 */
public class UserPrincipal implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private String username;
    private String email;
    private String fullName;
    private Set<String> roles;
    private Set<String> permissions;
    private LocalDateTime lastActivity;
    private String sessionId;
    
    /**
     * Create a UserPrincipal from a User entity
     * 
     * @param user The user entity
     * @return A new UserPrincipal
     */
    public static UserPrincipal from(User user) {
        UserPrincipal principal = new UserPrincipal();
        principal.id = user.getId();
        principal.username = user.getUsername();
        principal.email = user.getEmail();
        // If User doesn't have a fullName method, construct from firstName and lastName if available
        // or use username as fallback
        principal.fullName = user.getUsername(); // Fallback to username
        principal.roles = new HashSet<>();
        principal.roles.add(user.getRole().name());
        principal.permissions = calculatePermissions(user);
        principal.lastActivity = LocalDateTime.now();
        
        return principal;
    }
    
    /**
     * Calculate permissions based on user's role
     * 
     * @param user The user entity
     * @return Set of permission strings
     */
    private static Set<String> calculatePermissions(User user) {
        Set<String> permissions = new HashSet<>();
        
        // Basic permissions for all users
        permissions.add("profile:view");
        permissions.add("profile:edit");
        
        // Role-specific permissions
        if (user.getRole() == Role.admin) {
            // Admin permissions
            permissions.add("user:view");
            permissions.add("user:create");
            permissions.add("user:edit");
            permissions.add("user:delete");
            permissions.add("product:view");
            permissions.add("product:create");
            permissions.add("product:edit");
            permissions.add("product:delete");
            permissions.add("supplier:view");
            permissions.add("supplier:create");
            permissions.add("supplier:edit");
            permissions.add("supplier:delete");
            permissions.add("order:view");
            permissions.add("order:create");
            permissions.add("order:edit");
            permissions.add("order:delete");
            permissions.add("stock:view");
            permissions.add("stock:create");
            permissions.add("stock:edit");
            permissions.add("dashboard:view");
            permissions.add("report:view");
            permissions.add("report:export");
            permissions.add("settings:view");
            permissions.add("settings:edit");
        } else if (user.getRole() == Role.manager) {
            // Manager permissions
            permissions.add("product:view");
            permissions.add("product:create");
            permissions.add("product:edit");
            permissions.add("supplier:view");
            permissions.add("supplier:create");
            permissions.add("supplier:edit");
            permissions.add("order:view");
            permissions.add("order:create");
            permissions.add("order:edit");
            permissions.add("stock:view");
            permissions.add("stock:create");
            permissions.add("stock:edit");
            permissions.add("dashboard:view");
            permissions.add("report:view");
            permissions.add("report:export");
        } else { // Default user permissions
            // Basic user permissions
            permissions.add("product:view");
            permissions.add("order:view");
            permissions.add("order:create");
        }
        
        return permissions;
    }
    
    /**
     * Check if the user has a specific role
     * 
     * @param role The role to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        return roles.contains(role);
    }
    
    /**
     * Check if the user has a specific permission
     * 
     * @param permission The permission to check
     * @return true if the user has the permission, false otherwise
     */
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
    
    /**
     * Check if the user has any of the given roles
     * 
     * @param roleList The roles to check
     * @return true if the user has any of the roles, false otherwise
     */
    public boolean hasAnyRole(String... roleList) {
        for (String role : roleList) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Update the last activity timestamp
     */
    public void updateLastActivity() {
        this.lastActivity = LocalDateTime.now();
    }

    // Getters and setters
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<String> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public void setRoles(Set<String> roles) {
        this.roles = new HashSet<>(roles);
    }

    public Set<String> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = new HashSet<>(permissions);
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
} 
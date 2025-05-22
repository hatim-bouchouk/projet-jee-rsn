package com.scm.dao;

import java.util.List;
import java.util.Optional;

import com.scm.model.User;
import com.scm.model.User.Role;

/**
 * DAO interface for User entity with custom query methods
 */
public interface UserDao extends GenericDao<User, Integer> {
    
    /**
     * Find a user by username
     * 
     * @param username Username to search for
     * @return Optional containing user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find a user by email
     * 
     * @param email Email to search for
     * @return Optional containing user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find all users with a specific role
     * 
     * @param role Role to filter by
     * @return List of users with the specified role
     */
    List<User> findByRole(Role role);
    
    /**
     * Check if a username already exists
     * 
     * @param username Username to check
     * @return true if username exists, false otherwise
     */
    boolean usernameExists(String username);
    
    /**
     * Check if an email already exists
     * 
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    boolean emailExists(String email);
} 
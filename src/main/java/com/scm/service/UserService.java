package com.scm.service;

import java.util.List;
import java.util.Optional;

import com.scm.model.User;
import com.scm.model.User.Role;
import com.scm.service.exception.AuthenticationException;
import com.scm.service.exception.ServiceException;
import com.scm.service.exception.ValidationException;

/**
 * Service interface for User management and authentication.
 */
public interface UserService {
    
    /**
     * Authenticate a user with username and password
     * 
     * @param username Username
     * @param password Password (plain text)
     * @return Authenticated user
     * @throws AuthenticationException if authentication fails
     * @throws ServiceException if a system error occurs
     */
    User authenticate(String username, String password) throws AuthenticationException, ServiceException;
    
    /**
     * Create a new user
     * 
     * @param user User to create
     * @return Created user with generated ID
     * @throws ValidationException if user data is invalid
     * @throws ServiceException if a system error occurs
     */
    User createUser(User user) throws ValidationException, ServiceException;
    
    /**
     * Update an existing user
     * 
     * @param user User to update
     * @return Updated user
     * @throws ValidationException if user data is invalid
     * @throws ServiceException if a system error occurs
     */
    User updateUser(User user) throws ValidationException, ServiceException;
    
    /**
     * Change user's password
     * 
     * @param userId User ID
     * @param currentPassword Current password
     * @param newPassword New password
     * @return true if password was changed, false otherwise
     * @throws AuthenticationException if current password is incorrect
     * @throws ValidationException if new password is invalid
     * @throws ServiceException if a system error occurs
     */
    boolean changePassword(Integer userId, String currentPassword, String newPassword) 
            throws AuthenticationException, ValidationException, ServiceException;
    
    /**
     * Delete a user
     * 
     * @param userId User ID
     * @return true if user was deleted, false if user was not found
     * @throws ServiceException if a system error occurs
     */
    boolean deleteUser(Integer userId) throws ServiceException;
    
    /**
     * Find a user by ID
     * 
     * @param userId User ID
     * @return Optional containing user if found, empty otherwise
     * @throws ServiceException if a system error occurs
     */
    Optional<User> findById(Integer userId) throws ServiceException;
    
    /**
     * Find a user by username
     * 
     * @param username Username
     * @return Optional containing user if found, empty otherwise
     * @throws ServiceException if a system error occurs
     */
    Optional<User> findByUsername(String username) throws ServiceException;
    
    /**
     * Find all users
     * 
     * @return List of all users
     * @throws ServiceException if a system error occurs
     */
    List<User> findAllUsers() throws ServiceException;
    
    /**
     * Find users by role
     * 
     * @param role Role
     * @return List of users with the specified role
     * @throws ServiceException if a system error occurs
     */
    List<User> findUsersByRole(Role role) throws ServiceException;
} 
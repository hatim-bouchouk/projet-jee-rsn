package com.scm.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.scm.dao.UserDao;
import com.scm.model.User;
import com.scm.model.User.Role;
import com.scm.service.UserService;
import com.scm.service.exception.AuthenticationException;
import com.scm.service.exception.ServiceException;
import com.scm.service.exception.ValidationException;
import com.scm.service.util.PasswordUtils;

/**
 * Implementation of UserService using EJB stateless session bean.
 */
@Stateless
public class UserServiceImpl implements UserService {
    
    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class.getName());
    
    @Inject
    private UserDao userDao;
    
    @Override
    @Transactional
    public User authenticate(String username, String password) throws AuthenticationException {
        try {
            Optional<User> userOpt = userDao.findByUsername(username);
            
            if (!userOpt.isPresent()) {
                throw new AuthenticationException("Invalid username or password");
            }
            
            User user = userOpt.get();
            if (!PasswordUtils.verifyPassword(password, user.getPassword())) {
                throw new AuthenticationException("Invalid username or password");
            }
            
            return user;
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error authenticating user", e);
            throw new AuthenticationException("Authentication failed due to system error: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public User createUser(User user) throws ValidationException, ServiceException {
        try {
            // Validate user data
            validateUser(user, true);
            
            // Hash the password
            user.setPassword(PasswordUtils.hashPassword(user.getPassword()));
            
            // Set creation timestamp
            user.setCreatedAt(LocalDateTime.now());
            
            // Save the user
            return userDao.save(user);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating user", e);
            throw new ServiceException("Failed to create user", e);
        }
    }
    
    @Override
    @Transactional
    public User updateUser(User user) throws ValidationException, ServiceException {
        try {
            // Check if user exists
            Optional<User> existingUserOpt = userDao.findById(user.getId());
            if (!existingUserOpt.isPresent()) {
                throw new ServiceException("User not found with ID: " + user.getId());
            }
            
            User existingUser = existingUserOpt.get();
            
            // Validate user data
            validateUserForUpdate(user, existingUser);
            
            // Preserve the password
            user.setPassword(existingUser.getPassword());
            
            // Preserve creation timestamp
            user.setCreatedAt(existingUser.getCreatedAt());
            
            // Update the user
            return userDao.update(user);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating user", e);
            throw new ServiceException("Failed to update user", e);
        }
    }
    
    @Override
    @Transactional
    public boolean changePassword(Integer userId, String currentPassword, String newPassword) 
            throws AuthenticationException, ValidationException, ServiceException {
        try {
            // Validate inputs
            if (userId == null) {
                throw new ValidationException("User ID cannot be null");
            }
            
            if (currentPassword == null || currentPassword.isEmpty()) {
                throw new ValidationException("Current password cannot be empty");
            }
            
            if (newPassword == null || newPassword.isEmpty()) {
                throw new ValidationException("New password cannot be empty");
            }
            
            // Check if password meets security requirements
            if (!PasswordUtils.isValidPassword(newPassword)) {
                throw new ValidationException("Password must be at least 8 characters long and contain at least one letter and one number");
            }
            
            // Find the user
            Optional<User> userOpt = userDao.findById(userId);
            if (!userOpt.isPresent()) {
                throw new ServiceException("User not found with ID: " + userId);
            }
            
            User user = userOpt.get();
            
            // Verify current password
            if (!PasswordUtils.verifyPassword(currentPassword, user.getPassword())) {
                throw new AuthenticationException("Current password is incorrect");
            }
            
            // Update password
            user.setPassword(PasswordUtils.hashPassword(newPassword));
            userDao.update(user);
            
            return true;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error changing password", e);
            throw new ServiceException("Failed to change password", e);
        }
    }
    
    @Override
    @Transactional
    public boolean deleteUser(Integer userId) throws ServiceException {
        try {
            if (userId == null) {
                throw new ServiceException("User ID cannot be null");
            }
            
            return userDao.deleteById(userId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting user", e);
            throw new ServiceException("Failed to delete user", e);
        }
    }
    
    @Override
    public Optional<User> findById(Integer userId) throws ServiceException {
        try {
            if (userId == null) {
                throw new ServiceException("User ID cannot be null");
            }
            
            return userDao.findById(userId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding user by ID", e);
            throw new ServiceException("Failed to find user by ID", e);
        }
    }
    
    @Override
    public Optional<User> findByUsername(String username) throws ServiceException {
        try {
            if (username == null || username.isEmpty()) {
                throw new ServiceException("Username cannot be empty");
            }
            
            return userDao.findByUsername(username);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding user by username", e);
            throw new ServiceException("Failed to find user by username", e);
        }
    }
    
    @Override
    public List<User> findAllUsers() throws ServiceException {
        try {
            return userDao.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding all users", e);
            throw new ServiceException("Failed to find all users", e);
        }
    }
    
    @Override
    public List<User> findUsersByRole(Role role) throws ServiceException {
        try {
            if (role == null) {
                throw new ServiceException("Role cannot be null");
            }
            
            return userDao.findByRole(role);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding users by role", e);
            throw new ServiceException("Failed to find users by role", e);
        }
    }
    
    /**
     * Validate user data for creation or update
     */
    private void validateUser(User user, boolean isNewUser) throws ValidationException {
        Map<String, String> errors = new HashMap<>();
        
        if (user == null) {
            throw new ValidationException("User cannot be null");
        }
        
        if (isNewUser && (user.getPassword() == null || user.getPassword().isEmpty())) {
            errors.put("password", "Password cannot be empty");
        }
        
        if (isNewUser && user.getPassword() != null && !PasswordUtils.isValidPassword(user.getPassword())) {
            errors.put("password", "Password must be at least 8 characters long and contain at least one letter and one number");
        }
        
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            errors.put("username", "Username cannot be empty");
        } else if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
            errors.put("username", "Username must be between 3 and 50 characters");
        } else if (isNewUser && userDao.usernameExists(user.getUsername())) {
            errors.put("username", "Username already exists");
        }
        
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            errors.put("email", "Email cannot be empty");
        } else if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.put("email", "Email is not valid");
        } else if (isNewUser && userDao.emailExists(user.getEmail())) {
            errors.put("email", "Email already exists");
        }
        
        if (user.getRole() == null) {
            errors.put("role", "Role cannot be null");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("User validation failed", errors);
        }
    }
    
    /**
     * Validate user data for update
     */
    private void validateUserForUpdate(User user, User existingUser) throws ValidationException {
        Map<String, String> errors = new HashMap<>();
        
        if (user == null) {
            throw new ValidationException("User cannot be null");
        }
        
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            errors.put("username", "Username cannot be empty");
        } else if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
            errors.put("username", "Username must be between 3 and 50 characters");
        } else if (!user.getUsername().equals(existingUser.getUsername()) && userDao.usernameExists(user.getUsername())) {
            errors.put("username", "Username already exists");
        }
        
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            errors.put("email", "Email cannot be empty");
        } else if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.put("email", "Email is not valid");
        } else if (!user.getEmail().equals(existingUser.getEmail()) && userDao.emailExists(user.getEmail())) {
            errors.put("email", "Email already exists");
        }
        
        if (user.getRole() == null) {
            errors.put("role", "Role cannot be null");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("User validation failed", errors);
        }
    }
} 
package com.scm.service.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing and verification.
 * In a production environment, a more robust library like BCrypt should be used.
 */
public class PasswordUtils {
    
    private static final int SALT_LENGTH = 16;
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String DELIMITER = ":";
    
    private PasswordUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Hash a password with a random salt.
     * 
     * @param password Password to hash
     * @return Hashed password with salt in format: salt:hash
     */
    public static String hashPassword(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            String hash = generateHash(password, salt);
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            
            return saltBase64 + DELIMITER + hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Verify a password against a stored hash.
     * 
     * @param password Password to verify
     * @param storedHash Stored hash in format: salt:hash
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(DELIMITER);
            if (parts.length != 2) {
                return false;
            }
            
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            String computedHash = generateHash(password, salt);
            
            return computedHash.equals(parts[1]);
        } catch (Exception e) {
            return false;
        }
    }
    
    private static String generateHash(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        digest.reset();
        digest.update(salt);
        byte[] hash = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
    
    /**
     * Validates a password meets security requirements.
     * 
     * @param password Password to validate
     * @return true if password is valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        // Minimum 8 characters with at least one letter and one number
        return password != null && password.length() >= 8 
                && password.matches(".*[A-Za-z].*") && password.matches(".*\\d.*");
    }
} 
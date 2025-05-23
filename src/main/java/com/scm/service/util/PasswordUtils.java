package com.scm.service.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class for password hashing and verification.
 * Uses BCrypt for secure password hashing.
 */
public class PasswordUtils {
    
    private static final int SALT_LENGTH = 16;
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String DELIMITER = ":";
    private static final int BCRYPT_STRENGTH = 10;
    private static final BCryptPasswordEncoder BCRYPT_ENCODER = new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    
    private PasswordUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Hash a password with a random salt using BCrypt.
     * 
     * @param password Password to hash
     * @return BCrypt hashed password
     */
    public static String hashPassword(String password) {
        return BCRYPT_ENCODER.encode(password);
    }
    
    /**
     * Verify a password against a stored BCrypt hash.
     * 
     * @param password Password to verify
     * @param storedHash Stored BCrypt hash
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (isBCryptHash(storedHash)) {
            return BCRYPT_ENCODER.matches(password, storedHash);
        } else {
            // Legacy verification for non-BCrypt hashes
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
    
    /**
     * Generate a BCrypt password hash for a given password.
     * This is a utility method for generating BCrypt hashes for testing.
     * 
     * @param password The password to hash
     * @return BCrypt hashed password
     */
    public static String generateBCryptHash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_STRENGTH));
    }
    
    /**
     * Check if the given hash is in BCrypt format.
     * 
     * @param hash The hash to check
     * @return true if the hash is in BCrypt format, false otherwise
     */
    public static boolean isBCryptHash(String hash) {
        return hash != null && hash.startsWith("$2a$");
    }
    
    /**
     * Utility method to generate BCrypt hashes for sample data.
     * This can be used to generate hashes for the init_data.sql file.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        String[] passwords = {"admin123", "manager123", "client123"};
        
        for (String password : passwords) {
            String bcryptHash = generateBCryptHash(password);
            System.out.println(password + " = " + bcryptHash);
        }
    }
} 
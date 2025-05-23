package com.scm.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Part;

/**
 * Utility class for handling file uploads.
 */
public class FileUploadUtil {
    
    private static final Logger LOGGER = Logger.getLogger(FileUploadUtil.class.getName());
    private static final String UPLOAD_DIR;
    private static final long MAX_FILE_SIZE;
    private static final Set<String> ALLOWED_CONTENT_TYPES;
    
    static {
        // Initialize from configuration
        UPLOAD_DIR = AppConfig.getProperty("file.upload.dir", System.getProperty("java.io.tmpdir") + "/scm/uploads");
        MAX_FILE_SIZE = AppConfig.getLongProperty("file.upload.max-size", 10 * 1024 * 1024); // Default 10MB
        
        // Parse allowed content types
        String allowedTypes = AppConfig.getProperty("file.upload.allowed-types", "image/jpeg,image/png,application/pdf");
        ALLOWED_CONTENT_TYPES = new HashSet<>(Arrays.asList(allowedTypes.split(",")));
        
        // Create upload directory if it doesn't exist
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                LOGGER.log(Level.INFO, "Created upload directory: {0}", UPLOAD_DIR);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create upload directory", e);
        }
    }
    
    // Private constructor to prevent instantiation
    private FileUploadUtil() {
    }
    
    /**
     * Save an uploaded file from a Part to the configured upload directory.
     * 
     * @param filePart the file part from the multipart request
     * @param subdirectory optional subdirectory within the upload directory
     * @return the saved file path
     * @throws IOException if the file cannot be saved
     * @throws IllegalArgumentException if the file is invalid
     */
    public static String saveFile(Part filePart, String subdirectory) throws IOException, IllegalArgumentException {
        validateFile(filePart);
        
        String fileName = getUniqueFileName(getSubmittedFileName(filePart));
        String uploadPath = UPLOAD_DIR;
        
        // If subdirectory is specified, append it to the upload path
        if (subdirectory != null && !subdirectory.isEmpty()) {
            uploadPath = uploadPath + File.separator + subdirectory;
            // Create subdirectory if it doesn't exist
            Files.createDirectories(Paths.get(uploadPath));
        }
        
        String filePath = uploadPath + File.separator + fileName;
        
        // Save the file
        try (InputStream input = filePart.getInputStream();
             FileOutputStream output = new FileOutputStream(filePath)) {
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            
            LOGGER.log(Level.INFO, "File saved successfully: {0}", filePath);
            return filePath;
        }
    }
    
    /**
     * Delete a file from the upload directory.
     * 
     * @param filePath the path of the file to delete
     * @return true if the file was deleted successfully
     */
    public static boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
            LOGGER.log(Level.INFO, "File deleted successfully: {0}", filePath);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to delete file: " + filePath, e);
            return false;
        }
    }
    
    /**
     * Validate that a file meets the size and type requirements.
     * 
     * @param filePart the file part to validate
     * @throws IllegalArgumentException if the file is invalid
     */
    private static void validateFile(Part filePart) throws IllegalArgumentException {
        // Check file size
        if (filePart.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum allowed size of " + 
                    (MAX_FILE_SIZE / (1024 * 1024)) + "MB");
        }
        
        // Check content type
        String contentType = filePart.getContentType();
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: " + 
                    String.join(", ", ALLOWED_CONTENT_TYPES));
        }
    }
    
    /**
     * Generate a unique file name to prevent overwriting existing files.
     * 
     * @param originalFileName the original file name
     * @return a unique file name
     */
    private static String getUniqueFileName(String originalFileName) {
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFileName.substring(dotIndex);
        }
        
        return UUID.randomUUID().toString() + extension;
    }
    
    /**
     * Extract the submitted file name from a Part.
     * 
     * @param part the file part
     * @return the submitted file name
     */
    private static String getSubmittedFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] elements = contentDisposition.split(";");
        
        for (String element : elements) {
            if (element.trim().startsWith("filename")) {
                return element.substring(element.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        
        return "unknown";
    }
} 
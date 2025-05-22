package com.scm.dao;

import java.util.List;
import java.util.Optional;

import com.scm.model.Supplier;

/**
 * DAO interface for Supplier entity with custom query methods
 */
public interface SupplierDao extends GenericDao<Supplier, Integer> {
    
    /**
     * Find a supplier by email
     * 
     * @param email Email to search for
     * @return Optional containing supplier if found, empty otherwise
     */
    Optional<Supplier> findByEmail(String email);
    
    /**
     * Find suppliers by name (partial match)
     * 
     * @param name Name to search for
     * @return List of suppliers with matching name
     */
    List<Supplier> findByNameContaining(String name);
    
    /**
     * Find suppliers that supply a specific product
     * 
     * @param productId Product ID
     * @return List of suppliers that supply the product
     */
    List<Supplier> findByProduct(Integer productId);
    
    /**
     * Find suppliers that supply a specific product by SKU
     * 
     * @param sku Product SKU
     * @return List of suppliers that supply the product
     */
    List<Supplier> findByProductSku(String sku);
    
    /**
     * Check if an email already exists
     * 
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    boolean emailExists(String email);
} 
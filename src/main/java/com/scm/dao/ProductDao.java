package com.scm.dao;

import java.util.List;
import java.util.Optional;

import com.scm.model.Product;

/**
 * DAO interface for Product entity with custom query methods
 */
public interface ProductDao extends GenericDao<Product, Integer> {
    
    /**
     * Find a product by SKU
     * 
     * @param sku SKU to search for
     * @return Optional containing product if found, empty otherwise
     */
    Optional<Product> findBySku(String sku);
    
    /**
     * Find products by name (partial match)
     * 
     * @param name Name to search for
     * @return List of products with matching name
     */
    List<Product> findByNameContaining(String name);
    
    /**
     * Find products below their reorder level (low stock)
     * 
     * @return List of products with stock below reorder level
     */
    List<Product> findLowStockProducts();
    
    /**
     * Find products with zero stock
     * 
     * @return List of products with zero stock
     */
    List<Product> findOutOfStockProducts();
    
    /**
     * Find products by supplier ID
     * 
     * @param supplierId Supplier ID
     * @return List of products provided by the supplier
     */
    List<Product> findBySupplier(Integer supplierId);
    
    /**
     * Check if a SKU already exists
     * 
     * @param sku SKU to check
     * @return true if SKU exists, false otherwise
     */
    boolean skuExists(String sku);
}
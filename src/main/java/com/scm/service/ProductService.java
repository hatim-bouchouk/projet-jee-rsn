package com.scm.service;

import java.util.List;
import java.util.Optional;

import com.scm.model.Product;
import com.scm.service.exception.ServiceException;
import com.scm.service.exception.ValidationException;

/**
 * Service interface for Product catalog and inventory management.
 */
public interface ProductService {
    
    /**
     * Create a new product
     * 
     * @param product Product to create
     * @return Created product with generated ID
     * @throws ValidationException if product data is invalid
     * @throws ServiceException if a system error occurs
     */
    Product createProduct(Product product) throws ValidationException, ServiceException;
    
    /**
     * Update an existing product
     * 
     * @param product Product to update
     * @return Updated product
     * @throws ValidationException if product data is invalid
     * @throws ServiceException if a system error occurs
     */
    Product updateProduct(Product product) throws ValidationException, ServiceException;
    
    /**
     * Delete a product
     * 
     * @param productId Product ID
     * @return true if product was deleted, false if product was not found
     * @throws ServiceException if a system error occurs
     */
    boolean deleteProduct(Integer productId) throws ServiceException;
    
    /**
     * Find a product by ID
     * 
     * @param productId Product ID
     * @return Optional containing product if found, empty otherwise
     * @throws ServiceException if a system error occurs
     */
    Optional<Product> findById(Integer productId) throws ServiceException;
    
    /**
     * Find a product by SKU
     * 
     * @param sku Product SKU
     * @return Optional containing product if found, empty otherwise
     * @throws ServiceException if a system error occurs
     */
    Optional<Product> findBySku(String sku) throws ServiceException;
    
    /**
     * Find all products
     * 
     * @return List of all products
     * @throws ServiceException if a system error occurs
     */
    List<Product> findAllProducts() throws ServiceException;
    
    /**
     * Find products by name (partial match)
     * 
     * @param name Name to search for
     * @return List of products with matching name
     * @throws ServiceException if a system error occurs
     */
    List<Product> findByNameContaining(String name) throws ServiceException;
    
    /**
     * Find products with low stock (below reorder level)
     * 
     * @return List of products with stock below reorder level
     * @throws ServiceException if a system error occurs
     */
    List<Product> findLowStockProducts() throws ServiceException;
    
    /**
     * Find products with zero stock
     * 
     * @return List of products with zero stock
     * @throws ServiceException if a system error occurs
     */
    List<Product> findOutOfStockProducts() throws ServiceException;
    
    /**
     * Find products by supplier ID
     * 
     * @param supplierId Supplier ID
     * @return List of products provided by the supplier
     * @throws ServiceException if a system error occurs
     */
    List<Product> findBySupplier(Integer supplierId) throws ServiceException;
    
    /**
     * Update product price
     * 
     * @param productId Product ID
     * @param newPrice New price
     * @return Updated product if found, empty otherwise
     * @throws ValidationException if price is invalid
     * @throws ServiceException if a system error occurs
     */
    Optional<Product> updatePrice(Integer productId, java.math.BigDecimal newPrice) 
            throws ValidationException, ServiceException;
    
    /**
     * Update product reorder level
     * 
     * @param productId Product ID
     * @param reorderLevel New reorder level
     * @return Updated product if found, empty otherwise
     * @throws ValidationException if reorder level is invalid
     * @throws ServiceException if a system error occurs
     */
    Optional<Product> updateReorderLevel(Integer productId, Integer reorderLevel) 
            throws ValidationException, ServiceException;
} 
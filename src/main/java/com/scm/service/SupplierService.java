package com.scm.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.scm.model.Supplier;
import com.scm.model.SupplierProduct;
import com.scm.service.exception.ServiceException;
import com.scm.service.exception.ValidationException;

/**
 * Service interface for Supplier management and supplier-product associations.
 */
public interface SupplierService {
    
    /**
     * Create a new supplier
     * 
     * @param supplier Supplier to create
     * @return Created supplier with generated ID
     * @throws ValidationException if supplier data is invalid
     * @throws ServiceException if a system error occurs
     */
    Supplier createSupplier(Supplier supplier) throws ValidationException, ServiceException;
    
    /**
     * Update an existing supplier
     * 
     * @param supplier Supplier to update
     * @return Updated supplier
     * @throws ValidationException if supplier data is invalid
     * @throws ServiceException if a system error occurs
     */
    Supplier updateSupplier(Supplier supplier) throws ValidationException, ServiceException;
    
    /**
     * Delete a supplier
     * 
     * @param supplierId Supplier ID
     * @return true if supplier was deleted, false if supplier was not found
     * @throws ServiceException if a system error occurs
     */
    boolean deleteSupplier(Integer supplierId) throws ServiceException;
    
    /**
     * Find a supplier by ID
     * 
     * @param supplierId Supplier ID
     * @return Optional containing supplier if found, empty otherwise
     * @throws ServiceException if a system error occurs
     */
    Optional<Supplier> findById(Integer supplierId) throws ServiceException;
    
    /**
     * Find a supplier by email
     * 
     * @param email Email
     * @return Optional containing supplier if found, empty otherwise
     * @throws ServiceException if a system error occurs
     */
    Optional<Supplier> findByEmail(String email) throws ServiceException;
    
    /**
     * Find all suppliers
     * 
     * @return List of all suppliers
     * @throws ServiceException if a system error occurs
     */
    List<Supplier> findAllSuppliers() throws ServiceException;
    
    /**
     * Find suppliers by name (partial match)
     * 
     * @param name Name to search for
     * @return List of suppliers with matching name
     * @throws ServiceException if a system error occurs
     */
    List<Supplier> findByNameContaining(String name) throws ServiceException;
    
    /**
     * Find suppliers that supply a specific product
     * 
     * @param productId Product ID
     * @return List of suppliers that supply the product
     * @throws ServiceException if a system error occurs
     */
    List<Supplier> findByProduct(Integer productId) throws ServiceException;
    
    /**
     * Add a product to a supplier's catalog
     * 
     * @param supplierId Supplier ID
     * @param productId Product ID
     * @param unitCost Unit cost from this supplier
     * @param leadTimeDays Lead time in days
     * @return Created supplier-product association
     * @throws ValidationException if data is invalid
     * @throws ServiceException if a system error occurs
     */
    SupplierProduct addProductToSupplier(Integer supplierId, Integer productId, 
            BigDecimal unitCost, Integer leadTimeDays) 
            throws ValidationException, ServiceException;
    
    /**
     * Update a supplier-product association
     * 
     * @param supplierProduct Supplier-product association to update
     * @return Updated supplier-product association
     * @throws ValidationException if data is invalid
     * @throws ServiceException if a system error occurs
     */
    SupplierProduct updateSupplierProduct(SupplierProduct supplierProduct) 
            throws ValidationException, ServiceException;
    
    /**
     * Remove a product from a supplier's catalog
     * 
     * @param supplierId Supplier ID
     * @param productId Product ID
     * @return true if association was removed, false if association was not found
     * @throws ServiceException if a system error occurs
     */
    boolean removeProductFromSupplier(Integer supplierId, Integer productId) 
            throws ServiceException;
    
    /**
     * Find the lowest-cost supplier for a product
     * 
     * @param productId Product ID
     * @return Optional containing lowest-cost supplier product association if found, empty otherwise
     * @throws ServiceException if a system error occurs
     */
    Optional<SupplierProduct> findLowestCostSupplierForProduct(Integer productId) 
            throws ServiceException;
    
    /**
     * Find the fastest-delivery supplier for a product
     * 
     * @param productId Product ID
     * @return Optional containing fastest-delivery supplier product association if found, empty otherwise
     * @throws ServiceException if a system error occurs
     */
    Optional<SupplierProduct> findFastestDeliverySupplierForProduct(Integer productId) 
            throws ServiceException;
} 
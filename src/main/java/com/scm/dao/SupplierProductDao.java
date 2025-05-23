package com.scm.dao;

import java.util.List;
import java.util.Optional;

import com.scm.model.SupplierProduct;
import com.scm.model.SupplierProductId;

/**
 * DAO interface for SupplierProduct entity with custom query methods
 */
public interface SupplierProductDao extends GenericDao<SupplierProduct, SupplierProductId> {
    
    /**
     * Find supplier products by supplier ID
     * 
     * @param supplierId Supplier ID
     * @return List of supplier products for the specified supplier
     */
    List<SupplierProduct> findBySupplier(Integer supplierId);
    
    /**
     * Find supplier products by product ID
     * 
     * @param productId Product ID
     * @return List of supplier products for the specified product
     */
    List<SupplierProduct> findByProduct(Integer productId);
    
    /**
     * Find a specific supplier product by supplier ID and product ID
     * 
     * @param supplierId Supplier ID
     * @param productId Product ID
     * @return Optional containing supplier product if found, empty otherwise
     */
    Optional<SupplierProduct> findBySupplierAndProduct(Integer supplierId, Integer productId);
    
    /**
     * Delete all supplier products for a specific supplier
     * 
     * @param supplierId Supplier ID
     */
    void deleteBySupplier(Integer supplierId);
    
    /**
     * Delete all supplier products for a specific product
     * 
     * @param productId Product ID
     */
    void deleteByProduct(Integer productId);
    
    /**
     * Find the supplier with the lowest cost for a specific product
     * 
     * @param productId Product ID
     * @return Optional containing supplier product with lowest cost if found, empty otherwise
     */
    Optional<SupplierProduct> findLowestCostSupplierForProduct(Integer productId);
    
    /**
     * Find the supplier with the fastest delivery time for a specific product
     * 
     * @param productId Product ID
     * @return Optional containing supplier product with fastest delivery if found, empty otherwise
     */
    Optional<SupplierProduct> findFastestDeliverySupplierForProduct(Integer productId);
} 
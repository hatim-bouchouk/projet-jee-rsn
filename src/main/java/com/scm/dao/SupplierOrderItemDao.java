package com.scm.dao;

import java.util.List;

import com.scm.model.SupplierOrderItem;

/**
 * DAO interface for SupplierOrderItem entity with custom query methods
 */
public interface SupplierOrderItemDao extends GenericDao<SupplierOrderItem, Integer> {
    
    /**
     * Find supplier order items by supplier order ID
     * 
     * @param supplierOrderId Supplier order ID
     * @return List of supplier order items for the specified order
     */
    List<SupplierOrderItem> findBySupplierOrderId(Integer supplierOrderId);
    
    /**
     * Find supplier order items by product ID
     * 
     * @param productId Product ID
     * @return List of supplier order items for the specified product
     */
    List<SupplierOrderItem> findByProductId(Integer productId);
    
    /**
     * Delete all supplier order items for a specific supplier order
     * 
     * @param supplierOrderId Supplier order ID
     */
    void deleteBySupplierOrderId(Integer supplierOrderId);
} 
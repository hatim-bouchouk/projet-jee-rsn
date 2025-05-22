package com.scm.dao;

import java.util.Optional;

import com.scm.model.Stock;

/**
 * DAO interface for Stock entity with custom query methods
 */
public interface StockDao extends GenericDao<Stock, Integer> {
    
    /**
     * Find stock by product ID
     * 
     * @param productId Product ID
     * @return Optional containing stock if found, empty otherwise
     */
    Optional<Stock> findByProductId(Integer productId);
    
    /**
     * Find stock by product SKU
     * 
     * @param sku Product SKU
     * @return Optional containing stock if found, empty otherwise
     */
    Optional<Stock> findByProductSku(String sku);
    
    /**
     * Update stock quantity
     * 
     * @param productId Product ID
     * @param quantityChange Change in quantity (positive or negative)
     * @return Updated stock entity if found, empty otherwise
     */
    Optional<Stock> updateQuantity(Integer productId, int quantityChange);
} 
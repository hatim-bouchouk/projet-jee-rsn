package com.scm.dao;

import java.util.List;
import java.util.Optional;

import com.scm.model.OrderItem;

/**
 * DAO interface for OrderItem entity with custom query methods
 */
public interface OrderItemDao extends GenericDao<OrderItem, Integer> {
    
    /**
     * Find order items by order ID
     * 
     * @param orderId Order ID
     * @return List of order items for the specified order
     */
    List<OrderItem> findByOrderId(Integer orderId);
    
    /**
     * Find order items by product ID
     * 
     * @param productId Product ID
     * @return List of order items for the specified product
     */
    List<OrderItem> findByProductId(Integer productId);
    
    /**
     * Delete all order items for a specific order
     * 
     * @param orderId Order ID
     */
    void deleteByOrderId(Integer orderId);
    
    /**
     * Find order item by order ID and product ID
     * 
     * @param orderId Order ID
     * @param productId Product ID
     * @return Optional containing order item if found, empty otherwise
     */
    Optional<OrderItem> findByOrderIdAndProductId(Integer orderId, Integer productId);
} 
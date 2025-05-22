package com.scm.dao;

import java.time.LocalDateTime;
import java.util.List;

import com.scm.model.CustomerOrder;
import com.scm.model.CustomerOrder.Status;

/**
 * DAO interface for CustomerOrder entity with custom query methods
 */
public interface CustomerOrderDao extends GenericDao<CustomerOrder, Integer> {
    
    /**
     * Find orders by customer email
     * 
     * @param email Customer email
     * @return List of orders for the customer
     */
    List<CustomerOrder> findByCustomerEmail(String email);
    
    /**
     * Find orders by status
     * 
     * @param status Order status
     * @return List of orders with the specified status
     */
    List<CustomerOrder> findByStatus(Status status);
    
    /**
     * Find orders by date range
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return List of orders in the date range
     */
    List<CustomerOrder> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find recent orders
     * 
     * @param limit Maximum number of orders to return
     * @return List of recent orders
     */
    List<CustomerOrder> findRecentOrders(int limit);
    
    /**
     * Find orders that contain a specific product
     * 
     * @param productId Product ID
     * @return List of orders containing the product
     */
    List<CustomerOrder> findByProduct(Integer productId);
    
    /**
     * Update order status
     * 
     * @param orderId Order ID
     * @param status New status
     * @return true if order was found and updated, false otherwise
     */
    boolean updateStatus(Integer orderId, Status status);
} 
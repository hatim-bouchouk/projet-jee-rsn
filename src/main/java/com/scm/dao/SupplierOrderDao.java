package com.scm.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.scm.model.SupplierOrder;
import com.scm.model.SupplierOrder.Status;

/**
 * DAO interface for SupplierOrder entity with custom query methods
 */
public interface SupplierOrderDao extends GenericDao<SupplierOrder, Integer> {
    
    /**
     * Find orders by supplier ID
     * 
     * @param supplierId Supplier ID
     * @return List of orders for the supplier
     */
    List<SupplierOrder> findBySupplierId(Integer supplierId);
    
    /**
     * Find orders by status
     * 
     * @param status Order status
     * @return List of orders with the specified status
     */
    List<SupplierOrder> findByStatus(Status status);
    
    /**
     * Find orders by date range
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return List of orders in the date range
     */
    List<SupplierOrder> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find orders expected to be delivered in date range
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return List of orders expected to be delivered in the date range
     */
    List<SupplierOrder> findByExpectedDeliveryDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find orders that are overdue (expected delivery date in the past but not delivered)
     * 
     * @return List of overdue orders
     */
    List<SupplierOrder> findOverdueOrders();
    
    /**
     * Find recent orders
     * 
     * @param limit Maximum number of orders to return
     * @return List of recent orders
     */
    List<SupplierOrder> findRecentOrders(int limit);
    
    /**
     * Find orders that contain a specific product
     * 
     * @param productId Product ID
     * @return List of orders containing the product
     */
    List<SupplierOrder> findByProduct(Integer productId);
    
    /**
     * Update order status
     * 
     * @param orderId Order ID
     * @param status New status
     * @return true if order was found and updated, false otherwise
     */
    boolean updateStatus(Integer orderId, Status status);
} 
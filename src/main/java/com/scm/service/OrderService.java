package com.scm.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.scm.model.CustomerOrder;
import com.scm.model.CustomerOrder.Status;
import com.scm.model.OrderItem;
import com.scm.service.exception.ServiceException;
import com.scm.service.exception.ValidationException;

/**
 * Service interface for Customer Order processing and status management.
 */
public interface OrderService {
    
    /**
     * Create a new customer order
     * 
     * @param order Order to create (without ID)
     * @return Created order with generated ID
     * @throws ValidationException if order data is invalid
     * @throws ServiceException if a system error occurs
     */
    CustomerOrder createOrder(CustomerOrder order) throws ValidationException, ServiceException;
    
    /**
     * Update an existing order
     * 
     * @param order Order to update
     * @return Updated order
     * @throws ValidationException if order data is invalid
     * @throws ServiceException if a system error occurs
     */
    CustomerOrder updateOrder(CustomerOrder order) throws ValidationException, ServiceException;
    
    /**
     * Cancel an order
     * 
     * @param orderId Order ID
     * @return true if order was cancelled, false if order was not found or already shipped
     * @throws ServiceException if a system error occurs
     */
    boolean cancelOrder(Integer orderId) throws ServiceException;
    
    /**
     * Update order status
     * 
     * @param orderId Order ID
     * @param status New status
     * @return Updated order if found, empty otherwise
     * @throws ValidationException if status transition is invalid
     * @throws ServiceException if a system error occurs
     */
    Optional<CustomerOrder> updateOrderStatus(Integer orderId, Status status) 
            throws ValidationException, ServiceException;
    
    /**
     * Add an item to an order
     * 
     * @param orderId Order ID
     * @param item Item to add
     * @return Added order item with generated ID
     * @throws ValidationException if item data is invalid or order is already processed
     * @throws ServiceException if a system error occurs
     */
    OrderItem addOrderItem(Integer orderId, OrderItem item) 
            throws ValidationException, ServiceException;
    
    /**
     * Update an order item
     * 
     * @param item Item to update
     * @return Updated order item
     * @throws ValidationException if item data is invalid or order is already processed
     * @throws ServiceException if a system error occurs
     */
    OrderItem updateOrderItem(OrderItem item) throws ValidationException, ServiceException;
    
    /**
     * Remove an item from an order
     * 
     * @param orderItemId Order item ID
     * @return true if item was removed, false if item was not found
     * @throws ValidationException if order is already processed
     * @throws ServiceException if a system error occurs
     */
    boolean removeOrderItem(Integer orderItemId) throws ValidationException, ServiceException;
    
    /**
     * Find an order by ID
     * 
     * @param orderId Order ID
     * @return Optional containing order if found, empty otherwise
     * @throws ServiceException if a system error occurs
     */
    Optional<CustomerOrder> findById(Integer orderId) throws ServiceException;
    
    /**
     * Find orders by customer email
     * 
     * @param email Customer email
     * @return List of orders for the customer
     * @throws ServiceException if a system error occurs
     */
    List<CustomerOrder> findByCustomerEmail(String email) throws ServiceException;
    
    /**
     * Find orders by status
     * 
     * @param status Order status
     * @return List of orders with the specified status
     * @throws ServiceException if a system error occurs
     */
    List<CustomerOrder> findByStatus(Status status) throws ServiceException;
    
    /**
     * Find orders by date range
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return List of orders in the date range
     * @throws ServiceException if a system error occurs
     */
    List<CustomerOrder> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) 
            throws ServiceException;
    
    /**
     * Find recent orders
     * 
     * @param limit Maximum number of orders to return
     * @return List of recent orders
     * @throws ServiceException if a system error occurs
     */
    List<CustomerOrder> findRecentOrders(int limit) throws ServiceException;
    
    /**
     * Process payment for an order
     * 
     * @param orderId Order ID
     * @param paymentDetails Payment details (implementation-specific)
     * @return true if payment was processed, false otherwise
     * @throws ValidationException if payment details are invalid
     * @throws ServiceException if a system error occurs
     */
    boolean processPayment(Integer orderId, Object paymentDetails) 
            throws ValidationException, ServiceException;
    
    /**
     * Process shipment for an order
     * 
     * @param orderId Order ID
     * @param shipmentDetails Shipment details (implementation-specific)
     * @return Tracking number or shipment identifier
     * @throws ValidationException if order is not in a shippable state
     * @throws ServiceException if a system error occurs
     */
    String processShipment(Integer orderId, Object shipmentDetails) 
            throws ValidationException, ServiceException;
} 
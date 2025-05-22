package com.scm.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.scm.dao.CustomerOrderDao;
import com.scm.dao.OrderItemDao;
import com.scm.dao.ProductDao;
import com.scm.model.CustomerOrder;
import com.scm.model.CustomerOrder.Status;
import com.scm.model.OrderItem;
import com.scm.model.Product;
import com.scm.service.OrderService;
import com.scm.service.StockService;
import com.scm.service.exception.ServiceException;
import com.scm.service.exception.ValidationException;

/**
 * Implementation of OrderService using EJB stateless session bean.
 */
@Stateless
public class OrderServiceImpl implements OrderService {
    
    private static final Logger LOGGER = Logger.getLogger(OrderServiceImpl.class.getName());
    
    @Inject
    private CustomerOrderDao customerOrderDao;
    
    @Inject
    private OrderItemDao orderItemDao;
    
    @Inject
    private ProductDao productDao;
    
    @Inject
    private StockService stockService;
    
    @Override
    @Transactional
    public CustomerOrder createOrder(CustomerOrder order) throws ValidationException, ServiceException {
        try {
            // Validate order data
            validateOrder(order, true);
            
            // Set default values if not provided
            if (order.getOrderDate() == null) {
                order.setOrderDate(LocalDateTime.now());
            }
            
            if (order.getStatus() == null) {
                order.setStatus(Status.PENDING);
            }
            
            // Save the order
            CustomerOrder savedOrder = customerOrderDao.save(order);
            
            // Save order items if provided
            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                for (OrderItem item : order.getOrderItems()) {
                    item.setOrder(savedOrder);
                    validateOrderItem(item);
                    orderItemDao.save(item);
                }
            }
            
            // Calculate total amount
            updateOrderTotal(savedOrder.getId());
            
            return customerOrderDao.findById(savedOrder.getId()).orElse(savedOrder);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating order", e);
            throw new ServiceException("Failed to create order", e);
        }
    }
    
    @Override
    @Transactional
    public CustomerOrder updateOrder(CustomerOrder order) throws ValidationException, ServiceException {
        try {
            // Check if order exists
            Optional<CustomerOrder> existingOrderOpt = customerOrderDao.findById(order.getId());
            if (!existingOrderOpt.isPresent()) {
                throw new ServiceException("Order not found with ID: " + order.getId());
            }
            
            CustomerOrder existingOrder = existingOrderOpt.get();
            
            // Validate order data
            validateOrderForUpdate(order, existingOrder);
            
            // Preserve creation date
            order.setOrderDate(existingOrder.getOrderDate());
            
            // Update the order
            CustomerOrder updatedOrder = customerOrderDao.update(order);
            
            // Calculate total amount
            updateOrderTotal(updatedOrder.getId());
            
            return customerOrderDao.findById(updatedOrder.getId()).orElse(updatedOrder);
        } catch (ValidationException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating order", e);
            throw new ServiceException("Failed to update order", e);
        }
    }
    
    @Override
    @Transactional
    public boolean cancelOrder(Integer orderId) throws ServiceException {
        try {
            if (orderId == null) {
                throw new ServiceException("Order ID cannot be null");
            }
            
            Optional<CustomerOrder> orderOpt = customerOrderDao.findById(orderId);
            if (!orderOpt.isPresent()) {
                return false;
            }
            
            CustomerOrder order = orderOpt.get();
            
            // Check if order can be cancelled
            if (order.getStatus() == Status.SHIPPED || order.getStatus() == Status.DELIVERED
                    || order.getStatus() == Status.CANCELLED) {
                throw new ServiceException("Cannot cancel order with status: " + order.getStatus());
            }
            
            // Update order status
            order.setStatus(Status.CANCELLED);
            customerOrderDao.update(order);
            
            return true;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cancelling order", e);
            throw new ServiceException("Failed to cancel order", e);
        }
    }
    
    @Override
    @Transactional
    public Optional<CustomerOrder> updateOrderStatus(Integer orderId, Status status) 
            throws ValidationException, ServiceException {
        try {
            // Validate inputs
            if (orderId == null) {
                throw new ValidationException("Order ID cannot be null");
            }
            
            if (status == null) {
                throw new ValidationException("Status cannot be null");
            }
            
            // Find the order
            Optional<CustomerOrder> orderOpt = customerOrderDao.findById(orderId);
            if (!orderOpt.isPresent()) {
                return Optional.empty();
            }
            
            CustomerOrder order = orderOpt.get();
            
            // Validate status transition
            validateStatusTransition(order.getStatus(), status);
            
            // Update status
            if (customerOrderDao.updateStatus(orderId, status)) {
                return customerOrderDao.findById(orderId);
            } else {
                return Optional.empty();
            }
        } catch (ValidationException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating order status", e);
            throw new ServiceException("Failed to update order status", e);
        }
    }
    
    @Override
    @Transactional
    public OrderItem addOrderItem(Integer orderId, OrderItem item) 
            throws ValidationException, ServiceException {
        try {
            // Validate inputs
            if (orderId == null) {
                throw new ValidationException("Order ID cannot be null");
            }
            
            if (item == null) {
                throw new ValidationException("Order item cannot be null");
            }
            
            // Find the order
            Optional<CustomerOrder> orderOpt = customerOrderDao.findById(orderId);
            if (!orderOpt.isPresent()) {
                throw new ServiceException("Order not found with ID: " + orderId);
            }
            
            CustomerOrder order = orderOpt.get();
            
            // Check if order is in an editable state
            if (order.getStatus() != Status.PENDING) {
                throw new ValidationException("Cannot add items to order with status: " + order.getStatus());
            }
            
            // Set order reference
            item.setOrder(order);
            
            // Validate order item
            validateOrderItem(item);
            
            // Check if this product already exists in the order
            Optional<OrderItem> existingItemOpt = orderItemDao.findByOrderIdAndProductId(orderId, item.getProduct().getId());
            
            OrderItem savedItem;
            if (existingItemOpt.isPresent()) {
                // Update quantity of existing item
                OrderItem existingItem = existingItemOpt.get();
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                savedItem = orderItemDao.update(existingItem);
            } else {
                // Save new item
                savedItem = orderItemDao.save(item);
            }
            
            // Update order total
            updateOrderTotal(orderId);
            
            return savedItem;
        } catch (ValidationException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding order item", e);
            throw new ServiceException("Failed to add order item", e);
        }
    }
    
    @Override
    @Transactional
    public OrderItem updateOrderItem(OrderItem item) throws ValidationException, ServiceException {
        try {
            // Validate inputs
            if (item == null || item.getId() == null) {
                throw new ValidationException("Order item ID cannot be null");
            }
            
            // Find the order item
            Optional<OrderItem> existingItemOpt = orderItemDao.findById(item.getId());
            if (!existingItemOpt.isPresent()) {
                throw new ServiceException("Order item not found with ID: " + item.getId());
            }
            
            OrderItem existingItem = existingItemOpt.get();
            CustomerOrder order = existingItem.getOrder();
            
            // Check if order is in an editable state
            if (order.getStatus() != Status.PENDING) {
                throw new ValidationException("Cannot update items for order with status: " + order.getStatus());
            }
            
            // Preserve order reference
            item.setOrder(order);
            
            // Validate order item
            validateOrderItem(item);
            
            // Update the item
            OrderItem updatedItem = orderItemDao.update(item);
            
            // Update order total
            updateOrderTotal(order.getId());
            
            return updatedItem;
        } catch (ValidationException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating order item", e);
            throw new ServiceException("Failed to update order item", e);
        }
    }
    
    @Override
    @Transactional
    public boolean removeOrderItem(Integer orderItemId) throws ValidationException, ServiceException {
        try {
            // Validate inputs
            if (orderItemId == null) {
                throw new ValidationException("Order item ID cannot be null");
            }
            
            // Find the order item
            Optional<OrderItem> itemOpt = orderItemDao.findById(orderItemId);
            if (!itemOpt.isPresent()) {
                return false;
            }
            
            OrderItem item = itemOpt.get();
            CustomerOrder order = item.getOrder();
            
            // Check if order is in an editable state
            if (order.getStatus() != Status.PENDING) {
                throw new ValidationException("Cannot remove items from order with status: " + order.getStatus());
            }
            
            // Delete the item
            if (orderItemDao.deleteById(orderItemId)) {
                // Update order total
                updateOrderTotal(order.getId());
                return true;
            } else {
                return false;
            }
        } catch (ValidationException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error removing order item", e);
            throw new ServiceException("Failed to remove order item", e);
        }
    }
    
    @Override
    public Optional<CustomerOrder> findById(Integer orderId) throws ServiceException {
        try {
            if (orderId == null) {
                throw new ServiceException("Order ID cannot be null");
            }
            
            return customerOrderDao.findById(orderId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding order by ID", e);
            throw new ServiceException("Failed to find order by ID", e);
        }
    }
    
    @Override
    public List<CustomerOrder> findByCustomerEmail(String email) throws ServiceException {
        try {
            if (email == null || email.isEmpty()) {
                throw new ServiceException("Email cannot be empty");
            }
            
            return customerOrderDao.findByCustomerEmail(email);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding orders by customer email", e);
            throw new ServiceException("Failed to find orders by customer email", e);
        }
    }
    
    @Override
    public List<CustomerOrder> findByStatus(Status status) throws ServiceException {
        try {
            if (status == null) {
                throw new ServiceException("Status cannot be null");
            }
            
            return customerOrderDao.findByStatus(status);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding orders by status", e);
            throw new ServiceException("Failed to find orders by status", e);
        }
    }
    
    @Override
    public List<CustomerOrder> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) 
            throws ServiceException {
        try {
            if (startDate == null) {
                throw new ServiceException("Start date cannot be null");
            }
            
            if (endDate == null) {
                throw new ServiceException("End date cannot be null");
            }
            
            if (startDate.isAfter(endDate)) {
                throw new ServiceException("Start date cannot be after end date");
            }
            
            return customerOrderDao.findByDateRange(startDate, endDate);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding orders by date range", e);
            throw new ServiceException("Failed to find orders by date range", e);
        }
    }
    
    @Override
    public List<CustomerOrder> findRecentOrders(int limit) throws ServiceException {
        try {
            if (limit <= 0) {
                throw new ServiceException("Limit must be greater than zero");
            }
            
            return customerOrderDao.findRecentOrders(limit);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding recent orders", e);
            throw new ServiceException("Failed to find recent orders", e);
        }
    }
    
    @Override
    @Transactional
    public boolean processPayment(Integer orderId, Object paymentDetails) 
            throws ValidationException, ServiceException {
        try {
            // Validate inputs
            if (orderId == null) {
                throw new ValidationException("Order ID cannot be null");
            }
            
            if (paymentDetails == null) {
                throw new ValidationException("Payment details cannot be null");
            }
            
            // Find the order
            Optional<CustomerOrder> orderOpt = customerOrderDao.findById(orderId);
            if (!orderOpt.isPresent()) {
                throw new ServiceException("Order not found with ID: " + orderId);
            }
            
            CustomerOrder order = orderOpt.get();
            
            // Check if order is in a processable state
            if (order.getStatus() != Status.PENDING) {
                throw new ValidationException("Cannot process payment for order with status: " + order.getStatus());
            }
            
            // In a real implementation, this would integrate with a payment gateway
            // For now, just update the order status
            order.setStatus(Status.PAID);
            customerOrderDao.update(order);
            
            return true;
        } catch (ValidationException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing payment", e);
            throw new ServiceException("Failed to process payment", e);
        }
    }
    
    @Override
    @Transactional
    public String processShipment(Integer orderId, Object shipmentDetails) 
            throws ValidationException, ServiceException {
        try {
            // Validate inputs
            if (orderId == null) {
                throw new ValidationException("Order ID cannot be null");
            }
            
            if (shipmentDetails == null) {
                throw new ValidationException("Shipment details cannot be null");
            }
            
            // Find the order
            Optional<CustomerOrder> orderOpt = customerOrderDao.findById(orderId);
            if (!orderOpt.isPresent()) {
                throw new ServiceException("Order not found with ID: " + orderId);
            }
            
            CustomerOrder order = orderOpt.get();
            
            // Check if order is in a shippable state
            if (order.getStatus() != Status.PROCESSING && order.getStatus() != Status.PAID) {
                throw new ValidationException("Cannot process shipment for order with status: " + order.getStatus());
            }
            
            // Process stock for the order
            if (!stockService.processStockForCustomerOrder(orderId)) {
                throw new ServiceException("Failed to process stock for order");
            }
            
            // In a real implementation, this would integrate with a shipping provider
            // For now, just update the order status and generate a tracking number
            order.setStatus(Status.SHIPPED);
            customerOrderDao.update(order);
            
            // Generate a tracking number
            String trackingNumber = "TRK" + System.currentTimeMillis() + orderId;
            
            return trackingNumber;
        } catch (ValidationException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing shipment", e);
            throw new ServiceException("Failed to process shipment", e);
        }
    }
    
    /**
     * Update the total amount of an order based on its items
     */
    private void updateOrderTotal(Integer orderId) throws ServiceException {
        try {
            Optional<CustomerOrder> orderOpt = customerOrderDao.findById(orderId);
            if (!orderOpt.isPresent()) {
                throw new ServiceException("Order not found with ID: " + orderId);
            }
            
            CustomerOrder order = orderOpt.get();
            List<OrderItem> items = orderItemDao.findByOrderId(orderId);
            
            BigDecimal total = BigDecimal.ZERO;
            for (OrderItem item : items) {
                BigDecimal itemTotal = item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
                total = total.add(itemTotal);
            }
            
            order.setTotalAmount(total);
            customerOrderDao.update(order);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating order total", e);
            throw new ServiceException("Failed to update order total", e);
        }
    }
    
    /**
     * Validate order data for creation
     */
    private void validateOrder(CustomerOrder order, boolean isNewOrder) throws ValidationException {
        Map<String, String> errors = new HashMap<>();
        
        if (order == null) {
            throw new ValidationException("Order cannot be null");
        }
        
        if (order.getCustomerName() == null || order.getCustomerName().isEmpty()) {
            errors.put("customerName", "Customer name cannot be empty");
        }
        
        if (order.getCustomerEmail() == null || order.getCustomerEmail().isEmpty()) {
            errors.put("customerEmail", "Customer email cannot be empty");
        } else if (!order.getCustomerEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.put("customerEmail", "Customer email is not valid");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Order validation failed", errors);
        }
    }
    
    /**
     * Validate order data for update
     */
    private void validateOrderForUpdate(CustomerOrder order, CustomerOrder existingOrder) 
            throws ValidationException {
        Map<String, String> errors = new HashMap<>();
        
        if (order == null) {
            throw new ValidationException("Order cannot be null");
        }
        
        if (order.getCustomerName() == null || order.getCustomerName().isEmpty()) {
            errors.put("customerName", "Customer name cannot be empty");
        }
        
        if (order.getCustomerEmail() == null || order.getCustomerEmail().isEmpty()) {
            errors.put("customerEmail", "Customer email cannot be empty");
        } else if (!order.getCustomerEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.put("customerEmail", "Customer email is not valid");
        }
        
        // Validate status transition if status is being changed
        if (order.getStatus() != null && !order.getStatus().equals(existingOrder.getStatus())) {
            try {
                validateStatusTransition(existingOrder.getStatus(), order.getStatus());
            } catch (ValidationException e) {
                errors.put("status", e.getMessage());
            }
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Order validation failed", errors);
        }
    }
    
    /**
     * Validate order item data
     */
    private void validateOrderItem(OrderItem item) throws ValidationException {
        Map<String, String> errors = new HashMap<>();
        
        if (item == null) {
            throw new ValidationException("Order item cannot be null");
        }
        
        if (item.getOrder() == null) {
            errors.put("order", "Order reference cannot be null");
        }
        
        if (item.getProduct() == null) {
            errors.put("product", "Product cannot be null");
        } else {
            // Verify product exists
            Optional<Product> productOpt = productDao.findById(item.getProduct().getId());
            if (!productOpt.isPresent()) {
                errors.put("product", "Product not found with ID: " + item.getProduct().getId());
            } else if (item.getUnitPrice() == null) {
                // Use product price if not specified
                item.setUnitPrice(productOpt.get().getUnitPrice());
            }
        }
        
        if (item.getQuantity() <= 0) {
            errors.put("quantity", "Quantity must be greater than zero");
        }
        
        if (item.getUnitPrice() == null) {
            errors.put("unitPrice", "Unit price cannot be null");
        } else if (item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("unitPrice", "Unit price must be greater than zero");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Order item validation failed", errors);
        }
    }
    
    /**
     * Validate order status transition
     */
    private void validateStatusTransition(Status currentStatus, Status newStatus) throws ValidationException {
        // Valid transitions
        switch (currentStatus) {
            case PENDING:
                if (newStatus != Status.PROCESSING && newStatus != Status.PAID && newStatus != Status.CANCELLED) {
                    throw new ValidationException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case PROCESSING:
                if (newStatus != Status.SHIPPED && newStatus != Status.CANCELLED) {
                    throw new ValidationException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case PAID:
                if (newStatus != Status.PROCESSING && newStatus != Status.SHIPPED && newStatus != Status.CANCELLED) {
                    throw new ValidationException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case SHIPPED:
                if (newStatus != Status.DELIVERED) {
                    throw new ValidationException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case DELIVERED:
            case CANCELLED:
                throw new ValidationException("Cannot change status from " + currentStatus);
            default:
                throw new ValidationException("Unknown current status: " + currentStatus);
        }
    }
} 
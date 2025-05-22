package com.scm.service.impl;

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
import com.scm.dao.StockDao;
import com.scm.dao.StockMovementDao;
import com.scm.dao.SupplierOrderDao;
import com.scm.dao.SupplierOrderItemDao;
import com.scm.model.CustomerOrder;
import com.scm.model.OrderItem;
import com.scm.model.Product;
import com.scm.model.Stock;
import com.scm.model.StockMovement;
import com.scm.model.StockMovement.MovementType;
import com.scm.model.SupplierOrder;
import com.scm.model.SupplierOrderItem;
import com.scm.service.StockService;
import com.scm.service.exception.ServiceException;
import com.scm.service.exception.ValidationException;

/**
 * Implementation of StockService using EJB stateless session bean.
 */
@Stateless
public class StockServiceImpl implements StockService {
    
    private static final Logger LOGGER = Logger.getLogger(StockServiceImpl.class.getName());
    
    @Inject
    private StockDao stockDao;
    
    @Inject
    private ProductDao productDao;
    
    @Inject
    private StockMovementDao stockMovementDao;
    
    @Inject
    private CustomerOrderDao customerOrderDao;
    
    @Inject
    private OrderItemDao orderItemDao;
    
    @Inject
    private SupplierOrderDao supplierOrderDao;
    
    @Inject
    private SupplierOrderItemDao supplierOrderItemDao;
    
    @Override
    public Optional<Stock> getStockForProduct(Integer productId) throws ServiceException {
        try {
            if (productId == null) {
                throw new ServiceException("Product ID cannot be null");
            }
            
            return stockDao.findByProductId(productId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting stock for product", e);
            throw new ServiceException("Failed to get stock for product", e);
        }
    }
    
    @Override
    public Optional<Stock> getStockForProductBySku(String sku) throws ServiceException {
        try {
            if (sku == null || sku.isEmpty()) {
                throw new ServiceException("SKU cannot be empty");
            }
            
            return stockDao.findByProductSku(sku);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting stock for product by SKU", e);
            throw new ServiceException("Failed to get stock for product by SKU", e);
        }
    }
    
    @Override
    @Transactional
    public Optional<Stock> updateStockQuantity(Integer productId, int quantityChange, 
            MovementType movementType, Integer referenceId, String notes) 
            throws ValidationException, ServiceException {
        try {
            // Validate inputs
            if (productId == null) {
                throw new ValidationException("Product ID cannot be null");
            }
            
            if (movementType == null) {
                throw new ValidationException("Movement type cannot be null");
            }
            
            // Find the product
            Optional<Product> productOpt = productDao.findById(productId);
            if (!productOpt.isPresent()) {
                throw new ServiceException("Product not found with ID: " + productId);
            }
            
            Product product = productOpt.get();
            
            // Find or create stock record
            Optional<Stock> stockOpt = stockDao.findByProductId(productId);
            Stock stock;
            
            if (stockOpt.isPresent()) {
                stock = stockOpt.get();
            } else {
                // Create new stock record
                stock = new Stock();
                stock.setProduct(product);
                stock.setQuantityAvailable(0);
                stock.setLastUpdated(LocalDateTime.now());
                stock = stockDao.save(stock);
            }
            
            // Validate if quantity change would make stock negative
            if (stock.getQuantityAvailable() + quantityChange < 0) {
                throw new ValidationException("Cannot update stock: insufficient quantity available");
            }
            
            // Update stock quantity
            stock.setQuantityAvailable(stock.getQuantityAvailable() + quantityChange);
            stock.setLastUpdated(LocalDateTime.now());
            
            // Create stock movement record
            StockMovement movement = new StockMovement();
            movement.setProduct(product);
            movement.setMovementType(movementType);
            movement.setQuantity(quantityChange);
            movement.setReferenceId(referenceId);
            movement.setNotes(notes);
            movement.setMovementDate(LocalDateTime.now());
            
            stockMovementDao.save(movement);
            
            // Update stock record
            return Optional.of(stockDao.update(stock));
        } catch (ValidationException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating stock quantity", e);
            throw new ServiceException("Failed to update stock quantity", e);
        }
    }
    
    @Override
    public List<StockMovement> getStockMovementsForProduct(Integer productId) throws ServiceException {
        try {
            if (productId == null) {
                throw new ServiceException("Product ID cannot be null");
            }
            
            return stockMovementDao.findByProductId(productId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting stock movements for product", e);
            throw new ServiceException("Failed to get stock movements for product", e);
        }
    }
    
    @Override
    public List<StockMovement> getStockMovementsByType(MovementType movementType) throws ServiceException {
        try {
            if (movementType == null) {
                throw new ServiceException("Movement type cannot be null");
            }
            
            return stockMovementDao.findByMovementType(movementType);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting stock movements by type", e);
            throw new ServiceException("Failed to get stock movements by type", e);
        }
    }
    
    @Override
    public List<StockMovement> getStockMovementsByReferenceId(Integer referenceId) throws ServiceException {
        try {
            if (referenceId == null) {
                throw new ServiceException("Reference ID cannot be null");
            }
            
            return stockMovementDao.findByReferenceId(referenceId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting stock movements by reference ID", e);
            throw new ServiceException("Failed to get stock movements by reference ID", e);
        }
    }
    
    @Override
    public List<StockMovement> getStockMovementsByDateRange(LocalDateTime startDate, LocalDateTime endDate) 
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
            
            return stockMovementDao.findByDateRange(startDate, endDate);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting stock movements by date range", e);
            throw new ServiceException("Failed to get stock movements by date range", e);
        }
    }
    
    @Override
    @Transactional
    public StockMovement createStockAdjustment(Integer productId, int quantity, String notes) 
            throws ValidationException, ServiceException {
        try {
            // Validate inputs
            if (productId == null) {
                throw new ValidationException("Product ID cannot be null");
            }
            
            if (notes == null || notes.isEmpty()) {
                throw new ValidationException("Notes cannot be empty for stock adjustment");
            }
            
            // Find the product
            Optional<Product> productOpt = productDao.findById(productId);
            if (!productOpt.isPresent()) {
                throw new ServiceException("Product not found with ID: " + productId);
            }
            
            Product product = productOpt.get();
            
            // Update stock quantity
            Optional<Stock> stockOpt = updateStockQuantity(productId, quantity, 
                    MovementType.ADJUSTMENT, null, notes);
            
            if (!stockOpt.isPresent()) {
                throw new ServiceException("Failed to update stock quantity");
            }
            
            // Retrieve and return the created stock movement
            List<StockMovement> movements = stockMovementDao.findByProductId(productId);
            if (movements.isEmpty()) {
                throw new ServiceException("No stock movement record found after adjustment");
            }
            
            // The most recent movement will be the one we just created
            return movements.get(0);
        } catch (ValidationException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating stock adjustment", e);
            throw new ServiceException("Failed to create stock adjustment", e);
        }
    }
    
    @Override
    @Transactional
    public boolean processStockForCustomerOrder(Integer orderId) 
            throws ValidationException, ServiceException {
        try {
            // Validate inputs
            if (orderId == null) {
                throw new ValidationException("Order ID cannot be null");
            }
            
            // Find the order
            Optional<CustomerOrder> orderOpt = customerOrderDao.findById(orderId);
            if (!orderOpt.isPresent()) {
                throw new ServiceException("Order not found with ID: " + orderId);
            }
            
            CustomerOrder order = orderOpt.get();
            
            // Check if order is in a processable state
            if (order.getStatus() != CustomerOrder.Status.PENDING) {
                throw new ValidationException("Cannot process stock for order with status: " + order.getStatus());
            }
            
            // Get order items
            List<OrderItem> orderItems = orderItemDao.findByOrderId(orderId);
            if (orderItems.isEmpty()) {
                throw new ValidationException("Order has no items");
            }
            
            // Check stock availability for all items
            for (OrderItem item : orderItems) {
                Optional<Stock> stockOpt = stockDao.findByProductId(item.getProduct().getId());
                if (!stockOpt.isPresent() || stockOpt.get().getQuantityAvailable() < item.getQuantity()) {
                    throw new ValidationException("Insufficient stock for product: " 
                            + item.getProduct().getName() + " (SKU: " + item.getProduct().getSku() + ")");
                }
            }
            
            // Process stock for each item
            for (OrderItem item : orderItems) {
                updateStockQuantity(
                    item.getProduct().getId(),
                    -item.getQuantity(),
                    MovementType.CUSTOMER_ORDER,
                    orderId,
                    "Customer order #" + orderId
                );
            }
            
            // Update order status
            order.setStatus(CustomerOrder.Status.PROCESSING);
            customerOrderDao.update(order);
            
            return true;
        } catch (ValidationException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing stock for customer order", e);
            throw new ServiceException("Failed to process stock for customer order", e);
        }
    }
    
    @Override
    @Transactional
    public boolean processStockForSupplierOrder(Integer supplierOrderId) throws ServiceException {
        try {
            // Validate inputs
            if (supplierOrderId == null) {
                throw new ServiceException("Supplier order ID cannot be null");
            }
            
            // Find the supplier order
            Optional<SupplierOrder> orderOpt = supplierOrderDao.findById(supplierOrderId);
            if (!orderOpt.isPresent()) {
                throw new ServiceException("Supplier order not found with ID: " + supplierOrderId);
            }
            
            SupplierOrder order = orderOpt.get();
            
            // Check if order is in a processable state
            if (order.getStatus() != SupplierOrder.Status.RECEIVED) {
                throw new ServiceException("Cannot process stock for supplier order with status: " + order.getStatus());
            }
            
            // Get order items
            List<SupplierOrderItem> orderItems = supplierOrderItemDao.findByOrderId(supplierOrderId);
            if (orderItems.isEmpty()) {
                throw new ServiceException("Supplier order has no items");
            }
            
            // Process stock for each item
            for (SupplierOrderItem item : orderItems) {
                updateStockQuantity(
                    item.getProduct().getId(),
                    item.getQuantity(),
                    MovementType.SUPPLIER_ORDER,
                    supplierOrderId,
                    "Supplier order #" + supplierOrderId + " from " + order.getSupplier().getName()
                );
            }
            
            // Update order status
            order.setStatus(SupplierOrder.Status.COMPLETED);
            supplierOrderDao.update(order);
            
            return true;
        } catch (ValidationException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing stock for supplier order", e);
            throw new ServiceException("Failed to process stock for supplier order", e);
        }
    }
    
    @Override
    public boolean checkSufficientStockForOrder(Integer orderId) throws ServiceException {
        try {
            // Validate inputs
            if (orderId == null) {
                throw new ServiceException("Order ID cannot be null");
            }
            
            // Find the order
            Optional<CustomerOrder> orderOpt = customerOrderDao.findById(orderId);
            if (!orderOpt.isPresent()) {
                throw new ServiceException("Order not found with ID: " + orderId);
            }
            
            // Get order items
            List<OrderItem> orderItems = orderItemDao.findByOrderId(orderId);
            if (orderItems.isEmpty()) {
                return true; // No items to check
            }
            
            // Check stock availability for all items
            for (OrderItem item : orderItems) {
                Optional<Stock> stockOpt = stockDao.findByProductId(item.getProduct().getId());
                if (!stockOpt.isPresent() || stockOpt.get().getQuantityAvailable() < item.getQuantity()) {
                    return false; // Insufficient stock
                }
            }
            
            return true; // Sufficient stock for all items
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking sufficient stock for order", e);
            throw new ServiceException("Failed to check sufficient stock for order", e);
        }
    }
    
    @Override
    public List<Stock> getProductsNeedingReorder() throws ServiceException {
        try {
            List<Product> lowStockProducts = productDao.findLowStockProducts();
            return stockDao.findByProductIds(lowStockProducts.stream()
                    .map(Product::getId)
                    .toArray(Integer[]::new));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting products needing reorder", e);
            throw new ServiceException("Failed to get products needing reorder", e);
        }
    }
} 
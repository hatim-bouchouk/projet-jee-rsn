package com.scm.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.scm.model.Stock;
import com.scm.model.StockMovement;
import com.scm.model.StockMovement.MovementType;
import com.scm.service.exception.ServiceException;
import com.scm.service.exception.ValidationException;

/**
 * Service interface for inventory management and stock movements.
 */
public interface StockService {
    
    /**
     * Get current stock for a product
     * 
     * @param productId Product ID
     * @return Optional containing stock if found, empty otherwise
     * @throws ServiceException if a system error occurs
     */
    Optional<Stock> getStockForProduct(Integer productId) throws ServiceException;
    
    /**
     * Get current stock for a product by SKU
     * 
     * @param sku Product SKU
     * @return Optional containing stock if found, empty otherwise
     * @throws ServiceException if a system error occurs
     */
    Optional<Stock> getStockForProductBySku(String sku) throws ServiceException;
    
    /**
     * Update stock quantity
     * 
     * @param productId Product ID
     * @param quantityChange Change in quantity (positive or negative)
     * @param movementType Type of movement
     * @param referenceId Reference ID (e.g., order ID)
     * @param notes Additional notes
     * @return Updated stock if found, empty otherwise
     * @throws ValidationException if quantity is invalid
     * @throws ServiceException if a system error occurs
     */
    Optional<Stock> updateStockQuantity(Integer productId, int quantityChange, 
            MovementType movementType, Integer referenceId, String notes) 
            throws ValidationException, ServiceException;
    
    /**
     * Get stock movements for a product
     * 
     * @param productId Product ID
     * @return List of stock movements for the product
     * @throws ServiceException if a system error occurs
     */
    List<StockMovement> getStockMovementsForProduct(Integer productId) throws ServiceException;
    
    /**
     * Get stock movements by type
     * 
     * @param movementType Movement type
     * @return List of stock movements of the specified type
     * @throws ServiceException if a system error occurs
     */
    List<StockMovement> getStockMovementsByType(MovementType movementType) throws ServiceException;
    
    /**
     * Get stock movements by reference ID
     * 
     * @param referenceId Reference ID (e.g., order ID)
     * @return List of stock movements with the specified reference
     * @throws ServiceException if a system error occurs
     */
    List<StockMovement> getStockMovementsByReferenceId(Integer referenceId) throws ServiceException;
    
    /**
     * Get stock movements in a date range
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return List of stock movements in the date range
     * @throws ServiceException if a system error occurs
     */
    List<StockMovement> getStockMovementsByDateRange(LocalDateTime startDate, LocalDateTime endDate) 
            throws ServiceException;
    
    /**
     * Create a stock adjustment
     * 
     * @param productId Product ID
     * @param quantity Adjustment quantity (positive or negative)
     * @param notes Reason for adjustment
     * @return Created stock movement
     * @throws ValidationException if data is invalid
     * @throws ServiceException if a system error occurs
     */
    StockMovement createStockAdjustment(Integer productId, int quantity, String notes) 
            throws ValidationException, ServiceException;
    
    /**
     * Process stock for a customer order
     * 
     * @param orderId Order ID
     * @return true if stock was successfully processed, false otherwise
     * @throws ValidationException if there is insufficient stock
     * @throws ServiceException if a system error occurs
     */
    boolean processStockForCustomerOrder(Integer orderId) 
            throws ValidationException, ServiceException;
    
    /**
     * Process stock for a supplier order
     * 
     * @param supplierOrderId Supplier order ID
     * @return true if stock was successfully processed, false otherwise
     * @throws ServiceException if a system error occurs
     */
    boolean processStockForSupplierOrder(Integer supplierOrderId) throws ServiceException;
    
    /**
     * Check if there is sufficient stock for an order
     * 
     * @param orderId Order ID
     * @return true if there is sufficient stock, false otherwise
     * @throws ServiceException if a system error occurs
     */
    boolean checkSufficientStockForOrder(Integer orderId) throws ServiceException;
    
    /**
     * Get products that need reordering (stock below reorder level)
     * 
     * @return List of stocks that need reordering
     * @throws ServiceException if a system error occurs
     */
    List<Stock> getProductsNeedingReorder() throws ServiceException;
} 
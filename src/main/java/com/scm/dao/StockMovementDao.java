package com.scm.dao;

import java.time.LocalDateTime;
import java.util.List;

import com.scm.model.StockMovement;
import com.scm.model.StockMovement.MovementType;

/**
 * DAO interface for StockMovement entity with custom query methods
 */
public interface StockMovementDao extends GenericDao<StockMovement, Integer> {
    
    /**
     * Find stock movements by product ID
     * 
     * @param productId Product ID
     * @return List of stock movements for the product
     */
    List<StockMovement> findByProductId(Integer productId);
    
    /**
     * Find stock movements by movement type
     * 
     * @param type Movement type
     * @return List of stock movements of the specified type
     */
    List<StockMovement> findByMovementType(MovementType type);
    
    /**
     * Find stock movements by reference ID (order ID)
     * 
     * @param referenceId Reference ID (order ID)
     * @return List of stock movements with the specified reference
     */
    List<StockMovement> findByReferenceId(Integer referenceId);
    
    /**
     * Find stock movements in a date range
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return List of stock movements in the date range
     */
    List<StockMovement> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find stock movements by product ID and date range
     * 
     * @param productId Product ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of stock movements for the product in the date range
     */
    List<StockMovement> findByProductAndDateRange(Integer productId, LocalDateTime startDate, LocalDateTime endDate);
} 
package com.scm.dao.impl;

import java.time.LocalDateTime;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.scm.dao.StockMovementDao;
import com.scm.model.StockMovement;
import com.scm.model.StockMovement.MovementType;

/**
 * JPA implementation of StockMovementDao
 */
@Stateless
public class StockMovementDaoImpl extends AbstractJpaDao<StockMovement, Integer> implements StockMovementDao {
    
    public StockMovementDaoImpl() {
        super(StockMovement.class);
    }
    
    @Override
    public List<StockMovement> findByProductId(Integer productId) {
        TypedQuery<StockMovement> query = entityManager.createQuery(
                "SELECT sm FROM StockMovement sm WHERE sm.product.id = :productId " +
                "ORDER BY sm.movementDate DESC", StockMovement.class);
        query.setParameter("productId", productId);
        
        return query.getResultList();
    }
    
    @Override
    public List<StockMovement> findByMovementType(MovementType type) {
        TypedQuery<StockMovement> query = entityManager.createQuery(
                "SELECT sm FROM StockMovement sm WHERE sm.movementType = :type " +
                "ORDER BY sm.movementDate DESC", StockMovement.class);
        query.setParameter("type", type);
        
        return query.getResultList();
    }
    
    @Override
    public List<StockMovement> findByReferenceId(Integer referenceId) {
        TypedQuery<StockMovement> query = entityManager.createQuery(
                "SELECT sm FROM StockMovement sm WHERE sm.referenceId = :referenceId " +
                "ORDER BY sm.movementDate DESC", StockMovement.class);
        query.setParameter("referenceId", referenceId);
        
        return query.getResultList();
    }
    
    @Override
    public List<StockMovement> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        TypedQuery<StockMovement> query = entityManager.createQuery(
                "SELECT sm FROM StockMovement sm WHERE sm.movementDate BETWEEN :startDate AND :endDate " +
                "ORDER BY sm.movementDate DESC", StockMovement.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        return query.getResultList();
    }
    
    @Override
    public List<StockMovement> findByProductAndDateRange(Integer productId, LocalDateTime startDate, LocalDateTime endDate) {
        TypedQuery<StockMovement> query = entityManager.createQuery(
                "SELECT sm FROM StockMovement sm WHERE sm.product.id = :productId " +
                "AND sm.movementDate BETWEEN :startDate AND :endDate " +
                "ORDER BY sm.movementDate DESC", StockMovement.class);
        query.setParameter("productId", productId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        return query.getResultList();
    }
} 
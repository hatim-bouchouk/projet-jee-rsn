package com.scm.dao.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.scm.dao.SupplierOrderDao;
import com.scm.model.SupplierOrder;
import com.scm.model.SupplierOrder.Status;

/**
 * JPA implementation of SupplierOrderDao
 */
@Stateless
public class SupplierOrderDaoImpl extends AbstractJpaDao<SupplierOrder, Integer> implements SupplierOrderDao {
    
    public SupplierOrderDaoImpl() {
        super(SupplierOrder.class);
    }
    
    @Override
    public List<SupplierOrder> findBySupplierId(Integer supplierId) {
        TypedQuery<SupplierOrder> query = entityManager.createQuery(
                "SELECT o FROM SupplierOrder o WHERE o.supplier.id = :supplierId " +
                "ORDER BY o.orderDate DESC", SupplierOrder.class);
        query.setParameter("supplierId", supplierId);
        
        return query.getResultList();
    }
    
    @Override
    public List<SupplierOrder> findByStatus(Status status) {
        TypedQuery<SupplierOrder> query = entityManager.createQuery(
                "SELECT o FROM SupplierOrder o WHERE o.status = :status " +
                "ORDER BY o.orderDate DESC", SupplierOrder.class);
        query.setParameter("status", status);
        
        return query.getResultList();
    }
    
    @Override
    public List<SupplierOrder> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        TypedQuery<SupplierOrder> query = entityManager.createQuery(
                "SELECT o FROM SupplierOrder o WHERE o.orderDate BETWEEN :startDate AND :endDate " +
                "ORDER BY o.orderDate DESC", SupplierOrder.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        return query.getResultList();
    }
    
    @Override
    public List<SupplierOrder> findByExpectedDeliveryDateRange(LocalDate startDate, LocalDate endDate) {
        TypedQuery<SupplierOrder> query = entityManager.createQuery(
                "SELECT o FROM SupplierOrder o WHERE o.expectedDelivery BETWEEN :startDate AND :endDate " +
                "ORDER BY o.expectedDelivery", SupplierOrder.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        return query.getResultList();
    }
    
    @Override
    public List<SupplierOrder> findOverdueOrders() {
        LocalDate today = LocalDate.now();
        
        TypedQuery<SupplierOrder> query = entityManager.createQuery(
                "SELECT o FROM SupplierOrder o WHERE o.expectedDelivery < :today " +
                "AND o.status NOT IN ('delivered', 'cancelled') " +
                "ORDER BY o.expectedDelivery", SupplierOrder.class);
        query.setParameter("today", today);
        
        return query.getResultList();
    }
    
    @Override
    public List<SupplierOrder> findRecentOrders(int limit) {
        TypedQuery<SupplierOrder> query = entityManager.createQuery(
                "SELECT o FROM SupplierOrder o ORDER BY o.orderDate DESC", SupplierOrder.class);
        query.setMaxResults(limit);
        
        return query.getResultList();
    }
    
    @Override
    public List<SupplierOrder> findByProduct(Integer productId) {
        TypedQuery<SupplierOrder> query = entityManager.createQuery(
                "SELECT DISTINCT o FROM SupplierOrder o JOIN o.orderItems i " +
                "WHERE i.product.id = :productId " +
                "ORDER BY o.orderDate DESC", SupplierOrder.class);
        query.setParameter("productId", productId);
        
        return query.getResultList();
    }
    
    @Override
    public boolean updateStatus(Integer orderId, Status status) {
        Optional<SupplierOrder> orderOpt = findById(orderId);
        
        if (orderOpt.isPresent()) {
            SupplierOrder order = orderOpt.get();
            order.setStatus(status);
            update(order);
            return true;
        }
        
        return false;
    }
} 
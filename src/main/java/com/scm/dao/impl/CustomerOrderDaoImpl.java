package com.scm.dao.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.scm.dao.CustomerOrderDao;
import com.scm.model.CustomerOrder;
import com.scm.model.CustomerOrder.Status;

/**
 * JPA implementation of CustomerOrderDao
 */
@Stateless
public class CustomerOrderDaoImpl extends AbstractJpaDao<CustomerOrder, Integer> implements CustomerOrderDao {
    
    public CustomerOrderDaoImpl() {
        super(CustomerOrder.class);
    }
    
    @Override
    public List<CustomerOrder> findByCustomerEmail(String email) {
        TypedQuery<CustomerOrder> query = entityManager.createQuery(
                "SELECT o FROM CustomerOrder o WHERE o.customerEmail = :email " +
                "ORDER BY o.orderDate DESC", CustomerOrder.class);
        query.setParameter("email", email);
        
        return query.getResultList();
    }
    
    @Override
    public List<CustomerOrder> findByStatus(Status status) {
        TypedQuery<CustomerOrder> query = entityManager.createQuery(
                "SELECT o FROM CustomerOrder o WHERE o.status = :status " +
                "ORDER BY o.orderDate DESC", CustomerOrder.class);
        query.setParameter("status", status);
        
        return query.getResultList();
    }
    
    @Override
    public List<CustomerOrder> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        TypedQuery<CustomerOrder> query = entityManager.createQuery(
                "SELECT o FROM CustomerOrder o WHERE o.orderDate BETWEEN :startDate AND :endDate " +
                "ORDER BY o.orderDate DESC", CustomerOrder.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        return query.getResultList();
    }
    
    @Override
    public List<CustomerOrder> findRecentOrders(int limit) {
        TypedQuery<CustomerOrder> query = entityManager.createQuery(
                "SELECT o FROM CustomerOrder o ORDER BY o.orderDate DESC", CustomerOrder.class);
        query.setMaxResults(limit);
        
        return query.getResultList();
    }
    
    @Override
    public List<CustomerOrder> findByProduct(Integer productId) {
        TypedQuery<CustomerOrder> query = entityManager.createQuery(
                "SELECT DISTINCT o FROM CustomerOrder o JOIN o.orderItems i " +
                "WHERE i.product.id = :productId " +
                "ORDER BY o.orderDate DESC", CustomerOrder.class);
        query.setParameter("productId", productId);
        
        return query.getResultList();
    }
    
    @Override
    public boolean updateStatus(Integer orderId, Status status) {
        Optional<CustomerOrder> orderOpt = findById(orderId);
        
        if (orderOpt.isPresent()) {
            CustomerOrder order = orderOpt.get();
            order.setStatus(status);
            update(order);
            return true;
        }
        
        return false;
    }
}
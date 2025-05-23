package com.scm.dao.impl;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.scm.dao.OrderItemDao;
import com.scm.model.OrderItem;

/**
 * JPA implementation of OrderItemDao
 */
@Stateless
public class OrderItemDaoImpl extends AbstractJpaDao<OrderItem, Integer> implements OrderItemDao {
    
    public OrderItemDaoImpl() {
        super(OrderItem.class);
    }
    
    @Override
    public List<OrderItem> findByOrderId(Integer orderId) {
        TypedQuery<OrderItem> query = entityManager.createQuery(
                "SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId", OrderItem.class);
        query.setParameter("orderId", orderId);
        
        return query.getResultList();
    }
    
    @Override
    public List<OrderItem> findByProductId(Integer productId) {
        TypedQuery<OrderItem> query = entityManager.createQuery(
                "SELECT oi FROM OrderItem oi WHERE oi.product.id = :productId", OrderItem.class);
        query.setParameter("productId", productId);
        
        return query.getResultList();
    }
    
    @Override
    public void deleteByOrderId(Integer orderId) {
        entityManager.createQuery(
                "DELETE FROM OrderItem oi WHERE oi.order.id = :orderId")
                .setParameter("orderId", orderId)
                .executeUpdate();
    }
    
    @Override
    public Optional<OrderItem> findByOrderIdAndProductId(Integer orderId, Integer productId) {
        TypedQuery<OrderItem> query = entityManager.createQuery(
                "SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.product.id = :productId", 
                OrderItem.class);
        query.setParameter("orderId", orderId);
        query.setParameter("productId", productId);
        
        return getSingleResult(query);
    }
} 
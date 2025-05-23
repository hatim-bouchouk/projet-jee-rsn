package com.scm.dao.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.scm.dao.SupplierOrderItemDao;
import com.scm.model.SupplierOrderItem;

/**
 * JPA implementation of SupplierOrderItemDao
 */
@Stateless
public class SupplierOrderItemDaoImpl extends AbstractJpaDao<SupplierOrderItem, Integer> implements SupplierOrderItemDao {
    
    public SupplierOrderItemDaoImpl() {
        super(SupplierOrderItem.class);
    }
    
    @Override
    public List<SupplierOrderItem> findBySupplierOrderId(Integer supplierOrderId) {
        TypedQuery<SupplierOrderItem> query = entityManager.createQuery(
                "SELECT soi FROM SupplierOrderItem soi WHERE soi.supplierOrder.id = :supplierOrderId", 
                SupplierOrderItem.class);
        query.setParameter("supplierOrderId", supplierOrderId);
        
        return query.getResultList();
    }
    
    @Override
    public List<SupplierOrderItem> findByProductId(Integer productId) {
        TypedQuery<SupplierOrderItem> query = entityManager.createQuery(
                "SELECT soi FROM SupplierOrderItem soi WHERE soi.product.id = :productId", 
                SupplierOrderItem.class);
        query.setParameter("productId", productId);
        
        return query.getResultList();
    }
    
    @Override
    public void deleteBySupplierOrderId(Integer supplierOrderId) {
        entityManager.createQuery(
                "DELETE FROM SupplierOrderItem soi WHERE soi.supplierOrder.id = :supplierOrderId")
                .setParameter("supplierOrderId", supplierOrderId)
                .executeUpdate();
    }
} 
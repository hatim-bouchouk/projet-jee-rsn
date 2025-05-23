package com.scm.dao.impl;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.scm.dao.SupplierProductDao;
import com.scm.model.SupplierProduct;
import com.scm.model.SupplierProductId;

/**
 * JPA implementation of SupplierProductDao
 */
@Stateless
public class SupplierProductDaoImpl extends AbstractJpaDao<SupplierProduct, SupplierProductId> implements SupplierProductDao {
    
    public SupplierProductDaoImpl() {
        super(SupplierProduct.class);
    }
    
    @Override
    public List<SupplierProduct> findBySupplier(Integer supplierId) {
        TypedQuery<SupplierProduct> query = entityManager.createQuery(
                "SELECT sp FROM SupplierProduct sp WHERE sp.supplier.id = :supplierId", SupplierProduct.class);
        query.setParameter("supplierId", supplierId);
        
        return query.getResultList();
    }
    
    @Override
    public List<SupplierProduct> findByProduct(Integer productId) {
        TypedQuery<SupplierProduct> query = entityManager.createQuery(
                "SELECT sp FROM SupplierProduct sp WHERE sp.product.id = :productId", SupplierProduct.class);
        query.setParameter("productId", productId);
        
        return query.getResultList();
    }
    
    @Override
    public Optional<SupplierProduct> findBySupplierAndProduct(Integer supplierId, Integer productId) {
        TypedQuery<SupplierProduct> query = entityManager.createQuery(
                "SELECT sp FROM SupplierProduct sp WHERE sp.supplier.id = :supplierId AND sp.product.id = :productId", 
                SupplierProduct.class);
        query.setParameter("supplierId", supplierId);
        query.setParameter("productId", productId);
        
        return getSingleResult(query);
    }
    
    @Override
    public void deleteBySupplier(Integer supplierId) {
        entityManager.createQuery(
                "DELETE FROM SupplierProduct sp WHERE sp.supplier.id = :supplierId")
                .setParameter("supplierId", supplierId)
                .executeUpdate();
    }
    
    @Override
    public void deleteByProduct(Integer productId) {
        entityManager.createQuery(
                "DELETE FROM SupplierProduct sp WHERE sp.product.id = :productId")
                .setParameter("productId", productId)
                .executeUpdate();
    }
    
    public Optional<SupplierProduct> findLowestCostSupplierForProduct(Integer productId) {
        TypedQuery<SupplierProduct> query = entityManager.createQuery(
                "SELECT sp FROM SupplierProduct sp WHERE sp.product.id = :productId " +
                "ORDER BY sp.unitCost ASC", 
                SupplierProduct.class);
        query.setParameter("productId", productId);
        query.setMaxResults(1);
        
        return getSingleResult(query);
    }
    
    public Optional<SupplierProduct> findFastestDeliverySupplierForProduct(Integer productId) {
        TypedQuery<SupplierProduct> query = entityManager.createQuery(
                "SELECT sp FROM SupplierProduct sp WHERE sp.product.id = :productId " +
                "ORDER BY sp.leadTimeDays ASC", 
                SupplierProduct.class);
        query.setParameter("productId", productId);
        query.setMaxResults(1);
        
        return getSingleResult(query);
    }
} 
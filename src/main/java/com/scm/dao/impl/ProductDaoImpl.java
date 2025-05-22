package com.scm.dao.impl;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.scm.dao.ProductDao;
import com.scm.model.Product;

/**
 * JPA implementation of ProductDao
 */
@Stateless
public class ProductDaoImpl extends AbstractJpaDao<Product, Integer> implements ProductDao {
    
    public ProductDaoImpl() {
        super(Product.class);
    }
    
    @Override
    public Optional<Product> findBySku(String sku) {
        TypedQuery<Product> query = entityManager.createQuery(
                "SELECT p FROM Product p WHERE p.sku = :sku", Product.class);
        query.setParameter("sku", sku);
        
        return getSingleResult(query);
    }
    
    @Override
    public List<Product> findByNameContaining(String name) {
        TypedQuery<Product> query = entityManager.createQuery(
                "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:name)", Product.class);
        query.setParameter("name", "%" + name + "%");
        
        return query.getResultList();
    }
    
    @Override
    public List<Product> findLowStockProducts() {
        TypedQuery<Product> query = entityManager.createQuery(
                "SELECT p FROM Product p JOIN p.stock s " +
                "WHERE s.quantityAvailable <= p.reorderLevel AND p.reorderLevel > 0", 
                Product.class);
        
        return query.getResultList();
    }
    
    @Override
    public List<Product> findOutOfStockProducts() {
        TypedQuery<Product> query = entityManager.createQuery(
                "SELECT p FROM Product p JOIN p.stock s " +
                "WHERE s.quantityAvailable = 0", 
                Product.class);
        
        return query.getResultList();
    }
    
    @Override
    public List<Product> findBySupplier(Integer supplierId) {
        TypedQuery<Product> query = entityManager.createQuery(
                "SELECT DISTINCT p FROM Product p " +
                "JOIN p.supplierProducts sp " +
                "WHERE sp.supplier.id = :supplierId", 
                Product.class);
        query.setParameter("supplierId", supplierId);
        
        return query.getResultList();
    }
    
    @Override
    public boolean skuExists(String sku) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.sku = :sku", Long.class);
        query.setParameter("sku", sku);
        
        return query.getSingleResult() > 0;
    }
} 
package com.scm.dao.impl;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.scm.dao.SupplierDao;
import com.scm.model.Supplier;

/**
 * JPA implementation of SupplierDao
 */
@Stateless
public class SupplierDaoImpl extends AbstractJpaDao<Supplier, Integer> implements SupplierDao {
    
    public SupplierDaoImpl() {
        super(Supplier.class);
    }
    
    @Override
    public Optional<Supplier> findByEmail(String email) {
        TypedQuery<Supplier> query = entityManager.createQuery(
                "SELECT s FROM Supplier s WHERE s.email = :email", Supplier.class);
        query.setParameter("email", email);
        
        return getSingleResult(query);
    }
    
    @Override
    public List<Supplier> findByNameContaining(String name) {
        TypedQuery<Supplier> query = entityManager.createQuery(
                "SELECT s FROM Supplier s WHERE LOWER(s.name) LIKE LOWER(:name)", Supplier.class);
        query.setParameter("name", "%" + name + "%");
        
        return query.getResultList();
    }
    
    @Override
    public List<Supplier> findByProduct(Integer productId) {
        TypedQuery<Supplier> query = entityManager.createQuery(
                "SELECT DISTINCT s FROM Supplier s " +
                "JOIN s.supplierProducts sp " +
                "WHERE sp.product.id = :productId", 
                Supplier.class);
        query.setParameter("productId", productId);
        
        return query.getResultList();
    }
    
    @Override
    public List<Supplier> findByProductSku(String sku) {
        TypedQuery<Supplier> query = entityManager.createQuery(
                "SELECT DISTINCT s FROM Supplier s " +
                "JOIN s.supplierProducts sp " +
                "JOIN sp.product p " +
                "WHERE p.sku = :sku", 
                Supplier.class);
        query.setParameter("sku", sku);
        
        return query.getResultList();
    }
    
    @Override
    public boolean emailExists(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(s) FROM Supplier s WHERE s.email = :email", Long.class);
        query.setParameter("email", email);
        
        return query.getSingleResult() > 0;
    }
} 
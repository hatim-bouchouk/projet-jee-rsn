package com.scm.dao.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.scm.dao.GenericDao;

/**
 * Abstract JPA DAO implementation that provides common CRUD operations for all entities.
 * 
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public abstract class AbstractJpaDao<T, ID extends Serializable> implements GenericDao<T, ID> {
    
    @PersistenceContext
    protected EntityManager entityManager;
    
    private final Class<T> entityClass;
    
    /**
     * Constructor that takes the entity class for this DAO
     * 
     * @param entityClass Entity class
     */
    public AbstractJpaDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    
    @Override
    public T save(T entity) {
        entityManager.persist(entity);
        return entity;
    }
    
    @Override
    public T update(T entity) {
        return entityManager.merge(entity);
    }
    
    @Override
    public T saveOrUpdate(T entity) {
        return entityManager.merge(entity);
    }
    
    @Override
    public void delete(T entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }
    
    @Override
    public boolean deleteById(ID id) {
        Optional<T> entity = findById(id);
        if (entity.isPresent()) {
            delete(entity.get());
            return true;
        }
        return false;
    }
    
    @Override
    public Optional<T> findById(ID id) {
        T entity = entityManager.find(entityClass, id);
        return Optional.ofNullable(entity);
    }
    
    @Override
    public List<T> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> rootEntry = cq.from(entityClass);
        CriteriaQuery<T> all = cq.select(rootEntry);
        
        TypedQuery<T> allQuery = entityManager.createQuery(all);
        return allQuery.getResultList();
    }
    
    @Override
    public List<T> findAll(int startPosition, int maxResults) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> rootEntry = cq.from(entityClass);
        CriteriaQuery<T> all = cq.select(rootEntry);
        
        TypedQuery<T> allQuery = entityManager.createQuery(all);
        allQuery.setFirstResult(startPosition);
        allQuery.setMaxResults(maxResults);
        return allQuery.getResultList();
    }
    
    @Override
    public long count() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(entityClass)));
        return entityManager.createQuery(cq).getSingleResult();
    }
    
    @Override
    public boolean exists(ID id) {
        return findById(id).isPresent();
    }
    
    @Override
    public void flush() {
        entityManager.flush();
    }
    
    @Override
    public void clear() {
        entityManager.clear();
    }
    
    /**
     * Get a single result from a query or empty Optional if none found
     * 
     * @param <E> Result type
     * @param query The query to execute
     * @return Optional containing the result or empty
     */
    protected <E> Optional<E> getSingleResult(TypedQuery<E> query) {
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
} 
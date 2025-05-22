package com.scm.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Generic DAO interface that defines common CRUD operations for all entities.
 * 
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public interface GenericDao<T, ID extends Serializable> {
    
    /**
     * Persist the entity to the database
     * 
     * @param entity Entity to save
     * @return The persisted entity with generated ID
     */
    T save(T entity);
    
    /**
     * Update an existing entity in the database
     * 
     * @param entity Entity to update
     * @return The updated entity
     */
    T update(T entity);
    
    /**
     * Save or update an entity based on whether it exists or not
     * 
     * @param entity Entity to save or update
     * @return The saved or updated entity
     */
    T saveOrUpdate(T entity);
    
    /**
     * Delete an entity from the database
     * 
     * @param entity Entity to delete
     */
    void delete(T entity);
    
    /**
     * Delete an entity by its ID
     * 
     * @param id Entity's ID
     * @return true if the entity was found and deleted, false otherwise
     */
    boolean deleteById(ID id);
    
    /**
     * Find an entity by its ID
     * 
     * @param id Entity's ID
     * @return Optional containing entity if found, empty otherwise
     */
    Optional<T> findById(ID id);
    
    /**
     * Find all entities of type T
     * 
     * @return List of all entities
     */
    List<T> findAll();
    
    /**
     * Find all entities with pagination
     * 
     * @param startPosition Starting position
     * @param maxResults Maximum number of results to return
     * @return List of entities
     */
    List<T> findAll(int startPosition, int maxResults);
    
    /**
     * Count all entities of type T
     * 
     * @return Count of entities
     */
    long count();
    
    /**
     * Check if an entity with the given ID exists
     * 
     * @param id Entity's ID
     * @return true if entity exists, false otherwise
     */
    boolean exists(ID id);
    
    /**
     * Flush pending changes to the database
     */
    void flush();
    
    /**
     * Clear the persistence context
     */
    void clear();
} 
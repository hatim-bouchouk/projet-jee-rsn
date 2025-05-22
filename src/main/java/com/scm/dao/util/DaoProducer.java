package com.scm.dao.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * CDI Producer for EntityManager.
 * This class makes the EntityManager available for injection in the application.
 */
@ApplicationScoped
public class DaoProducer {
    
    @PersistenceContext(unitName = "scmPU")
    private EntityManager entityManager;
    
    /**
     * Produces an EntityManager for injection
     * 
     * @return Entity manager
     */
    @Produces
    public EntityManager getEntityManager() {
        return entityManager;
    }
} 
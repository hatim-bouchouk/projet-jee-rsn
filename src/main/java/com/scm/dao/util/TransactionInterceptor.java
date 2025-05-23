package com.scm.dao.util;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import com.scm.dao.exception.DaoException;

/**
 * CDI Interceptor for transaction management.
 * This interceptor handles beginning and committing transactions for methods
 * annotated with @Transactional.
 */
@Interceptor
@Transactional
@Priority(Interceptor.Priority.APPLICATION)
public class TransactionInterceptor {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Around invoke method that handles transaction management
     * 
     * @param context Invocation context
     * @return Result of method invocation
     * @throws Exception if an error occurs
     */
    @AroundInvoke
    public Object manageTransaction(InvocationContext context) throws Exception {
        try {
            // Check if a transaction is already active
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
                boolean completedSuccessfully = false;
                
                try {
                    Object result = context.proceed();
                    completedSuccessfully = true;
                    return result;
                } finally {
                    if (completedSuccessfully) {
                        entityManager.getTransaction().commit();
                    } else {
                        entityManager.getTransaction().rollback();
                    }
                }
            } else {
                // Transaction already active (e.g., nested call)
                return context.proceed();
            }
        } catch (Exception e) {
            throw new DaoException("Transaction failed", e);
        }
    }
} 
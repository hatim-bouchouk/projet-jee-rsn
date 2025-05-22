package com.scm.dao.impl;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.scm.dao.UserDao;
import com.scm.model.User;
import com.scm.model.User.Role;

/**
 * JPA implementation of UserDao
 */
@Stateless
public class UserDaoImpl extends AbstractJpaDao<User, Integer> implements UserDao {
    
    public UserDaoImpl() {
        super(User.class);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", username);
        
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        
        return getSingleResult(query);
    }
    
    @Override
    public List<User> findByRole(Role role) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.role = :role", User.class);
        query.setParameter("role", role);
        
        return query.getResultList();
    }
    
    @Override
    public boolean usernameExists(String username) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class);
        query.setParameter("username", username);
        
        return query.getSingleResult() > 0;
    }
    
    @Override
    public boolean emailExists(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
        query.setParameter("email", email);
        
        return query.getSingleResult() > 0;
    }
} 
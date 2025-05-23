package com.scm.dao.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.scm.dao.StockDao;
import com.scm.model.Stock;

/**
 * JPA implementation of StockDao
 */
@Stateless
public class StockDaoImpl extends AbstractJpaDao<Stock, Integer> implements StockDao {
    
    public StockDaoImpl() {
        super(Stock.class);
    }
    
    @Override
    public Optional<Stock> findByProductId(Integer productId) {
        TypedQuery<Stock> query = entityManager.createQuery(
                "SELECT s FROM Stock s WHERE s.product.id = :productId", Stock.class);
        query.setParameter("productId", productId);
        
        return getSingleResult(query);
    }
    
    @Override
    public Optional<Stock> findByProductSku(String sku) {
        TypedQuery<Stock> query = entityManager.createQuery(
                "SELECT s FROM Stock s JOIN s.product p WHERE p.sku = :sku", Stock.class);
        query.setParameter("sku", sku);
        
        return getSingleResult(query);
    }
    
    @Override
    public List<Stock> findByProductIds(Integer[] productIds) {
        if (productIds == null || productIds.length == 0) {
            return List.of();
        }
        
        String idList = Arrays.stream(productIds)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        
        TypedQuery<Stock> query = entityManager.createQuery(
                "SELECT s FROM Stock s WHERE s.product.id IN (" + idList + ")", Stock.class);
        
        return query.getResultList();
    }
    
    @Override
    public Optional<Stock> updateQuantity(Integer productId, int quantityChange) {
        Optional<Stock> stockOpt = findByProductId(productId);
        
        if (stockOpt.isPresent()) {
            Stock stock = stockOpt.get();
            stock.setQuantityAvailable(stock.getQuantityAvailable() + quantityChange);
            stock.setLastUpdated(LocalDateTime.now());
            
            return Optional.of(update(stock));
        }
        
        return Optional.empty();
    }
} 
package com.scm.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.scm.model.CustomerOrder;
import com.scm.model.Product;
import com.scm.model.Supplier;
import com.scm.service.exception.ServiceException;

/**
 * Service interface for dashboard statistics and reporting.
 */
public interface DashboardService {
    
    /**
     * Get sales statistics for a date range
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return Map of statistics (total sales, order count, average order value, etc.)
     * @throws ServiceException if a system error occurs
     */
    Map<String, Object> getSalesStatistics(LocalDateTime startDate, LocalDateTime endDate) 
            throws ServiceException;
    
    /**
     * Get inventory statistics
     * 
     * @return Map of statistics (total products, low stock count, out of stock count, etc.)
     * @throws ServiceException if a system error occurs
     */
    Map<String, Object> getInventoryStatistics() throws ServiceException;
    
    /**
     * Get supplier statistics
     * 
     * @return Map of statistics (total suppliers, active suppliers, average lead time, etc.)
     * @throws ServiceException if a system error occurs
     */
    Map<String, Object> getSupplierStatistics() throws ServiceException;
    
    /**
     * Get top selling products for a date range
     * 
     * @param startDate Start date
     * @param endDate End date
     * @param limit Maximum number of products to return
     * @return List of products with sales quantities
     * @throws ServiceException if a system error occurs
     */
    List<Map<String, Object>> getTopSellingProducts(
            LocalDateTime startDate, LocalDateTime endDate, int limit) 
            throws ServiceException;
    
    /**
     * Get sales by time period (day, week, month, etc.)
     * 
     * @param startDate Start date
     * @param endDate End date
     * @param groupBy Group by period (day, week, month, year)
     * @return List of period sales statistics
     * @throws ServiceException if a system error occurs
     */
    List<Map<String, Object>> getSalesByTimePeriod(
            LocalDateTime startDate, LocalDateTime endDate, String groupBy) 
            throws ServiceException;
    
    /**
     * Get recent activity for the dashboard
     * 
     * @param limit Maximum number of items to return
     * @return List of recent activities (orders, stock movements, etc.)
     * @throws ServiceException if a system error occurs
     */
    List<Map<String, Object>> getRecentActivity(int limit) throws ServiceException;
    
    /**
     * Get sales by product category
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return Map of category to sales amount
     * @throws ServiceException if a system error occurs
     */
    Map<String, BigDecimal> getSalesByCategory(LocalDateTime startDate, LocalDateTime endDate) 
            throws ServiceException;
    
    /**
     * Get orders requiring attention (pending, problematic, etc.)
     * 
     * @return List of orders requiring attention
     * @throws ServiceException if a system error occurs
     */
    List<CustomerOrder> getOrdersRequiringAttention() throws ServiceException;
    
    /**
     * Get stock alerts (low stock, out of stock)
     * 
     * @return List of products with stock alerts
     * @throws ServiceException if a system error occurs
     */
    List<Product> getStockAlerts() throws ServiceException;
    
    /**
     * Get supplier performance metrics
     * 
     * @param supplierId Supplier ID (null for all suppliers)
     * @return Map of supplier performance metrics
     * @throws ServiceException if a system error occurs
     */
    Map<Supplier, Map<String, Object>> getSupplierPerformanceMetrics(Integer supplierId) 
            throws ServiceException;
    
    /**
     * Get inventory valuation
     * 
     * @return Total value of inventory
     * @throws ServiceException if a system error occurs
     */
    BigDecimal getInventoryValuation() throws ServiceException;
    
    /**
     * Generate sales report for a date range
     * 
     * @param startDate Start date
     * @param endDate End date
     * @param format Report format (PDF, CSV, etc.)
     * @return Report data in the requested format
     * @throws ServiceException if a system error occurs
     */
    byte[] generateSalesReport(LocalDateTime startDate, LocalDateTime endDate, String format) 
            throws ServiceException;
    
    /**
     * Generate inventory report
     * 
     * @param format Report format (PDF, CSV, etc.)
     * @return Report data in the requested format
     * @throws ServiceException if a system error occurs
     */
    byte[] generateInventoryReport(String format) throws ServiceException;
} 
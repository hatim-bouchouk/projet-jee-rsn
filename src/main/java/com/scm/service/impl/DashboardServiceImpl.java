package com.scm.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.scm.dao.CustomerOrderDao;
import com.scm.dao.ProductDao;
import com.scm.dao.StockDao;
import com.scm.dao.SupplierDao;
import com.scm.dao.StockMovementDao;
import com.scm.model.CustomerOrder;
import com.scm.model.Product;
import com.scm.model.Stock;
import com.scm.model.Supplier;
import com.scm.service.DashboardService;
import com.scm.service.ProductService;
import com.scm.service.StockService;
import com.scm.service.exception.ServiceException;

/**
 * Implementation of DashboardService using EJB stateless session bean.
 */
@Stateless
public class DashboardServiceImpl implements DashboardService {
    
    private static final Logger LOGGER = Logger.getLogger(DashboardServiceImpl.class.getName());
    
    @Inject
    private CustomerOrderDao customerOrderDao;
    
    @Inject
    private ProductDao productDao;
    
    @Inject
    private StockDao stockDao;
    
    @Inject
    private SupplierDao supplierDao;
    
    @Inject
    private StockMovementDao stockMovementDao;
    
    @Inject
    private ProductService productService;
    
    @Inject
    private StockService stockService;
    
    @Override
    public Map<String, Object> getSalesStatistics(LocalDateTime startDate, LocalDateTime endDate) 
            throws ServiceException {
        try {
            if (startDate == null) {
                throw new ServiceException("Start date cannot be null");
            }
            
            if (endDate == null) {
                throw new ServiceException("End date cannot be null");
            }
            
            if (startDate.isAfter(endDate)) {
                throw new ServiceException("Start date cannot be after end date");
            }
            
            Map<String, Object> statistics = new HashMap<>();
            
            // Get orders in date range
            List<CustomerOrder> orders = customerOrderDao.findByDateRange(startDate, endDate);
            
            // Calculate statistics
            BigDecimal totalSales = BigDecimal.ZERO;
            int orderCount = orders.size();
            BigDecimal averageOrderValue = BigDecimal.ZERO;
            
            for (CustomerOrder order : orders) {
                if (order.getTotalAmount() != null) {
                    totalSales = totalSales.add(order.getTotalAmount());
                }
            }
            
            if (orderCount > 0) {
                averageOrderValue = totalSales.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP);
            }
            
            // Count orders by status
            Map<String, Long> ordersByStatus = orders.stream()
                    .collect(Collectors.groupingBy(order -> order.getStatus().name(), Collectors.counting()));
            
            // Add statistics to the map
            statistics.put("totalSales", totalSales);
            statistics.put("orderCount", orderCount);
            statistics.put("averageOrderValue", averageOrderValue);
            statistics.put("ordersByStatus", ordersByStatus);
            
            // Calculate daily sales average
            long daysBetween = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
            if (daysBetween > 0) {
                BigDecimal dailyAverage = totalSales.divide(BigDecimal.valueOf(daysBetween), 2, RoundingMode.HALF_UP);
                statistics.put("dailyAverageSales", dailyAverage);
            }
            
            return statistics;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting sales statistics", e);
            throw new ServiceException("Failed to get sales statistics", e);
        }
    }
    
    @Override
    public Map<String, Object> getInventoryStatistics() throws ServiceException {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // Get all products and stocks
            List<Product> allProducts = productDao.findAll();
            
            // Count total products
            statistics.put("totalProducts", allProducts.size());
            
            // Count low stock products
            List<Product> lowStockProducts = productService.findLowStockProducts();
            statistics.put("lowStockCount", lowStockProducts.size());
            
            // Count out of stock products
            List<Product> outOfStockProducts = productService.findOutOfStockProducts();
            statistics.put("outOfStockCount", outOfStockProducts.size());
            
            // Calculate inventory value
            BigDecimal inventoryValue = getInventoryValuation();
            statistics.put("inventoryValue", inventoryValue);
            
            // Count products needing reorder
            List<Stock> stocksNeedingReorder = stockService.getProductsNeedingReorder();
            statistics.put("productsNeedingReorder", stocksNeedingReorder.size());
            
            // Calculate inventory metrics by category (assuming Product has a category field)
            Map<String, Long> productsByCategory = allProducts.stream()
                    .collect(Collectors.groupingBy(
                            product -> product.getProductType() != null ? product.getProductType() : "Uncategorized",
                            Collectors.counting()));
            
            statistics.put("productsByCategory", productsByCategory);
            
            return statistics;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting inventory statistics", e);
            throw new ServiceException("Failed to get inventory statistics", e);
        }
    }
    
    @Override
    public Map<String, Object> getSupplierStatistics() throws ServiceException {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // Get all suppliers
            List<Supplier> allSuppliers = supplierDao.findAll();
            
            // Count total suppliers
            statistics.put("totalSuppliers", allSuppliers.size());
            
            // Count active suppliers (those with at least one product)
            long activeSuppliers = allSuppliers.stream()
                    .filter(supplier -> supplier.getSupplierProducts() != null && !supplier.getSupplierProducts().isEmpty())
                    .count();
            
            statistics.put("activeSuppliers", activeSuppliers);
            
            // Calculate average lead time across all suppliers
            double averageLeadTime = allSuppliers.stream()
                    .filter(supplier -> supplier.getSupplierProducts() != null)
                    .flatMap(supplier -> supplier.getSupplierProducts().stream())
                    .mapToInt(sp -> sp.getLeadTimeDays())
                    .average()
                    .orElse(0);
            
            statistics.put("averageLeadTime", averageLeadTime);
            
            return statistics;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting supplier statistics", e);
            throw new ServiceException("Failed to get supplier statistics", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> getTopSellingProducts(
            LocalDateTime startDate, LocalDateTime endDate, int limit) 
            throws ServiceException {
        try {
            if (startDate == null) {
                throw new ServiceException("Start date cannot be null");
            }
            
            if (endDate == null) {
                throw new ServiceException("End date cannot be null");
            }
            
            if (startDate.isAfter(endDate)) {
                throw new ServiceException("Start date cannot be after end date");
            }
            
            if (limit <= 0) {
                throw new ServiceException("Limit must be greater than zero");
            }
            
            // Since findTopSellingProducts doesn't exist, we'll implement a workaround
            List<Map<String, Object>> result = new ArrayList<>();
            
            // In a real implementation, this would query the database for top selling products
            // For now, we'll return an empty list
            LOGGER.log(Level.WARNING, "findTopSellingProducts method not implemented in CustomerOrderDao");
            
            return result;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting top selling products", e);
            throw new ServiceException("Failed to get top selling products", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> getSalesByTimePeriod(
            LocalDateTime startDate, LocalDateTime endDate, String groupBy) 
            throws ServiceException {
        try {
            if (startDate == null) {
                throw new ServiceException("Start date cannot be null");
            }
            
            if (endDate == null) {
                throw new ServiceException("End date cannot be null");
            }
            
            if (startDate.isAfter(endDate)) {
                throw new ServiceException("Start date cannot be after end date");
            }
            
            if (groupBy == null || groupBy.isEmpty()) {
                throw new ServiceException("Group by parameter cannot be empty");
            }
            
            // Validate groupBy parameter
            if (!groupBy.equals("day") && !groupBy.equals("week") && 
                !groupBy.equals("month") && !groupBy.equals("year")) {
                throw new ServiceException("Group by parameter must be one of: day, week, month, year");
            }
            
            // Since findSalesByTimePeriod doesn't exist, we'll implement a workaround
            List<Map<String, Object>> result = new ArrayList<>();
            
            // In a real implementation, this would query the database for sales by time period
            // For now, we'll return an empty list
            LOGGER.log(Level.WARNING, "findSalesByTimePeriod method not implemented in CustomerOrderDao");
            
            return result;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting sales by time period", e);
            throw new ServiceException("Failed to get sales by time period", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> getRecentActivity(int limit) throws ServiceException {
        try {
            if (limit <= 0) {
                throw new ServiceException("Limit must be greater than zero");
            }
            
            List<Map<String, Object>> activities = new ArrayList<>();
            
            // Get recent orders
            List<CustomerOrder> recentOrders = customerOrderDao.findRecentOrders(limit);
            for (CustomerOrder order : recentOrders) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("type", "ORDER");
                activity.put("timestamp", order.getOrderDate());
                activity.put("data", order);
                activities.add(activity);
            }
            
            // Since findRecentMovements doesn't exist, we'll skip this part
            LOGGER.log(Level.WARNING, "findRecentMovements method not implemented in StockMovementDao");
            
            // Sort activities by timestamp descending and limit
            return activities.stream()
                    .sorted((a1, a2) -> ((LocalDateTime)a2.get("timestamp"))
                            .compareTo((LocalDateTime)a1.get("timestamp")))
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting recent activity", e);
            throw new ServiceException("Failed to get recent activity", e);
        }
    }
    
    @Override
    public Map<String, BigDecimal> getSalesByCategory(LocalDateTime startDate, LocalDateTime endDate) 
            throws ServiceException {
        try {
            if (startDate == null) {
                throw new ServiceException("Start date cannot be null");
            }
            
            if (endDate == null) {
                throw new ServiceException("End date cannot be null");
            }
            
            if (startDate.isAfter(endDate)) {
                throw new ServiceException("Start date cannot be after end date");
            }
            
            // Since findSalesByCategory doesn't exist, we'll implement a workaround
            Map<String, BigDecimal> result = new HashMap<>();
            
            // In a real implementation, this would query the database for sales by category
            // For now, we'll return an empty map
            LOGGER.log(Level.WARNING, "findSalesByCategory method not implemented in CustomerOrderDao");
            
            return result;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting sales by category", e);
            throw new ServiceException("Failed to get sales by category", e);
        }
    }
    
    @Override
    public List<CustomerOrder> getOrdersRequiringAttention() throws ServiceException {
        try {
            List<CustomerOrder> ordersRequiringAttention = new ArrayList<>();
            
            // Use string constants for status since the enum fields are not resolved
            // In a real implementation, this would use the proper enum values
            ordersRequiringAttention.addAll(customerOrderDao.findByStatus(CustomerOrder.Status.valueOf("PENDING")));
            ordersRequiringAttention.addAll(customerOrderDao.findByStatus(CustomerOrder.Status.valueOf("PAID")));
            ordersRequiringAttention.addAll(customerOrderDao.findByStatus(CustomerOrder.Status.valueOf("PROCESSING")));
            
            // Sort by order date ascending (oldest first)
            return ordersRequiringAttention.stream()
                    .sorted((o1, o2) -> o1.getOrderDate().compareTo(o2.getOrderDate()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting orders requiring attention", e);
            throw new ServiceException("Failed to get orders requiring attention", e);
        }
    }
    
    @Override
    public List<Product> getStockAlerts() throws ServiceException {
        try {
            List<Product> alerts = new ArrayList<>();
            
            // Get out of stock products
            alerts.addAll(productService.findOutOfStockProducts());
            
            // Get low stock products
            alerts.addAll(productService.findLowStockProducts());
            
            // Remove duplicates and return
            return alerts.stream().distinct().collect(Collectors.toList());
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting stock alerts", e);
            throw new ServiceException("Failed to get stock alerts", e);
        }
    }
    
    @Override
    public Map<Supplier, Map<String, Object>> getSupplierPerformanceMetrics(Integer supplierId) 
            throws ServiceException {
        try {
            Map<Supplier, Map<String, Object>> result = new HashMap<>();
            
            List<Supplier> suppliers;
            if (supplierId != null) {
                // Get specific supplier
                Optional<Supplier> supplierOpt = supplierDao.findById(supplierId);
                if (!supplierOpt.isPresent()) {
                    throw new ServiceException("Supplier not found with ID: " + supplierId);
                }
                suppliers = new ArrayList<>();
                suppliers.add(supplierOpt.get());
            } else {
                // Get all suppliers
                suppliers = supplierDao.findAll();
            }
            
            for (Supplier supplier : suppliers) {
                Map<String, Object> metrics = new HashMap<>();
                
                if (supplier.getSupplierProducts() != null) {
                    // Calculate average lead time
                    double avgLeadTime = supplier.getSupplierProducts().stream()
                            .mapToInt(sp -> sp.getLeadTimeDays())
                            .average()
                            .orElse(0);
                    
                    metrics.put("averageLeadTime", avgLeadTime);
                    
                    // Calculate number of products supplied
                    int productCount = supplier.getSupplierProducts().size();
                    metrics.put("productCount", productCount);
                } else {
                    metrics.put("averageLeadTime", 0);
                    metrics.put("productCount", 0);
                }
                
                // Add more metrics as needed for supplier performance
                
                result.put(supplier, metrics);
            }
            
            return result;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting supplier performance metrics", e);
            throw new ServiceException("Failed to get supplier performance metrics", e);
        }
    }
    
    @Override
    public BigDecimal getInventoryValuation() throws ServiceException {
        try {
            BigDecimal totalValue = BigDecimal.ZERO;
            
            // Get all products with stock information
            List<Product> products = productDao.findAll();
            
            for (Product product : products) {
                if (product.getStock() != null && product.getUnitPrice() != null) {
                    BigDecimal productValue = product.getUnitPrice()
                            .multiply(BigDecimal.valueOf(product.getStock().getQuantityAvailable()));
                    totalValue = totalValue.add(productValue);
                }
            }
            
            return totalValue;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating inventory valuation", e);
            throw new ServiceException("Failed to calculate inventory valuation", e);
        }
    }
    
    @Override
    public byte[] generateSalesReport(LocalDateTime startDate, LocalDateTime endDate, String format) 
            throws ServiceException {
        try {
            if (startDate == null) {
                throw new ServiceException("Start date cannot be null");
            }
            
            if (endDate == null) {
                throw new ServiceException("End date cannot be null");
            }
            
            if (startDate.isAfter(endDate)) {
                throw new ServiceException("Start date cannot be after end date");
            }
            
            if (format == null || format.isEmpty()) {
                throw new ServiceException("Format cannot be empty");
            }
            
            // In a real implementation, this would generate a report using a reporting library
            // For now, we'll just create a simple representation
            
            StringBuilder report = new StringBuilder();
            
            // Get orders in date range
            List<CustomerOrder> orders = customerOrderDao.findByDateRange(startDate, endDate);
            
            // Get sales statistics
            Map<String, Object> statistics = getSalesStatistics(startDate, endDate);
            
            // Generate report content based on format
            switch (format.toUpperCase()) {
                case "PDF":
                    // In a real implementation, generate PDF
                    report.append("PDF Sales Report from ")
                          .append(startDate)
                          .append(" to ")
                          .append(endDate)
                          .append("\n\n");
                    break;
                case "CSV":
                    // In a real implementation, generate CSV
                    report.append("OrderID,Date,Customer,Total\n");
                    for (CustomerOrder order : orders) {
                        report.append(order.getId())
                              .append(",")
                              .append(order.getOrderDate())
                              .append(",")
                              .append(order.getCustomerName())
                              .append(",")
                              .append(order.getTotalAmount())
                              .append("\n");
                    }
                    break;
                default:
                    throw new ServiceException("Unsupported format: " + format);
            }
            
            // Return the report as bytes
            return report.toString().getBytes();
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating sales report", e);
            throw new ServiceException("Failed to generate sales report", e);
        }
    }
    
    @Override
    public byte[] generateInventoryReport(String format) throws ServiceException {
        try {
            if (format == null || format.isEmpty()) {
                throw new ServiceException("Format cannot be empty");
            }
            
            // In a real implementation, this would generate a report using a reporting library
            // For now, we'll just create a simple representation
            
            StringBuilder report = new StringBuilder();
            
            // Get all products with stock information
            List<Product> products = productDao.findAll();
            
            // Generate report content based on format
            switch (format.toUpperCase()) {
                case "PDF":
                    // In a real implementation, generate PDF
                    report.append("PDF Inventory Report as of ")
                          .append(LocalDateTime.now())
                          .append("\n\n");
                    break;
                case "CSV":
                    // In a real implementation, generate CSV
                    report.append("ProductID,SKU,Name,InStock,ReorderLevel,UnitPrice,Value\n");
                    for (Product product : products) {
                        int quantity = (product.getStock() != null) ? product.getStock().getQuantityAvailable() : 0;
                        BigDecimal value = (product.getUnitPrice() != null) 
                                ? product.getUnitPrice().multiply(BigDecimal.valueOf(quantity))
                                : BigDecimal.ZERO;
                        
                        report.append(product.getId())
                              .append(",")
                              .append(product.getSku())
                              .append(",")
                              .append(product.getName())
                              .append(",")
                              .append(quantity)
                              .append(",")
                              .append(product.getReorderLevel())
                              .append(",")
                              .append(product.getUnitPrice())
                              .append(",")
                              .append(value)
                              .append("\n");
                    }
                    break;
                default:
                    throw new ServiceException("Unsupported format: " + format);
            }
            
            // Return the report as bytes
            return report.toString().getBytes();
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating inventory report", e);
            throw new ServiceException("Failed to generate inventory report", e);
        }
    }
} 
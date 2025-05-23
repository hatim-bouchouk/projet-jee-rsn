package com.scm.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.scm.dao.ProductDao;
import com.scm.dao.StockDao;
import com.scm.model.Product;
import com.scm.model.Stock;
import com.scm.service.ProductService;
import com.scm.service.exception.ServiceException;
import com.scm.service.exception.ValidationException;

/**
 * Implementation of ProductService using EJB stateless session bean.
 */
@Stateless
public class ProductServiceImpl implements ProductService {
    
    private static final Logger LOGGER = Logger.getLogger(ProductServiceImpl.class.getName());
    
    @Inject
    private ProductDao productDao;
    
    @Inject
    private StockDao stockDao;
    
    @Override
    @Transactional
    public Product createProduct(Product product) throws ValidationException, ServiceException {
        try {
            // Validate product data
            validateProduct(product, true);
            
            // Set creation timestamp
            product.setCreatedAt(LocalDateTime.now());
            
            // Save the product
            Product savedProduct = productDao.save(product);
            
            // Create stock record if not already present
            if (savedProduct.getStock() == null) {
                Stock stock = new Stock();
                stock.setProduct(savedProduct);
                stock.setQuantityAvailable(0);
                stock.setLastUpdated(LocalDateTime.now());
                stockDao.save(stock);
            }
            
            return savedProduct;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating product", e);
            throw new ServiceException("Failed to create product", e);
        }
    }
    
    @Override
    @Transactional
    public Product updateProduct(Product product) throws ValidationException, ServiceException {
        try {
            // Check if product exists
            Optional<Product> existingProductOpt = productDao.findById(product.getId());
            if (!existingProductOpt.isPresent()) {
                throw new ServiceException("Product not found with ID: " + product.getId());
            }
            
            Product existingProduct = existingProductOpt.get();
            
            // Validate product data
            validateProductForUpdate(product, existingProduct);
            
            // Preserve creation timestamp
            product.setCreatedAt(existingProduct.getCreatedAt());
            
            // Update the product
            return productDao.update(product);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating product", e);
            throw new ServiceException("Failed to update product", e);
        }
    }
    
    @Override
    @Transactional
    public boolean deleteProduct(Integer productId) throws ServiceException {
        try {
            if (productId == null) {
                throw new ServiceException("Product ID cannot be null");
            }
            
            return productDao.deleteById(productId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting product", e);
            throw new ServiceException("Failed to delete product", e);
        }
    }
    
    @Override
    public Optional<Product> findById(Integer productId) throws ServiceException {
        try {
            if (productId == null) {
                throw new ServiceException("Product ID cannot be null");
            }
            
            return productDao.findById(productId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding product by ID", e);
            throw new ServiceException("Failed to find product by ID", e);
        }
    }
    
    @Override
    public Optional<Product> findBySku(String sku) throws ServiceException {
        try {
            if (sku == null || sku.isEmpty()) {
                throw new ServiceException("SKU cannot be empty");
            }
            
            return productDao.findBySku(sku);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding product by SKU", e);
            throw new ServiceException("Failed to find product by SKU", e);
        }
    }
    
    @Override
    public List<Product> findAllProducts() throws ServiceException {
        try {
            return productDao.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding all products", e);
            throw new ServiceException("Failed to find all products", e);
        }
    }
    
    @Override
    public List<Product> findByNameContaining(String name) throws ServiceException {
        try {
            if (name == null || name.isEmpty()) {
                throw new ServiceException("Name cannot be empty");
            }
            
            return productDao.findByNameContaining(name);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding products by name", e);
            throw new ServiceException("Failed to find products by name", e);
        }
    }
    
    @Override
    public List<Product> findLowStockProducts() throws ServiceException {
        try {
            return productDao.findLowStockProducts();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding low stock products", e);
            throw new ServiceException("Failed to find low stock products", e);
        }
    }
    
    @Override
    public List<Product> findOutOfStockProducts() throws ServiceException {
        try {
            return productDao.findOutOfStockProducts();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding out of stock products", e);
            throw new ServiceException("Failed to find out of stock products", e);
        }
    }
    
    @Override
    public List<Product> findBySupplier(Integer supplierId) throws ServiceException {
        try {
            if (supplierId == null) {
                throw new ServiceException("Supplier ID cannot be null");
            }
            
            return productDao.findBySupplier(supplierId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding products by supplier", e);
            throw new ServiceException("Failed to find products by supplier", e);
        }
    }
    
    @Override
    @Transactional
    public Optional<Product> updatePrice(Integer productId, BigDecimal newPrice) 
            throws ValidationException, ServiceException {
        try {
            // Validate inputs
            if (productId == null) {
                throw new ValidationException("Product ID cannot be null");
            }
            
            if (newPrice == null) {
                throw new ValidationException("Price cannot be null");
            }
            
            if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("Price must be greater than zero");
            }
            
            // Find the product
            Optional<Product> productOpt = productDao.findById(productId);
            if (!productOpt.isPresent()) {
                return Optional.empty();
            }
            
            Product product = productOpt.get();
            product.setUnitPrice(newPrice);
            
            // Update the product
            return Optional.of(productDao.update(product));
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating product price", e);
            throw new ServiceException("Failed to update product price", e);
        }
    }
    
    @Override
    @Transactional
    public Optional<Product> updateReorderLevel(Integer productId, Integer reorderLevel) 
            throws ValidationException, ServiceException {
        try {
            // Validate inputs
            if (productId == null) {
                throw new ValidationException("Product ID cannot be null");
            }
            
            if (reorderLevel == null) {
                throw new ValidationException("Reorder level cannot be null");
            }
            
            if (reorderLevel < 0) {
                throw new ValidationException("Reorder level cannot be negative");
            }
            
            // Find the product
            Optional<Product> productOpt = productDao.findById(productId);
            if (!productOpt.isPresent()) {
                return Optional.empty();
            }
            
            Product product = productOpt.get();
            product.setReorderLevel(reorderLevel);
            
            // Update the product
            return Optional.of(productDao.update(product));
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating product reorder level", e);
            throw new ServiceException("Failed to update product reorder level", e);
        }
    }
    
    /**
     * Validate product data for creation
     */
    private void validateProduct(Product product, boolean isNewProduct) throws ValidationException {
        Map<String, String> errors = new HashMap<>();
        
        if (product == null) {
            throw new ValidationException("Product cannot be null");
        }
        
        if (product.getName() == null || product.getName().isEmpty()) {
            errors.put("name", "Name cannot be empty");
        } else if (product.getName().length() > 100) {
            errors.put("name", "Name must be less than 100 characters");
        }
        
        if (product.getSku() == null || product.getSku().isEmpty()) {
            errors.put("sku", "SKU cannot be empty");
        } else if (product.getSku().length() > 50) {
            errors.put("sku", "SKU must be less than 50 characters");
        } else if (isNewProduct && productDao.skuExists(product.getSku())) {
            errors.put("sku", "SKU already exists");
        }
        
        if (product.getUnitPrice() == null) {
            errors.put("unitPrice", "Unit price cannot be null");
        } else if (product.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("unitPrice", "Unit price must be greater than zero");
        }
        
        if (product.getReorderLevel() != null && product.getReorderLevel() < 0) {
            errors.put("reorderLevel", "Reorder level cannot be negative");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Product validation failed", errors);
        }
    }
    
    /**
     * Validate product data for update
     */
    private void validateProductForUpdate(Product product, Product existingProduct) throws ValidationException {
        Map<String, String> errors = new HashMap<>();
        
        if (product == null) {
            throw new ValidationException("Product cannot be null");
        }
        
        if (product.getName() == null || product.getName().isEmpty()) {
            errors.put("name", "Name cannot be empty");
        } else if (product.getName().length() > 100) {
            errors.put("name", "Name must be less than 100 characters");
        }
        
        if (product.getSku() == null || product.getSku().isEmpty()) {
            errors.put("sku", "SKU cannot be empty");
        } else if (product.getSku().length() > 50) {
            errors.put("sku", "SKU must be less than 50 characters");
        } else if (!product.getSku().equals(existingProduct.getSku()) && productDao.skuExists(product.getSku())) {
            errors.put("sku", "SKU already exists");
        }
        
        if (product.getUnitPrice() == null) {
            errors.put("unitPrice", "Unit price cannot be null");
        } else if (product.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("unitPrice", "Unit price must be greater than zero");
        }
        
        if (product.getReorderLevel() != null && product.getReorderLevel() < 0) {
            errors.put("reorderLevel", "Reorder level cannot be negative");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Product validation failed", errors);
        }
    }
} 
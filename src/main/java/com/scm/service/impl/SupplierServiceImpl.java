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
import com.scm.dao.SupplierDao;
import com.scm.dao.SupplierProductDao;
import com.scm.model.Product;
import com.scm.model.Supplier;
import com.scm.model.SupplierProduct;
import com.scm.model.SupplierProductId;
import com.scm.service.SupplierService;
import com.scm.service.exception.ServiceException;
import com.scm.service.exception.ValidationException;

/**
 * Implementation of SupplierService using EJB stateless session bean.
 */
@Stateless
public class SupplierServiceImpl implements SupplierService {
    
    private static final Logger LOGGER = Logger.getLogger(SupplierServiceImpl.class.getName());
    
    @Inject
    private SupplierDao supplierDao;
    
    @Inject
    private ProductDao productDao;
    
    @Inject
    private SupplierProductDao supplierProductDao;
    
    @Override
    @Transactional
    public Supplier createSupplier(Supplier supplier) throws ValidationException, ServiceException {
        try {
            // Validate supplier data
            validateSupplier(supplier, true);
            
            // Set creation timestamp
            supplier.setCreatedAt(LocalDateTime.now());
            
            // Save the supplier
            return supplierDao.save(supplier);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating supplier", e);
            throw new ServiceException("Failed to create supplier", e);
        }
    }
    
    @Override
    @Transactional
    public Supplier updateSupplier(Supplier supplier) throws ValidationException, ServiceException {
        try {
            // Check if supplier exists
            Optional<Supplier> existingSupplierOpt = supplierDao.findById(supplier.getId());
            if (!existingSupplierOpt.isPresent()) {
                throw new ServiceException("Supplier not found with ID: " + supplier.getId());
            }
            
            Supplier existingSupplier = existingSupplierOpt.get();
            
            // Validate supplier data
            validateSupplierForUpdate(supplier, existingSupplier);
            
            // Preserve creation timestamp
            supplier.setCreatedAt(existingSupplier.getCreatedAt());
            
            // Update the supplier
            return supplierDao.update(supplier);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating supplier", e);
            throw new ServiceException("Failed to update supplier", e);
        }
    }
    
    @Override
    @Transactional
    public boolean deleteSupplier(Integer supplierId) throws ServiceException {
        try {
            if (supplierId == null) {
                throw new ServiceException("Supplier ID cannot be null");
            }
            
            return supplierDao.deleteById(supplierId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting supplier", e);
            throw new ServiceException("Failed to delete supplier", e);
        }
    }
    
    @Override
    public Optional<Supplier> findById(Integer supplierId) throws ServiceException {
        try {
            if (supplierId == null) {
                throw new ServiceException("Supplier ID cannot be null");
            }
            
            return supplierDao.findById(supplierId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding supplier by ID", e);
            throw new ServiceException("Failed to find supplier by ID", e);
        }
    }
    
    @Override
    public Optional<Supplier> findByEmail(String email) throws ServiceException {
        try {
            if (email == null || email.isEmpty()) {
                throw new ServiceException("Email cannot be empty");
            }
            
            return supplierDao.findByEmail(email);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding supplier by email", e);
            throw new ServiceException("Failed to find supplier by email", e);
        }
    }
    
    @Override
    public List<Supplier> findAllSuppliers() throws ServiceException {
        try {
            return supplierDao.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding all suppliers", e);
            throw new ServiceException("Failed to find all suppliers", e);
        }
    }
    
    @Override
    public List<Supplier> findByNameContaining(String name) throws ServiceException {
        try {
            if (name == null || name.isEmpty()) {
                throw new ServiceException("Name cannot be empty");
            }
            
            return supplierDao.findByNameContaining(name);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding suppliers by name", e);
            throw new ServiceException("Failed to find suppliers by name", e);
        }
    }
    
    @Override
    public List<Supplier> findByProduct(Integer productId) throws ServiceException {
        try {
            if (productId == null) {
                throw new ServiceException("Product ID cannot be null");
            }
            
            return supplierDao.findByProduct(productId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding suppliers by product", e);
            throw new ServiceException("Failed to find suppliers by product", e);
        }
    }
    
    @Override
    @Transactional
    public SupplierProduct addProductToSupplier(Integer supplierId, Integer productId, 
            BigDecimal unitCost, Integer leadTimeDays) throws ValidationException, ServiceException {
        try {
            // Validate inputs
            if (supplierId == null) {
                throw new ValidationException("Supplier ID cannot be null");
            }
            
            if (productId == null) {
                throw new ValidationException("Product ID cannot be null");
            }
            
            if (unitCost == null) {
                throw new ValidationException("Unit cost cannot be null");
            }
            
            if (unitCost.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("Unit cost must be greater than zero");
            }
            
            if (leadTimeDays == null) {
                throw new ValidationException("Lead time days cannot be null");
            }
            
            if (leadTimeDays < 0) {
                throw new ValidationException("Lead time days cannot be negative");
            }
            
            // Check if supplier exists
            Optional<Supplier> supplierOpt = supplierDao.findById(supplierId);
            if (!supplierOpt.isPresent()) {
                throw new ServiceException("Supplier not found with ID: " + supplierId);
            }
            
            // Check if product exists
            Optional<Product> productOpt = productDao.findById(productId);
            if (!productOpt.isPresent()) {
                throw new ServiceException("Product not found with ID: " + productId);
            }
            
            // Create composite key
            SupplierProductId id = new SupplierProductId();
            id.setSupplierId(supplierId);
            id.setProductId(productId);
            
            // Check if association already exists
            Optional<SupplierProduct> existingAssociationOpt = supplierProductDao.findById(id);
            if (existingAssociationOpt.isPresent()) {
                throw new ValidationException("This supplier already provides this product");
            }
            
            // Create new association
            SupplierProduct supplierProduct = new SupplierProduct();
            supplierProduct.setId(id);
            supplierProduct.setSupplier(supplierOpt.get());
            supplierProduct.setProduct(productOpt.get());
            supplierProduct.setUnitCost(unitCost);
            supplierProduct.setLeadTimeDays(leadTimeDays);
            
            // Save the association
            return supplierProductDao.save(supplierProduct);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding product to supplier", e);
            throw new ServiceException("Failed to add product to supplier", e);
        }
    }
    
    @Override
    @Transactional
    public SupplierProduct updateSupplierProduct(SupplierProduct supplierProduct) 
            throws ValidationException, ServiceException {
        try {
            // Validate supplier product
            validateSupplierProduct(supplierProduct);
            
            // Check if association exists
            if (!supplierProductDao.exists(supplierProduct.getId())) {
                throw new ServiceException("Supplier-product association not found");
            }
            
            // Update the association
            return supplierProductDao.update(supplierProduct);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating supplier product", e);
            throw new ServiceException("Failed to update supplier product", e);
        }
    }
    
    @Override
    @Transactional
    public boolean removeProductFromSupplier(Integer supplierId, Integer productId) 
            throws ServiceException {
        try {
            if (supplierId == null) {
                throw new ServiceException("Supplier ID cannot be null");
            }
            
            if (productId == null) {
                throw new ServiceException("Product ID cannot be null");
            }
            
            // Create composite key
            SupplierProductId id = new SupplierProductId();
            id.setSupplierId(supplierId);
            id.setProductId(productId);
            
            // Delete the association
            return supplierProductDao.deleteById(id);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error removing product from supplier", e);
            throw new ServiceException("Failed to remove product from supplier", e);
        }
    }
    
    @Override
    public Optional<SupplierProduct> findLowestCostSupplierForProduct(Integer productId) 
            throws ServiceException {
        try {
            if (productId == null) {
                throw new ServiceException("Product ID cannot be null");
            }
            
            return supplierProductDao.findLowestCostSupplierForProduct(productId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding lowest cost supplier for product", e);
            throw new ServiceException("Failed to find lowest cost supplier for product", e);
        }
    }
    
    @Override
    public Optional<SupplierProduct> findFastestDeliverySupplierForProduct(Integer productId) 
            throws ServiceException {
        try {
            if (productId == null) {
                throw new ServiceException("Product ID cannot be null");
            }
            
            return supplierProductDao.findFastestDeliverySupplierForProduct(productId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding fastest delivery supplier for product", e);
            throw new ServiceException("Failed to find fastest delivery supplier for product", e);
        }
    }
    
    /**
     * Validate supplier data for creation
     */
    private void validateSupplier(Supplier supplier, boolean isNewSupplier) throws ValidationException {
        Map<String, String> errors = new HashMap<>();
        
        if (supplier == null) {
            throw new ValidationException("Supplier cannot be null");
        }
        
        if (supplier.getName() == null || supplier.getName().isEmpty()) {
            errors.put("name", "Name cannot be empty");
        } else if (supplier.getName().length() > 100) {
            errors.put("name", "Name must be less than 100 characters");
        }
        
        if (supplier.getEmail() == null || supplier.getEmail().isEmpty()) {
            errors.put("email", "Email cannot be empty");
        } else if (!supplier.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.put("email", "Email is not valid");
        } else if (isNewSupplier && supplierDao.emailExists(supplier.getEmail())) {
            errors.put("email", "Email already exists");
        }
        
        if (supplier.getPhone() == null || supplier.getPhone().isEmpty()) {
            errors.put("phone", "Phone cannot be empty");
        }
        
        if (supplier.getAddress() == null || supplier.getAddress().isEmpty()) {
            errors.put("address", "Address cannot be empty");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Supplier validation failed", errors);
        }
    }
    
    /**
     * Validate supplier data for update
     */
    private void validateSupplierForUpdate(Supplier supplier, Supplier existingSupplier) 
            throws ValidationException {
        Map<String, String> errors = new HashMap<>();
        
        if (supplier == null) {
            throw new ValidationException("Supplier cannot be null");
        }
        
        if (supplier.getName() == null || supplier.getName().isEmpty()) {
            errors.put("name", "Name cannot be empty");
        } else if (supplier.getName().length() > 100) {
            errors.put("name", "Name must be less than 100 characters");
        }
        
        if (supplier.getEmail() == null || supplier.getEmail().isEmpty()) {
            errors.put("email", "Email cannot be empty");
        } else if (!supplier.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.put("email", "Email is not valid");
        } else if (!supplier.getEmail().equals(existingSupplier.getEmail()) 
                && supplierDao.emailExists(supplier.getEmail())) {
            errors.put("email", "Email already exists");
        }
        
        if (supplier.getPhone() == null || supplier.getPhone().isEmpty()) {
            errors.put("phone", "Phone cannot be empty");
        }
        
        if (supplier.getAddress() == null || supplier.getAddress().isEmpty()) {
            errors.put("address", "Address cannot be empty");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Supplier validation failed", errors);
        }
    }
    
    /**
     * Validate supplier product data
     */
    private void validateSupplierProduct(SupplierProduct supplierProduct) throws ValidationException {
        Map<String, String> errors = new HashMap<>();
        
        if (supplierProduct == null) {
            throw new ValidationException("Supplier product cannot be null");
        }
        
        if (supplierProduct.getId() == null) {
            errors.put("id", "Supplier product ID cannot be null");
        }
        
        if (supplierProduct.getUnitCost() == null) {
            errors.put("unitCost", "Unit cost cannot be null");
        } else if (supplierProduct.getUnitCost().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("unitCost", "Unit cost must be greater than zero");
        }
        
        if (supplierProduct.getLeadTimeDays() == null) {
            errors.put("leadTimeDays", "Lead time days cannot be null");
        } else if (supplierProduct.getLeadTimeDays() < 0) {
            errors.put("leadTimeDays", "Lead time days cannot be negative");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Supplier product validation failed", errors);
        }
    }
} 
package com.scm.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.scm.model.Supplier;
import com.scm.model.SupplierProduct;
import com.scm.security.model.UserPrincipal;
import com.scm.security.util.SessionManager;
import com.scm.service.ProductService;
import com.scm.service.SupplierService;
import com.scm.service.exception.ServiceException;
import com.scm.service.exception.ValidationException;

/**
 * Servlet for handling supplier management operations.
 */
@WebServlet(name = "SupplierServlet", urlPatterns = {"/suppliers", "/suppliers/*"})
public class SupplierServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(SupplierServlet.class.getName());
    
    @Inject
    private SupplierService supplierService;
    
    @Inject
    private ProductService productService;
    
    /**
     * Handles GET requests for supplier operations.
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @throws ServletException If an error occurs during request handling
     * @throws IOException If an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get current user from session
        UserPrincipal currentUser = SessionManager.getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Check permissions
        if (!currentUser.hasPermission("supplier:view")) {
            response.sendRedirect(request.getContextPath() + "/access-denied");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // List all suppliers
                String searchTerm = request.getParameter("search");
                List<Supplier> suppliers;
                
                if (searchTerm != null && !searchTerm.isEmpty()) {
                    suppliers = supplierService.findByNameContaining(searchTerm);
                    request.setAttribute("searchTerm", searchTerm);
                } else {
                    suppliers = supplierService.findAllSuppliers();
                }
                
                request.setAttribute("suppliers", suppliers);
                request.getRequestDispatcher("/WEB-INF/jsp/supplier/list.jsp").forward(request, response);
                
            } else if (pathInfo.startsWith("/view/")) {
                // View a specific supplier
                String idStr = pathInfo.substring("/view/".length());
                Integer supplierId = Integer.parseInt(idStr);
                
                Optional<Supplier> supplierOpt = supplierService.findById(supplierId);
                
                if (supplierOpt.isPresent()) {
                    request.setAttribute("supplier", supplierOpt.get());
                    request.getRequestDispatcher("/WEB-INF/jsp/supplier/view.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Supplier not found");
                    response.sendRedirect(request.getContextPath() + "/suppliers");
                }
                
            } else if (pathInfo.equals("/create")) {
                // Show create supplier form
                if (!currentUser.hasPermission("supplier:create")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                request.getRequestDispatcher("/WEB-INF/jsp/supplier/form.jsp").forward(request, response);
                
            } else if (pathInfo.startsWith("/edit/")) {
                // Show edit supplier form
                if (!currentUser.hasPermission("supplier:edit")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/edit/".length());
                Integer supplierId = Integer.parseInt(idStr);
                
                Optional<Supplier> supplierOpt = supplierService.findById(supplierId);
                
                if (supplierOpt.isPresent()) {
                    request.setAttribute("supplier", supplierOpt.get());
                    request.getRequestDispatcher("/WEB-INF/jsp/supplier/form.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Supplier not found");
                    response.sendRedirect(request.getContextPath() + "/suppliers");
                }
                
            } else if (pathInfo.startsWith("/products/")) {
                // Show products for a specific supplier
                String idStr = pathInfo.substring("/products/".length());
                Integer supplierId = Integer.parseInt(idStr);
                
                Optional<Supplier> supplierOpt = supplierService.findById(supplierId);
                
                if (supplierOpt.isPresent()) {
                    Supplier supplier = supplierOpt.get();
                    request.setAttribute("supplier", supplier);
                    
                    // Also get all products for the add product form
                    if (currentUser.hasPermission("supplier:edit")) {
                        request.setAttribute("allProducts", productService.findAllProducts());
                    }
                    
                    request.getRequestDispatcher("/WEB-INF/jsp/supplier/products.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Supplier not found");
                    response.sendRedirect(request.getContextPath() + "/suppliers");
                }
                
            } else {
                // Invalid path
                response.sendRedirect(request.getContextPath() + "/suppliers");
            }
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid supplier ID format", e);
            request.setAttribute("error", "Invalid supplier ID format");
            response.sendRedirect(request.getContextPath() + "/suppliers");
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving supplier data", e);
            request.setAttribute("error", "An error occurred while retrieving supplier data: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/supplier/list.jsp").forward(request, response);
        }
    }
    
    /**
     * Handles POST requests for supplier operations.
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @throws ServletException If an error occurs during request handling
     * @throws IOException If an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get current user from session
        UserPrincipal currentUser = SessionManager.getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/create")) {
                // Create a new supplier
                if (!currentUser.hasPermission("supplier:create")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                // Extract form data
                Supplier supplier = extractSupplierFromRequest(request);
                
                // Create the supplier
                supplier = supplierService.createSupplier(supplier);
                
                // Redirect to view the new supplier
                request.setAttribute("message", "Supplier created successfully");
                response.sendRedirect(request.getContextPath() + "/suppliers/view/" + supplier.getId());
                
            } else if (pathInfo.startsWith("/edit/")) {
                // Update an existing supplier
                if (!currentUser.hasPermission("supplier:edit")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/edit/".length());
                Integer supplierId = Integer.parseInt(idStr);
                
                // Extract form data
                Supplier supplier = extractSupplierFromRequest(request);
                supplier.setId(supplierId);
                
                // Update the supplier
                supplier = supplierService.updateSupplier(supplier);
                
                // Redirect to view the updated supplier
                request.setAttribute("message", "Supplier updated successfully");
                response.sendRedirect(request.getContextPath() + "/suppliers/view/" + supplier.getId());
                
            } else if (pathInfo.startsWith("/delete/")) {
                // Delete a supplier
                if (!currentUser.hasPermission("supplier:delete")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/delete/".length());
                Integer supplierId = Integer.parseInt(idStr);
                
                boolean deleted = supplierService.deleteSupplier(supplierId);
                
                if (deleted) {
                    request.setAttribute("message", "Supplier deleted successfully");
                } else {
                    request.setAttribute("error", "Supplier not found or could not be deleted");
                }
                
                response.sendRedirect(request.getContextPath() + "/suppliers");
                
            } else if (pathInfo.startsWith("/add-product/")) {
                // Add product to supplier
                if (!currentUser.hasPermission("supplier:edit")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/add-product/".length());
                Integer supplierId = Integer.parseInt(idStr);
                
                String productIdStr = request.getParameter("productId");
                String unitCostStr = request.getParameter("unitCost");
                String leadTimeDaysStr = request.getParameter("leadTimeDays");
                
                if (productIdStr == null || unitCostStr == null || leadTimeDaysStr == null) {
                    request.setAttribute("error", "Missing required fields");
                    response.sendRedirect(request.getContextPath() + "/suppliers/products/" + supplierId);
                    return;
                }
                
                Integer productId = Integer.parseInt(productIdStr);
                BigDecimal unitCost = new BigDecimal(unitCostStr);
                Integer leadTimeDays = Integer.parseInt(leadTimeDaysStr);
                
                SupplierProduct supplierProduct = supplierService.addProductToSupplier(
                        supplierId, productId, unitCost, leadTimeDays);
                
                request.setAttribute("message", "Product added to supplier successfully");
                response.sendRedirect(request.getContextPath() + "/suppliers/products/" + supplierId);
                
            } else if (pathInfo.startsWith("/remove-product/")) {
                // Remove product from supplier
                if (!currentUser.hasPermission("supplier:edit")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/remove-product/".length());
                Integer supplierId = Integer.parseInt(idStr);
                
                String productIdStr = request.getParameter("productId");
                
                if (productIdStr == null) {
                    request.setAttribute("error", "Missing product ID");
                    response.sendRedirect(request.getContextPath() + "/suppliers/products/" + supplierId);
                    return;
                }
                
                Integer productId = Integer.parseInt(productIdStr);
                
                boolean removed = supplierService.removeProductFromSupplier(supplierId, productId);
                
                if (removed) {
                    request.setAttribute("message", "Product removed from supplier successfully");
                } else {
                    request.setAttribute("error", "Product not found or could not be removed");
                }
                
                response.sendRedirect(request.getContextPath() + "/suppliers/products/" + supplierId);
                
            } else {
                // Invalid path
                response.sendRedirect(request.getContextPath() + "/suppliers");
            }
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid numeric format in supplier form", e);
            request.setAttribute("error", "Invalid numeric format: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/supplier/form.jsp").forward(request, response);
        } catch (ValidationException e) {
            LOGGER.log(Level.WARNING, "Supplier validation error", e);
            request.setAttribute("error", "Validation error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/supplier/form.jsp").forward(request, response);
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Error processing supplier operation", e);
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/supplier/form.jsp").forward(request, response);
        }
    }
    
    /**
     * Extract supplier data from request parameters
     * 
     * @param request The HTTP request
     * @return Supplier object populated with form data
     */
    private Supplier extractSupplierFromRequest(HttpServletRequest request) {
        Supplier supplier = new Supplier();
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String contactPerson = request.getParameter("contactPerson");
        
        supplier.setName(name);
        supplier.setEmail(email);
        supplier.setPhone(phone);
        supplier.setAddress(address);
        supplier.setContactPerson(contactPerson);
        
        return supplier;
    }
} 
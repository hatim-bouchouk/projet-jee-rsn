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

import com.scm.model.Product;
import com.scm.security.model.UserPrincipal;
import com.scm.security.util.SessionManager;
import com.scm.service.ProductService;
import com.scm.service.exception.ServiceException;
import com.scm.service.exception.ValidationException;

/**
 * Servlet for handling product management operations.
 */
@WebServlet(name = "ProductServlet", urlPatterns = {"/products", "/products/*"})
public class ProductServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ProductServlet.class.getName());
    
    @Inject
    private ProductService productService;
    
    /**
     * Handles GET requests for product operations.
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
        if (!currentUser.hasPermission("product:view")) {
            response.sendRedirect(request.getContextPath() + "/access-denied");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // List all products
                String searchTerm = request.getParameter("search");
                List<Product> products;
                
                if (searchTerm != null && !searchTerm.isEmpty()) {
                    products = productService.findByNameContaining(searchTerm);
                    request.setAttribute("searchTerm", searchTerm);
                } else {
                    products = productService.findAllProducts();
                }
                
                request.setAttribute("products", products);
                request.getRequestDispatcher("/WEB-INF/jsp/product/list.jsp").forward(request, response);
                
            } else if (pathInfo.startsWith("/view/")) {
                // View a specific product
                String idStr = pathInfo.substring("/view/".length());
                Integer productId = Integer.parseInt(idStr);
                
                Optional<Product> productOpt = productService.findById(productId);
                
                if (productOpt.isPresent()) {
                    request.setAttribute("product", productOpt.get());
                    request.getRequestDispatcher("/WEB-INF/jsp/product/view.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Product not found");
                    response.sendRedirect(request.getContextPath() + "/products");
                }
                
            } else if (pathInfo.equals("/create")) {
                // Show create product form
                if (!currentUser.hasPermission("product:create")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                request.getRequestDispatcher("/WEB-INF/jsp/product/form.jsp").forward(request, response);
                
            } else if (pathInfo.startsWith("/edit/")) {
                // Show edit product form
                if (!currentUser.hasPermission("product:edit")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/edit/".length());
                Integer productId = Integer.parseInt(idStr);
                
                Optional<Product> productOpt = productService.findById(productId);
                
                if (productOpt.isPresent()) {
                    request.setAttribute("product", productOpt.get());
                    request.getRequestDispatcher("/WEB-INF/jsp/product/form.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Product not found");
                    response.sendRedirect(request.getContextPath() + "/products");
                }
                
            } else if (pathInfo.equals("/low-stock")) {
                // List low stock products
                List<Product> products = productService.findLowStockProducts();
                request.setAttribute("products", products);
                request.setAttribute("title", "Low Stock Products");
                request.getRequestDispatcher("/WEB-INF/jsp/product/list.jsp").forward(request, response);
                
            } else if (pathInfo.equals("/out-of-stock")) {
                // List out of stock products
                List<Product> products = productService.findOutOfStockProducts();
                request.setAttribute("products", products);
                request.setAttribute("title", "Out of Stock Products");
                request.getRequestDispatcher("/WEB-INF/jsp/product/list.jsp").forward(request, response);
                
            } else {
                // Invalid path
                response.sendRedirect(request.getContextPath() + "/products");
            }
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid product ID format", e);
            request.setAttribute("error", "Invalid product ID format");
            response.sendRedirect(request.getContextPath() + "/products");
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving product data", e);
            request.setAttribute("error", "An error occurred while retrieving product data: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/product/list.jsp").forward(request, response);
        }
    }
    
    /**
     * Handles POST requests for product operations.
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
                // Create a new product
                if (!currentUser.hasPermission("product:create")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                // Extract form data
                Product product = extractProductFromRequest(request);
                
                // Create the product
                product = productService.createProduct(product);
                
                // Redirect to view the new product
                request.setAttribute("message", "Product created successfully");
                response.sendRedirect(request.getContextPath() + "/products/view/" + product.getId());
                
            } else if (pathInfo.startsWith("/edit/")) {
                // Update an existing product
                if (!currentUser.hasPermission("product:edit")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/edit/".length());
                Integer productId = Integer.parseInt(idStr);
                
                // Extract form data
                Product product = extractProductFromRequest(request);
                product.setId(productId);
                
                // Update the product
                product = productService.updateProduct(product);
                
                // Redirect to view the updated product
                request.setAttribute("message", "Product updated successfully");
                response.sendRedirect(request.getContextPath() + "/products/view/" + product.getId());
                
            } else if (pathInfo.startsWith("/delete/")) {
                // Delete a product
                if (!currentUser.hasPermission("product:delete")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/delete/".length());
                Integer productId = Integer.parseInt(idStr);
                
                boolean deleted = productService.deleteProduct(productId);
                
                if (deleted) {
                    request.setAttribute("message", "Product deleted successfully");
                } else {
                    request.setAttribute("error", "Product not found or could not be deleted");
                }
                
                response.sendRedirect(request.getContextPath() + "/products");
                
            } else if (pathInfo.startsWith("/update-price/")) {
                // Update product price
                if (!currentUser.hasPermission("product:edit")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/update-price/".length());
                Integer productId = Integer.parseInt(idStr);
                
                String priceStr = request.getParameter("price");
                BigDecimal newPrice = new BigDecimal(priceStr);
                
                Optional<Product> updatedProduct = productService.updatePrice(productId, newPrice);
                
                if (updatedProduct.isPresent()) {
                    request.setAttribute("message", "Product price updated successfully");
                    response.sendRedirect(request.getContextPath() + "/products/view/" + productId);
                } else {
                    request.setAttribute("error", "Product not found or price could not be updated");
                    response.sendRedirect(request.getContextPath() + "/products");
                }
                
            } else if (pathInfo.startsWith("/update-reorder-level/")) {
                // Update product reorder level
                if (!currentUser.hasPermission("product:edit")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/update-reorder-level/".length());
                Integer productId = Integer.parseInt(idStr);
                
                String levelStr = request.getParameter("reorderLevel");
                Integer newLevel = Integer.parseInt(levelStr);
                
                Optional<Product> updatedProduct = productService.updateReorderLevel(productId, newLevel);
                
                if (updatedProduct.isPresent()) {
                    request.setAttribute("message", "Product reorder level updated successfully");
                    response.sendRedirect(request.getContextPath() + "/products/view/" + productId);
                } else {
                    request.setAttribute("error", "Product not found or reorder level could not be updated");
                    response.sendRedirect(request.getContextPath() + "/products");
                }
                
            } else {
                // Invalid path
                response.sendRedirect(request.getContextPath() + "/products");
            }
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid numeric format in product form", e);
            request.setAttribute("error", "Invalid numeric format: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/product/form.jsp").forward(request, response);
        } catch (ValidationException e) {
            LOGGER.log(Level.WARNING, "Product validation error", e);
            request.setAttribute("error", "Validation error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/product/form.jsp").forward(request, response);
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Error processing product operation", e);
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/product/form.jsp").forward(request, response);
        }
    }
    
    /**
     * Extract product data from request parameters
     * 
     * @param request The HTTP request
     * @return Product object populated with form data
     * @throws NumberFormatException If numeric conversion fails
     */
    private Product extractProductFromRequest(HttpServletRequest request) throws NumberFormatException {
        Product product = new Product();
        
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String sku = request.getParameter("sku");
        String unitPriceStr = request.getParameter("unitPrice");
        String reorderLevelStr = request.getParameter("reorderLevel");
        
        product.setName(name);
        product.setDescription(description);
        product.setSku(sku);
        
        if (unitPriceStr != null && !unitPriceStr.isEmpty()) {
            product.setUnitPrice(new BigDecimal(unitPriceStr));
        }
        
        if (reorderLevelStr != null && !reorderLevelStr.isEmpty()) {
            product.setReorderLevel(Integer.parseInt(reorderLevelStr));
        }
        
        return product;
    }
} 
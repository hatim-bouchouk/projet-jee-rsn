package com.scm.controller;

import java.io.IOException;
import java.time.LocalDateTime;
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

import com.scm.model.Stock;
import com.scm.model.StockMovement;
import com.scm.model.StockMovement.MovementType;
import com.scm.security.model.UserPrincipal;
import com.scm.security.util.SessionManager;
import com.scm.service.ProductService;
import com.scm.service.StockService;
import com.scm.service.exception.ServiceException;
import com.scm.service.exception.ValidationException;

/**
 * Servlet for handling inventory management operations.
 */
@WebServlet(name = "StockServlet", urlPatterns = {"/stock", "/stock/*"})
public class StockServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(StockServlet.class.getName());
    
    @Inject
    private StockService stockService;
    
    @Inject
    private ProductService productService;
    
    /**
     * Handles GET requests for stock operations.
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
        if (!currentUser.hasPermission("stock:view")) {
            response.sendRedirect(request.getContextPath() + "/access-denied");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // List products needing reorder
                List<Stock> stocksNeedingReorder = stockService.getProductsNeedingReorder();
                request.setAttribute("stocks", stocksNeedingReorder);
                request.setAttribute("title", "Products Needing Reorder");
                request.getRequestDispatcher("/WEB-INF/jsp/stock/list.jsp").forward(request, response);
                
            } else if (pathInfo.startsWith("/view/")) {
                // View stock for a specific product
                String idStr = pathInfo.substring("/view/".length());
                Integer productId = Integer.parseInt(idStr);
                
                Optional<Stock> stockOpt = stockService.getStockForProduct(productId);
                
                if (stockOpt.isPresent()) {
                    Stock stock = stockOpt.get();
                    
                    // Get stock movements for this product
                    List<StockMovement> movements = stockService.getStockMovementsForProduct(productId);
                    
                    request.setAttribute("stock", stock);
                    request.setAttribute("movements", movements);
                    request.getRequestDispatcher("/WEB-INF/jsp/stock/view.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Stock not found for the specified product");
                    response.sendRedirect(request.getContextPath() + "/stock");
                }
                
            } else if (pathInfo.equals("/movements")) {
                // List all stock movements
                
                // Get filter parameters
                String typeParam = request.getParameter("type");
                String referenceIdParam = request.getParameter("referenceId");
                String startDateParam = request.getParameter("startDate");
                String endDateParam = request.getParameter("endDate");
                
                List<StockMovement> movements;
                
                if (typeParam != null && !typeParam.isEmpty()) {
                    // Filter by movement type
                    MovementType type = MovementType.valueOf(typeParam);
                    movements = stockService.getStockMovementsByType(type);
                    request.setAttribute("typeFilter", type);
                } else if (referenceIdParam != null && !referenceIdParam.isEmpty()) {
                    // Filter by reference ID
                    Integer referenceId = Integer.parseInt(referenceIdParam);
                    movements = stockService.getStockMovementsByReferenceId(referenceId);
                    request.setAttribute("referenceIdFilter", referenceId);
                } else if (startDateParam != null && !startDateParam.isEmpty() && 
                           endDateParam != null && !endDateParam.isEmpty()) {
                    // Filter by date range
                    LocalDateTime startDate = LocalDateTime.parse(startDateParam);
                    LocalDateTime endDate = LocalDateTime.parse(endDateParam);
                    movements = stockService.getStockMovementsByDateRange(startDate, endDate);
                    request.setAttribute("startDateFilter", startDateParam);
                    request.setAttribute("endDateFilter", endDateParam);
                } else {
                    // No filters, get recent movements (implementation-specific)
                    movements = stockService.getStockMovementsByDateRange(
                            LocalDateTime.now().minusDays(30), LocalDateTime.now());
                }
                
                request.setAttribute("movements", movements);
                request.getRequestDispatcher("/WEB-INF/jsp/stock/movements.jsp").forward(request, response);
                
            } else if (pathInfo.equals("/adjust")) {
                // Show stock adjustment form
                if (!currentUser.hasPermission("stock:edit")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                // Get all products for selection
                try {
                    request.setAttribute("products", productService.findAllProducts());
                } catch (ServiceException e) {
                    LOGGER.log(Level.SEVERE, "Error retrieving products", e);
                    request.setAttribute("error", "An error occurred while retrieving products: " + e.getMessage());
                }
                request.getRequestDispatcher("/WEB-INF/jsp/stock/adjust.jsp").forward(request, response);
                
            } else {
                // Invalid path
                response.sendRedirect(request.getContextPath() + "/stock");
            }
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid numeric format in request", e);
            request.setAttribute("error", "Invalid numeric format: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/stock");
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving stock data", e);
            request.setAttribute("error", "An error occurred while retrieving stock data: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/stock/list.jsp").forward(request, response);
        }
    }
    
    /**
     * Handles POST requests for stock operations.
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
        
        // Check permissions
        if (!currentUser.hasPermission("stock:edit")) {
            response.sendRedirect(request.getContextPath() + "/access-denied");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/adjust")) {
                // Process stock adjustment
                String productIdStr = request.getParameter("productId");
                String quantityStr = request.getParameter("quantity");
                String notes = request.getParameter("notes");
                
                if (productIdStr == null || quantityStr == null) {
                    request.setAttribute("error", "Product ID and quantity are required");
                    try {
                        request.setAttribute("products", productService.findAllProducts());
                    } catch (ServiceException e) {
                        LOGGER.log(Level.SEVERE, "Error retrieving products", e);
                        request.setAttribute("error", "An error occurred while retrieving products: " + e.getMessage());
                    }
                    request.getRequestDispatcher("/WEB-INF/jsp/stock/adjust.jsp").forward(request, response);
                    return;
                }
                
                Integer productId = Integer.parseInt(productIdStr);
                Integer quantity = Integer.parseInt(quantityStr);
                
                // Create stock adjustment
                StockMovement movement = stockService.createStockAdjustment(productId, quantity, notes);
                
                // Redirect to view the updated stock
                request.setAttribute("message", "Stock adjustment created successfully");
                response.sendRedirect(request.getContextPath() + "/stock/view/" + productId);
                
            } else if (pathInfo.startsWith("/update/")) {
                // Update stock quantity
                String idStr = pathInfo.substring("/update/".length());
                Integer productId = Integer.parseInt(idStr);
                
                String quantityChangeStr = request.getParameter("quantityChange");
                String movementTypeStr = request.getParameter("movementType");
                String referenceIdStr = request.getParameter("referenceId");
                String notes = request.getParameter("notes");
                
                if (quantityChangeStr == null || movementTypeStr == null) {
                    request.setAttribute("error", "Quantity change and movement type are required");
                    response.sendRedirect(request.getContextPath() + "/stock/view/" + productId);
                    return;
                }
                
                Integer quantityChange = Integer.parseInt(quantityChangeStr);
                MovementType movementType = MovementType.valueOf(movementTypeStr);
                Integer referenceId = null;
                
                if (referenceIdStr != null && !referenceIdStr.isEmpty()) {
                    referenceId = Integer.parseInt(referenceIdStr);
                }
                
                // Update stock quantity
                Optional<Stock> updatedStock = stockService.updateStockQuantity(
                        productId, quantityChange, movementType, referenceId, notes);
                
                if (updatedStock.isPresent()) {
                    request.setAttribute("message", "Stock quantity updated successfully");
                } else {
                    request.setAttribute("error", "Stock not found or could not be updated");
                }
                
                response.sendRedirect(request.getContextPath() + "/stock/view/" + productId);
                
            } else {
                // Invalid path
                response.sendRedirect(request.getContextPath() + "/stock");
            }
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid numeric format in stock form", e);
            request.setAttribute("error", "Invalid numeric format: " + e.getMessage());
            try {
                request.setAttribute("products", productService.findAllProducts());
            } catch (ServiceException se) {
                LOGGER.log(Level.SEVERE, "Error retrieving products", se);
                request.setAttribute("error", "An error occurred while retrieving products: " + se.getMessage());
            }
            request.getRequestDispatcher("/WEB-INF/jsp/stock/adjust.jsp").forward(request, response);
        } catch (ValidationException e) {
            LOGGER.log(Level.WARNING, "Stock validation error", e);
            request.setAttribute("error", "Validation error: " + e.getMessage());
            try {
                request.setAttribute("products", productService.findAllProducts());
            } catch (ServiceException se) {
                LOGGER.log(Level.SEVERE, "Error retrieving products", se);
                request.setAttribute("error", "An error occurred while retrieving products: " + se.getMessage());
            }
            request.getRequestDispatcher("/WEB-INF/jsp/stock/adjust.jsp").forward(request, response);
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Error processing stock operation", e);
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            try {
                request.setAttribute("products", productService.findAllProducts());
            } catch (ServiceException se) {
                LOGGER.log(Level.SEVERE, "Error retrieving products", se);
                // Don't override the original error message
            }
            request.getRequestDispatcher("/WEB-INF/jsp/stock/adjust.jsp").forward(request, response);
        }
    }
} 
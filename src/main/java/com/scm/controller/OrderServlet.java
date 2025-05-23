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

import com.scm.model.CustomerOrder;
import com.scm.model.CustomerOrder.Status;
import com.scm.model.OrderItem;
import com.scm.model.Product;
import com.scm.security.model.UserPrincipal;
import com.scm.security.util.SessionManager;
import com.scm.service.OrderService;
import com.scm.service.ProductService;
import com.scm.service.StockService;
import com.scm.service.exception.ServiceException;
import com.scm.service.exception.ValidationException;

/**
 * Servlet for handling order management operations.
 */
@WebServlet(name = "OrderServlet", urlPatterns = {"/orders", "/orders/*"})
public class OrderServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(OrderServlet.class.getName());
    
    @Inject
    private OrderService orderService;
    
    @Inject
    private ProductService productService;
    
    @Inject
    private StockService stockService;
    
    /**
     * Handles GET requests for order operations.
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
        if (!currentUser.hasPermission("order:view")) {
            response.sendRedirect(request.getContextPath() + "/access-denied");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // List all orders
                List<CustomerOrder> orders;
                
                // Filter by status if provided
                String statusParam = request.getParameter("status");
                if (statusParam != null && !statusParam.isEmpty()) {
                    Status status = Status.valueOf(statusParam);
                    orders = orderService.findByStatus(status);
                    request.setAttribute("statusFilter", status);
                } else {
                    orders = orderService.findRecentOrders(50); // Get most recent 50 orders
                }
                
                request.setAttribute("orders", orders);
                request.getRequestDispatcher("/WEB-INF/jsp/order/list.jsp").forward(request, response);
                
            } else if (pathInfo.startsWith("/view/")) {
                // View a specific order
                String idStr = pathInfo.substring("/view/".length());
                Integer orderId = Integer.parseInt(idStr);
                
                Optional<CustomerOrder> orderOpt = orderService.findById(orderId);
                
                if (orderOpt.isPresent()) {
                    request.setAttribute("order", orderOpt.get());
                    request.getRequestDispatcher("/WEB-INF/jsp/order/view.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Order not found");
                    response.sendRedirect(request.getContextPath() + "/orders");
                }
                
            } else if (pathInfo.equals("/create")) {
                // Show create order form
                if (!currentUser.hasPermission("order:create")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                // Get all products for order item selection
                List<Product> products = productService.findAllProducts();
                request.setAttribute("products", products);
                
                request.getRequestDispatcher("/WEB-INF/jsp/order/form.jsp").forward(request, response);
                
            } else if (pathInfo.startsWith("/edit/")) {
                // Show edit order form
                if (!currentUser.hasPermission("order:edit")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/edit/".length());
                Integer orderId = Integer.parseInt(idStr);
                
                Optional<CustomerOrder> orderOpt = orderService.findById(orderId);
                
                if (orderOpt.isPresent()) {
                    CustomerOrder order = orderOpt.get();
                    
                    // Only allow editing of orders in certain statuses
                    if (order.getStatus() == CustomerOrder.Status.COMPLETED || order.getStatus() == CustomerOrder.Status.CANCELLED) {
                        request.setAttribute("error", "Cannot edit orders that are completed or cancelled");
                        response.sendRedirect(request.getContextPath() + "/orders/view/" + orderId);
                        return;
                    }
                    
                    // Get all products for order item selection
                    List<Product> products = productService.findAllProducts();
                    
                    request.setAttribute("order", order);
                    request.setAttribute("products", products);
                    request.getRequestDispatcher("/WEB-INF/jsp/order/form.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Order not found");
                    response.sendRedirect(request.getContextPath() + "/orders");
                }
                
            } else if (pathInfo.startsWith("/add-item/")) {
                // Show add item form
                if (!currentUser.hasPermission("order:edit")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/add-item/".length());
                Integer orderId = Integer.parseInt(idStr);
                
                Optional<CustomerOrder> orderOpt = orderService.findById(orderId);
                
                if (orderOpt.isPresent()) {
                    CustomerOrder order = orderOpt.get();
                    
                    // Only allow adding items to orders in certain statuses
                    if (order.getStatus() != CustomerOrder.Status.NEW && order.getStatus() != CustomerOrder.Status.PROCESSING) {
                        request.setAttribute("error", "Cannot add items to orders that are not in NEW or PROCESSING status");
                        response.sendRedirect(request.getContextPath() + "/orders/view/" + orderId);
                        return;
                    }
                    
                    // Get all products for order item selection
                    List<Product> products = productService.findAllProducts();
                    
                    request.setAttribute("order", order);
                    request.setAttribute("products", products);
                    request.getRequestDispatcher("/WEB-INF/jsp/order/add-item.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Order not found");
                    response.sendRedirect(request.getContextPath() + "/orders");
                }
                
            } else {
                // Invalid path
                response.sendRedirect(request.getContextPath() + "/orders");
            }
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid order ID format", e);
            request.setAttribute("error", "Invalid order ID format");
            response.sendRedirect(request.getContextPath() + "/orders");
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving order data", e);
            request.setAttribute("error", "An error occurred while retrieving order data: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/order/list.jsp").forward(request, response);
        }
    }
    
    /**
     * Handles POST requests for order operations.
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
                // Create a new order
                if (!currentUser.hasPermission("order:create")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                // Extract form data
                CustomerOrder order = extractOrderFromRequest(request);
                
                // Create the order
                order = orderService.createOrder(order);
                
                // Redirect to view the new order
                request.setAttribute("message", "Order created successfully");
                response.sendRedirect(request.getContextPath() + "/orders/view/" + order.getId());
                
            } else if (pathInfo.startsWith("/edit/")) {
                // Update an existing order
                if (!currentUser.hasPermission("order:edit")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/edit/".length());
                Integer orderId = Integer.parseInt(idStr);
                
                // Extract form data
                CustomerOrder order = extractOrderFromRequest(request);
                order.setId(orderId);
                
                // Update the order
                order = orderService.updateOrder(order);
                
                // Redirect to view the updated order
                request.setAttribute("message", "Order updated successfully");
                response.sendRedirect(request.getContextPath() + "/orders/view/" + order.getId());
                
            } else if (pathInfo.startsWith("/cancel/")) {
                // Cancel an order
                if (!currentUser.hasPermission("order:edit")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/cancel/".length());
                Integer orderId = Integer.parseInt(idStr);
                
                boolean cancelled = orderService.cancelOrder(orderId);
                
                if (cancelled) {
                    request.setAttribute("message", "Order cancelled successfully");
                } else {
                    request.setAttribute("error", "Order not found or could not be cancelled");
                }
                
                response.sendRedirect(request.getContextPath() + "/orders/view/" + orderId);
                
            } else if (pathInfo.startsWith("/update-status/")) {
                // Update order status
                if (!currentUser.hasPermission("order:edit")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/update-status/".length());
                Integer orderId = Integer.parseInt(idStr);
                
                String statusStr = request.getParameter("status");
                Status status = Status.valueOf(statusStr);
                
                Optional<CustomerOrder> updatedOrder = orderService.updateOrderStatus(orderId, status);
                
                if (updatedOrder.isPresent()) {
                    request.setAttribute("message", "Order status updated successfully");
                } else {
                    request.setAttribute("error", "Order not found or status could not be updated");
                }
                
                response.sendRedirect(request.getContextPath() + "/orders/view/" + orderId);
                
            } else if (pathInfo.startsWith("/add-item/")) {
                // Add item to an order
                if (!currentUser.hasPermission("order:edit")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/add-item/".length());
                Integer orderId = Integer.parseInt(idStr);
                
                // Extract order item data
                OrderItem item = extractOrderItemFromRequest(request);
                
                // Add the item to the order
                OrderItem addedItem = orderService.addOrderItem(orderId, item);
                
                request.setAttribute("message", "Item added to order successfully");
                response.sendRedirect(request.getContextPath() + "/orders/view/" + orderId);
                
            } else if (pathInfo.startsWith("/remove-item/")) {
                // Remove item from an order
                if (!currentUser.hasPermission("order:edit")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/remove-item/".length());
                Integer orderItemId = Integer.parseInt(idStr);
                
                // Get the order ID for redirection
                String orderIdStr = request.getParameter("orderId");
                Integer orderId = Integer.parseInt(orderIdStr);
                
                boolean removed = orderService.removeOrderItem(orderItemId);
                
                if (removed) {
                    request.setAttribute("message", "Item removed from order successfully");
                } else {
                    request.setAttribute("error", "Item not found or could not be removed");
                }
                
                response.sendRedirect(request.getContextPath() + "/orders/view/" + orderId);
                
            } else if (pathInfo.startsWith("/process-payment/")) {
                // Process payment for an order
                if (!currentUser.hasPermission("order:edit")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/process-payment/".length());
                Integer orderId = Integer.parseInt(idStr);
                
                // Extract payment details (simplified for this example)
                String paymentMethod = request.getParameter("paymentMethod");
                String paymentReference = request.getParameter("paymentReference");
                
                // Create a simple payment details object
                Object paymentDetails = new Object[] { paymentMethod, paymentReference };
                
                boolean processed = orderService.processPayment(orderId, paymentDetails);
                
                if (processed) {
                    request.setAttribute("message", "Payment processed successfully");
                } else {
                    request.setAttribute("error", "Payment could not be processed");
                }
                
                response.sendRedirect(request.getContextPath() + "/orders/view/" + orderId);
                
            } else if (pathInfo.startsWith("/process-shipment/")) {
                // Process shipment for an order
                if (!currentUser.hasPermission("order:edit")) {
                    response.sendRedirect(request.getContextPath() + "/access-denied");
                    return;
                }
                
                String idStr = pathInfo.substring("/process-shipment/".length());
                Integer orderId = Integer.parseInt(idStr);
                
                // Extract shipment details (simplified for this example)
                String carrier = request.getParameter("carrier");
                String trackingNumber = request.getParameter("trackingNumber");
                
                // Create a simple shipment details object
                Object shipmentDetails = new Object[] { carrier, trackingNumber };
                
                // Check if there is sufficient stock
                boolean sufficientStock = stockService.checkSufficientStockForOrder(orderId);
                
                if (!sufficientStock) {
                    request.setAttribute("error", "Insufficient stock to process shipment");
                    response.sendRedirect(request.getContextPath() + "/orders/view/" + orderId);
                    return;
                }
                
                String trackingInfo = orderService.processShipment(orderId, shipmentDetails);
                
                if (trackingInfo != null) {
                    // Process stock for the order
                    stockService.processStockForCustomerOrder(orderId);
                    
                    request.setAttribute("message", "Shipment processed successfully. Tracking: " + trackingInfo);
                } else {
                    request.setAttribute("error", "Shipment could not be processed");
                }
                
                response.sendRedirect(request.getContextPath() + "/orders/view/" + orderId);
                
            } else {
                // Invalid path
                response.sendRedirect(request.getContextPath() + "/orders");
            }
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid numeric format in order form", e);
            request.setAttribute("error", "Invalid numeric format: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/order/form.jsp").forward(request, response);
        } catch (ValidationException e) {
            LOGGER.log(Level.WARNING, "Order validation error", e);
            request.setAttribute("error", "Validation error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/order/form.jsp").forward(request, response);
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Error processing order operation", e);
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/order/form.jsp").forward(request, response);
        }
    }
    
    /**
     * Extract order data from request parameters
     * 
     * @param request The HTTP request
     * @return CustomerOrder object populated with form data
     */
    private CustomerOrder extractOrderFromRequest(HttpServletRequest request) {
        CustomerOrder order = new CustomerOrder();
        
        String customerName = request.getParameter("customerName");
        String customerEmail = request.getParameter("customerEmail");
        String customerPhone = request.getParameter("customerPhone");
        String shippingAddress = request.getParameter("shippingAddress");
        String notes = request.getParameter("notes");
        
        order.setCustomerName(customerName);
        order.setCustomerEmail(customerEmail);
        order.setCustomerPhone(customerPhone);
        order.setShippingAddress(shippingAddress);
        order.setNotes(notes);
        
        // Set initial status
        order.setStatus(CustomerOrder.Status.NEW);
        
        return order;
    }
    
    /**
     * Extract order item data from request parameters
     * 
     * @param request The HTTP request
     * @return OrderItem object populated with form data
     * @throws NumberFormatException If numeric conversion fails
     */
    private OrderItem extractOrderItemFromRequest(HttpServletRequest request) throws NumberFormatException {
        OrderItem item = new OrderItem();
        
        String productIdStr = request.getParameter("productId");
        String quantityStr = request.getParameter("quantity");
        String unitPriceStr = request.getParameter("unitPrice");
        
        Integer productId = Integer.parseInt(productIdStr);
        Integer quantity = Integer.parseInt(quantityStr);
        BigDecimal unitPrice = new BigDecimal(unitPriceStr);
        
        Product product = new Product();
        product.setId(productId);
        
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        
        return item;
    }
} 
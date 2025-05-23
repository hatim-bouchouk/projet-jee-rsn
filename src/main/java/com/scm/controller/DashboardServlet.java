package com.scm.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.scm.model.CustomerOrder;
import com.scm.model.Product;
import com.scm.security.model.UserPrincipal;
import com.scm.security.util.SessionManager;
import com.scm.service.DashboardService;
import com.scm.service.exception.ServiceException;

/**
 * Servlet for handling the main dashboard display.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DashboardServlet.class.getName());
    
    @Inject
    private DashboardService dashboardService;
    
    /**
     * Handles GET requests to display the dashboard.
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
        if (!currentUser.hasPermission("dashboard:view")) {
            response.sendRedirect(request.getContextPath() + "/access-denied");
            return;
        }
        
        try {
            // Get date range parameters or use defaults (last 30 days)
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minus(30, ChronoUnit.DAYS);
            
            String startDateParam = request.getParameter("startDate");
            String endDateParam = request.getParameter("endDate");
            
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            
            if (startDateParam != null && !startDateParam.isEmpty()) {
                startDate = LocalDateTime.parse(startDateParam, formatter);
            }
            
            if (endDateParam != null && !endDateParam.isEmpty()) {
                endDate = LocalDateTime.parse(endDateParam, formatter);
            }
            
            // Get dashboard data
            Map<String, Object> salesStats = dashboardService.getSalesStatistics(startDate, endDate);
            Map<String, Object> inventoryStats = dashboardService.getInventoryStatistics();
            Map<String, Object> supplierStats = dashboardService.getSupplierStatistics();
            
            List<Map<String, Object>> topProducts = dashboardService.getTopSellingProducts(startDate, endDate, 5);
            List<Map<String, Object>> salesByPeriod = dashboardService.getSalesByTimePeriod(startDate, endDate, "day");
            List<Map<String, Object>> recentActivity = dashboardService.getRecentActivity(10);
            
            List<CustomerOrder> ordersNeedingAttention = dashboardService.getOrdersRequiringAttention();
            List<Product> stockAlerts = dashboardService.getStockAlerts();
            
            // Set attributes for the view
            request.setAttribute("salesStats", salesStats);
            request.setAttribute("inventoryStats", inventoryStats);
            request.setAttribute("supplierStats", supplierStats);
            request.setAttribute("topProducts", topProducts);
            request.setAttribute("salesByPeriod", salesByPeriod);
            request.setAttribute("recentActivity", recentActivity);
            request.setAttribute("ordersNeedingAttention", ordersNeedingAttention);
            request.setAttribute("stockAlerts", stockAlerts);
            request.setAttribute("startDate", startDate.format(formatter));
            request.setAttribute("endDate", endDate.format(formatter));
            request.setAttribute("currentUser", currentUser);
            
            // Forward to dashboard view
            request.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(request, response);
            
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving dashboard data", e);
            request.setAttribute("error", "An error occurred while retrieving dashboard data: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(request, response);
        }
    }
    
    /**
     * Handles POST requests for dashboard actions (e.g., exporting reports).
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
        
        // Check permissions for report generation
        if (!currentUser.hasPermission("report:export")) {
            response.sendRedirect(request.getContextPath() + "/access-denied");
            return;
        }
        
        try {
            String action = request.getParameter("action");
            
            if ("exportSalesReport".equals(action)) {
                // Get date range parameters
                LocalDateTime endDate = LocalDateTime.now();
                LocalDateTime startDate = endDate.minus(30, ChronoUnit.DAYS);
                
                String startDateParam = request.getParameter("startDate");
                String endDateParam = request.getParameter("endDate");
                String format = request.getParameter("format");
                
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                
                if (startDateParam != null && !startDateParam.isEmpty()) {
                    startDate = LocalDateTime.parse(startDateParam, formatter);
                }
                
                if (endDateParam != null && !endDateParam.isEmpty()) {
                    endDate = LocalDateTime.parse(endDateParam, formatter);
                }
                
                if (format == null || format.isEmpty()) {
                    format = "PDF";
                }
                
                // Generate and send the report
                byte[] reportData = dashboardService.generateSalesReport(startDate, endDate, format);
                
                // Set response headers
                response.setContentType(getContentType(format));
                response.setHeader("Content-Disposition", "attachment; filename=sales-report." + format.toLowerCase());
                response.setContentLength(reportData.length);
                
                // Write report data to response
                response.getOutputStream().write(reportData);
                response.getOutputStream().flush();
                
            } else if ("exportInventoryReport".equals(action)) {
                String format = request.getParameter("format");
                
                if (format == null || format.isEmpty()) {
                    format = "PDF";
                }
                
                // Generate and send the report
                byte[] reportData = dashboardService.generateInventoryReport(format);
                
                // Set response headers
                response.setContentType(getContentType(format));
                response.setHeader("Content-Disposition", "attachment; filename=inventory-report." + format.toLowerCase());
                response.setContentLength(reportData.length);
                
                // Write report data to response
                response.getOutputStream().write(reportData);
                response.getOutputStream().flush();
                
            } else {
                // Unknown action
                response.sendRedirect(request.getContextPath() + "/dashboard");
            }
            
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Error generating report", e);
            request.setAttribute("error", "An error occurred while generating the report: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(request, response);
        }
    }
    
    /**
     * Get the content type for a report format
     * 
     * @param format Report format
     * @return Content type
     */
    private String getContentType(String format) {
        switch (format.toUpperCase()) {
            case "PDF":
                return "application/pdf";
            case "CSV":
                return "text/csv";
            case "XLSX":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default:
                return "application/octet-stream";
        }
    }
} 
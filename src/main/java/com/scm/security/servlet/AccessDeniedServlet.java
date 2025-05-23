package com.scm.security.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for handling access denied situations.
 * This shows a friendly page when users try to access resources they don't have permission for.
 */
@WebServlet(name = "AccessDeniedServlet", urlPatterns = {"/access-denied"})
public class AccessDeniedServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;

    /**
     * Handles GET requests for access denied pages.
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @throws ServletException If an error occurs during request handling
     * @throws IOException If an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get the referrer URL if available
        String referrer = request.getHeader("Referer");
        if (referrer != null && !referrer.isEmpty()) {
            request.setAttribute("referrer", referrer);
        }
        
        // Set the HTTP status code to 403 Forbidden
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        
        // Forward to the access denied page
        request.getRequestDispatcher("/WEB-INF/jsp/access-denied.jsp").forward(request, response);
    }
    
    /**
     * Handles POST requests for access denied page.
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @throws ServletException If an error occurs during request handling
     * @throws IOException If an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Just delegate to doGet
        doGet(request, response);
    }
} 
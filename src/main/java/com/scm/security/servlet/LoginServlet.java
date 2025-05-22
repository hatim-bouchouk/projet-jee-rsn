package com.scm.security.servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.scm.model.User;
import com.scm.security.model.UserPrincipal;
import com.scm.security.util.SessionManager;
import com.scm.service.UserService;
import com.scm.service.exception.AuthenticationException;
import com.scm.service.exception.ServiceException;

/**
 * Servlet for handling user login.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    
    @Inject
    private UserService userService;
    
    /**
     * Handles GET requests to the login page.
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @throws ServletException If an error occurs during request handling
     * @throws IOException If an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if user is already logged in
        UserPrincipal currentUser = SessionManager.getCurrentUser(request);
        if (currentUser != null) {
            // User is already logged in, redirect to home page
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Get the redirect URL if any
        String redirectUrl = request.getParameter("redirect");
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            request.setAttribute("redirectUrl", redirectUrl);
        }
        
        // Forward to login page
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
    
    /**
     * Handles POST requests for login form submission.
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @throws ServletException If an error occurs during request handling
     * @throws IOException If an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String redirectUrl = request.getParameter("redirect");
        
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("error", "Username and password are required");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }
        
        try {
            // Authenticate user
            User user = userService.authenticate(username, password);
            
            // Create user session
            UserPrincipal principal = SessionManager.createSession(request, response, user);
            
            LOGGER.log(Level.INFO, "User {0} logged in successfully", username);
            
            // Redirect to the appropriate page based on role or the redirect URL
            if (redirectUrl != null && !redirectUrl.isEmpty() && !redirectUrl.contains("login") && !redirectUrl.contains("logout")) {
                response.sendRedirect(request.getContextPath() + redirectUrl);
            } else if (principal.hasRole("ADMIN")) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else if (principal.hasRole("MANAGER")) {
                response.sendRedirect(request.getContextPath() + "/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/products");
            }
            
        } catch (AuthenticationException e) {
            LOGGER.log(Level.INFO, "Authentication failed for username: {0}", username);
            request.setAttribute("error", "Invalid username or password");
            request.setAttribute("username", username);
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                request.setAttribute("redirectUrl", redirectUrl);
            }
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Error during authentication", e);
            request.setAttribute("error", "An error occurred during login. Please try again later.");
            request.setAttribute("username", username);
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                request.setAttribute("redirectUrl", redirectUrl);
            }
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
} 
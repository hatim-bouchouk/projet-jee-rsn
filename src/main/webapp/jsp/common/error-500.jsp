<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - Internal Server Error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
    <style>
        .error-container {
            text-align: center;
            padding: 100px 0;
        }
        .error-code {
            font-size: 120px;
            font-weight: bold;
            color: #dc3545;
        }
        .error-message {
            font-size: 24px;
            margin: 20px 0;
        }
        .error-details {
            margin: 20px 0;
            text-align: left;
            background-color: #f8f9fa;
            padding: 15px;
            border-radius: 5px;
            max-height: 200px;
            overflow-y: auto;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="error-container">
            <div class="error-code">500</div>
            <div class="error-message">Internal Server Error</div>
            <p>Sorry, something went wrong on our server. Our technical team has been notified.</p>
            
            <% if (request.getParameter("debug") != null && request.getParameter("debug").equals("true") && exception != null) { %>
                <div class="error-details">
                    <h5>Error Details (Debug Mode):</h5>
                    <p><strong>Message:</strong> <%= exception.getMessage() %></p>
                    <p><strong>Type:</strong> <%= exception.getClass().getName() %></p>
                    <pre><%= exception.getStackTrace() %></pre>
                </div>
            <% } %>
            
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary mt-4">Go to Homepage</a>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/resources/js/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.bundle.min.js"></script>
</body>
</html> 
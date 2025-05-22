<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Access Denied - Supply Chain Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body class="error-page">
    <div class="error-container">
        <div class="error-code">403</div>
        <h1>Access Denied</h1>
        <div class="error-message">
            <p>You don't have permission to access this resource.</p>
            <p>Please contact your administrator if you believe this is an error.</p>
        </div>
        
        <div class="error-actions">
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Go to Home</a>
            
            <c:if test="${not empty referrer}">
                <a href="${referrer}" class="btn btn-secondary">Go Back</a>
            </c:if>
        </div>
    </div>
</body>
</html> 
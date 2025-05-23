<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>403 - Access Denied</title>
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
    </style>
</head>
<body>
    <div class="container">
        <div class="error-container">
            <div class="error-code">403</div>
            <div class="error-message">Access Denied</div>
            <p>You don't have permission to access this resource.</p>
            <p>Please contact your administrator if you believe this is an error.</p>
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary mt-4">Go to Homepage</a>
            <a href="${pageContext.request.contextPath}/jsp/common/login.jsp" class="btn btn-outline-primary mt-4">Login</a>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/resources/js/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.bundle.min.js"></script>
</body>
</html> 
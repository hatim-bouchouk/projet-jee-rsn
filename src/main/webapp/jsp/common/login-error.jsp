<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login Error - Supply Chain Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css">
    <style>
        .error-container {
            max-width: 500px;
            margin: 50px auto;
            padding: 30px;
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        
        .error-icon {
            font-size: 48px;
            color: #e74c3c;
            margin-bottom: 20px;
        }
        
        .error-message {
            margin-bottom: 30px;
            color: #e74c3c;
            font-weight: bold;
        }
        
        .error-container a {
            display: inline-block;
            margin-top: 20px;
            padding: 10px 20px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            transition: background-color 0.3s;
        }
        
        .error-container a:hover {
            background-color: #2980b9;
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1>Supply Chain Management System</h1>
        </header>
        
        <main>
            <div class="error-container">
                <div class="error-icon">!</div>
                <h2>Login Failed</h2>
                <div class="error-message">
                    Authentication failed. The username or password you entered is incorrect.
                </div>
                <p>Please check your credentials and try again.</p>
                <a href="${pageContext.request.contextPath}/jsp/common/login.jsp">Back to Login</a>
            </div>
        </main>
        
        <footer>
            <p>&copy; 2024 Supply Chain Management System. All rights reserved.</p>
        </footer>
    </div>
</body>
</html> 
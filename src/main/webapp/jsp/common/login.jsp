<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Supply Chain Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css">
    <style>
        .login-container {
            max-width: 400px;
            margin: 50px auto;
            padding: 30px;
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        
        .login-container h2 {
            text-align: center;
            margin-bottom: 30px;
        }
        
        .login-form .form-group {
            margin-bottom: 20px;
        }
        
        .login-form button {
            width: 100%;
            padding: 12px;
            font-size: 16px;
        }
        
        .login-links {
            text-align: center;
            margin-top: 20px;
        }
        
        .login-links a {
            color: #3498db;
            text-decoration: none;
        }
        
        .login-links a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1>Supply Chain Management System</h1>
        </header>
        
        <main>
            <div class="login-container">
                <h2>Login</h2>
                
                <c:if test="${param.error != null}">
                    <div class="error-message">
                        Invalid username or password. Please try again.
                    </div>
                </c:if>
                
                <form class="login-form" action="j_security_check" method="post">
                    <div class="form-group">
                        <label for="j_username">Username</label>
                        <input type="text" id="j_username" name="j_username" required autofocus>
                    </div>
                    
                    <div class="form-group">
                        <label for="j_password">Password</label>
                        <input type="password" id="j_password" name="j_password" required>
                    </div>
                    
                    <div class="form-group">
                        <button type="submit">Login</button>
                    </div>
                </form>
                
                <div class="login-links">
                    <a href="${pageContext.request.contextPath}/">Back to Home</a>
                </div>
            </div>
        </main>
        
        <footer>
            <p>&copy; 2024 Supply Chain Management System. All rights reserved.</p>
        </footer>
    </div>
</body>
</html> 
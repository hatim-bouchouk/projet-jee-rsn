<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Supply Chain Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Supply Chain Management System</h1>
            <nav>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/inventory">Inventory Management</a></li>
                    <li><a href="${pageContext.request.contextPath}/supplier">Supplier Management</a></li>
                    <li><a href="${pageContext.request.contextPath}/order">Order Management</a></li>
                    <li><a href="${pageContext.request.contextPath}/report">Reports</a></li>
                </ul>
            </nav>
        </header>
        
        <main>
            <section class="welcome">
                <h2>Welcome to the Supply Chain Management System</h2>
                <p>This system helps you manage your supply chain efficiently.</p>
            </section>
            
            <section class="dashboard">
                <h2>Dashboard</h2>
                <div class="widgets">
                    <div class="widget">
                        <h3>Inventory Status</h3>
                        <p>Quick overview of your inventory status will be displayed here.</p>
                    </div>
                    
                    <div class="widget">
                        <h3>Recent Orders</h3>
                        <p>Your most recent orders will be displayed here.</p>
                    </div>
                    
                    <div class="widget">
                        <h3>Supplier Status</h3>
                        <p>Active supplier information will be displayed here.</p>
                    </div>
                </div>
            </section>
        </main>
        
        <footer>
            <p>&copy; 2024 Supply Chain Management System. All rights reserved.</p>
        </footer>
    </div>
    
    <script src="${pageContext.request.contextPath}/resources/js/main.js"></script>
</body>
</html> 
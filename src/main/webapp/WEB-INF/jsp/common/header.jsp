<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://scm.com/tags/security" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.pageTitle} - Supply Chain Management</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="${pageContext.request.contextPath}/assets/css/styles.css" rel="stylesheet">
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
    <!-- Additional head content -->
    <c:if test="${not empty param.extraHead}">
        <jsp:include page="${param.extraHead}" />
    </c:if>
</head>
<body>
    <header>
        <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
            <div class="container-fluid">
                <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">
                    <i class="fas fa-boxes me-2"></i>SCM System
                </a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarMain">
                    <span class="navbar-toggler-icon"></span>
                </button>
                
                <div class="collapse navbar-collapse" id="navbarMain">
                    <ul class="navbar-nav me-auto">
                        <security:authorize hasPermission="dashboard:view">
                            <li class="nav-item">
                                <a class="nav-link ${fn:contains(pageContext.request.requestURI, 'dashboard') ? 'active' : ''}" 
                                   href="${pageContext.request.contextPath}/dashboard">
                                    <i class="fas fa-tachometer-alt me-1"></i> Dashboard
                                </a>
                            </li>
                        </security:authorize>
                        
                        <security:authorize hasPermission="product:view">
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle ${fn:contains(pageContext.request.requestURI, 'product') ? 'active' : ''}" 
                                   href="#" role="button" data-bs-toggle="dropdown">
                                    <i class="fas fa-box me-1"></i> Products
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/products">All Products</a></li>
                                    <security:authorize hasPermission="product:create">
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/products/create">Add Product</a></li>
                                    </security:authorize>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/products/low-stock">Low Stock</a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/products/out-of-stock">Out of Stock</a></li>
                                </ul>
                            </li>
                        </security:authorize>
                        
                        <security:authorize hasPermission="supplier:view">
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle ${fn:contains(pageContext.request.requestURI, 'supplier') ? 'active' : ''}" 
                                   href="#" role="button" data-bs-toggle="dropdown">
                                    <i class="fas fa-truck me-1"></i> Suppliers
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/suppliers">All Suppliers</a></li>
                                    <security:authorize hasPermission="supplier:create">
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/suppliers/create">Add Supplier</a></li>
                                    </security:authorize>
                                </ul>
                            </li>
                        </security:authorize>
                        
                        <security:authorize hasPermission="order:view">
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle ${fn:contains(pageContext.request.requestURI, 'order') ? 'active' : ''}" 
                                   href="#" role="button" data-bs-toggle="dropdown">
                                    <i class="fas fa-shopping-cart me-1"></i> Orders
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/orders">All Orders</a></li>
                                    <security:authorize hasPermission="order:create">
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/orders/create">New Order</a></li>
                                    </security:authorize>
                                </ul>
                            </li>
                        </security:authorize>
                        
                        <security:authorize hasPermission="stock:view">
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle ${fn:contains(pageContext.request.requestURI, 'stock') ? 'active' : ''}" 
                                   href="#" role="button" data-bs-toggle="dropdown">
                                    <i class="fas fa-warehouse me-1"></i> Inventory
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/stock">Stock Overview</a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/stock/movements">Stock Movements</a></li>
                                    <security:authorize hasPermission="stock:edit">
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/stock/adjust">Stock Adjustment</a></li>
                                    </security:authorize>
                                </ul>
                            </li>
                        </security:authorize>
                    </ul>
                    
                    <ul class="navbar-nav ms-auto">
                        <c:if test="${not empty currentUser}">
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                                    <i class="fas fa-user me-1"></i> ${currentUser.username}
                                </a>
                                <ul class="dropdown-menu dropdown-menu-end">
                                    <li><span class="dropdown-item-text text-muted">Role: ${currentUser.roles.iterator().next()}</span></li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Logout</a></li>
                                </ul>
                            </li>
                        </c:if>
                    </ul>
                </div>
            </div>
        </nav>
    </header>
    
    <div class="container-fluid mt-4">
        <!-- Alert messages -->
        <c:if test="${not empty message}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        
        <!-- Main content starts here -->
    </div>
</body>
</html> 
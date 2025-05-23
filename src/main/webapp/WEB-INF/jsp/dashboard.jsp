<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="pageTitle" value="Dashboard" />
</jsp:include>

<div class="row mb-4">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
            <h1><i class="fas fa-tachometer-alt me-2"></i>Dashboard</h1>
            <div>
                <form id="dateRangeForm" class="d-flex align-items-center">
                    <div class="input-group me-2">
                        <span class="input-group-text"><i class="fas fa-calendar"></i></span>
                        <input type="date" class="form-control" id="startDate" name="startDate" value="${startDate}">
                    </div>
                    <div class="input-group me-2">
                        <span class="input-group-text"><i class="fas fa-calendar"></i></span>
                        <input type="date" class="form-control" id="endDate" name="endDate" value="${endDate}">
                    </div>
                    <button type="submit" class="btn btn-primary">Apply</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Stats Cards -->
<div class="row mb-4">
    <!-- Sales Stats -->
    <div class="col-xl-3 col-md-6 mb-4">
        <div class="card border-left-primary shadow h-100 py-2">
            <div class="card-body">
                <div class="row no-gutters align-items-center">
                    <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                            Total Sales</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                            <fmt:formatNumber value="${salesStats.totalSales}" type="currency" currencySymbol="$" />
                        </div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-dollar-sign fa-2x text-gray-300"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Orders Stats -->
    <div class="col-xl-3 col-md-6 mb-4">
        <div class="card border-left-success shadow h-100 py-2">
            <div class="card-body">
                <div class="row no-gutters align-items-center">
                    <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                            Orders</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                            ${salesStats.orderCount}
                        </div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-shopping-cart fa-2x text-gray-300"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Inventory Stats -->
    <div class="col-xl-3 col-md-6 mb-4">
        <div class="card border-left-info shadow h-100 py-2">
            <div class="card-body">
                <div class="row no-gutters align-items-center">
                    <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-info text-uppercase mb-1">
                            Low Stock Items</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                            ${inventoryStats.lowStockCount}
                        </div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-warehouse fa-2x text-gray-300"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Supplier Stats -->
    <div class="col-xl-3 col-md-6 mb-4">
        <div class="card border-left-warning shadow h-100 py-2">
            <div class="card-body">
                <div class="row no-gutters align-items-center">
                    <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-warning text-uppercase mb-1">
                            Active Suppliers</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                            ${supplierStats.activeSupplierCount}
                        </div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-truck fa-2x text-gray-300"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Charts Row -->
<div class="row mb-4">
    <!-- Sales Chart -->
    <div class="col-xl-8 col-lg-7">
        <div class="card shadow mb-4">
            <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                <h6 class="m-0 font-weight-bold text-primary">Sales Overview</h6>
                <div class="dropdown no-arrow">
                    <a class="dropdown-toggle" href="#" role="button" id="salesDropdown" data-bs-toggle="dropdown">
                        <i class="fas fa-ellipsis-v fa-sm fa-fw text-gray-400"></i>
                    </a>
                    <div class="dropdown-menu dropdown-menu-end shadow animated--fade-in" aria-labelledby="salesDropdown">
                        <a class="dropdown-item" href="#" id="exportSalesReport">Export Report</a>
                    </div>
                </div>
            </div>
            <div class="card-body">
                <div class="chart-area">
                    <canvas id="salesChart"></canvas>
                </div>
            </div>
        </div>
    </div>

    <!-- Top Products Chart -->
    <div class="col-xl-4 col-lg-5">
        <div class="card shadow mb-4">
            <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                <h6 class="m-0 font-weight-bold text-primary">Top Products</h6>
            </div>
            <div class="card-body">
                <div class="chart-pie pt-4 pb-2">
                    <canvas id="topProductsChart"></canvas>
                </div>
                <div class="mt-4 text-center small">
                    <c:forEach var="product" items="${topProducts}" varStatus="status">
                        <span class="mr-2">
                            <i class="fas fa-circle" style="color: ${status.index == 0 ? '#4e73df' : status.index == 1 ? '#1cc88a' : status.index == 2 ? '#36b9cc' : status.index == 3 ? '#f6c23e' : '#e74a3b'}"></i> ${product.name}
                        </span>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Content Row -->
<div class="row">
    <!-- Orders Requiring Attention -->
    <div class="col-xl-6 col-lg-6">
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Orders Requiring Attention</h6>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty ordersNeedingAttention}">
                        <p class="text-center text-muted">No orders require attention at this time.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover" width="100%" cellspacing="0">
                                <thead>
                                    <tr>
                                        <th>Order #</th>
                                        <th>Customer</th>
                                        <th>Date</th>
                                        <th>Status</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="order" items="${ordersNeedingAttention}">
                                        <tr>
                                            <td>${order.id}</td>
                                            <td>${order.customerName}</td>
                                            <td><fmt:formatDate value="${order.createdAt}" pattern="yyyy-MM-dd" /></td>
                                            <td>
                                                <span class="badge bg-${order.status == 'NEW' ? 'primary' : order.status == 'PROCESSING' ? 'info' : order.status == 'PENDING_PAYMENT' ? 'warning' : 'secondary'}">
                                                    ${order.status}
                                                </span>
                                            </td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/orders/view/${order.id}" class="btn btn-sm btn-primary">
                                                    <i class="fas fa-eye"></i> View
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Stock Alerts -->
    <div class="col-xl-6 col-lg-6">
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Stock Alerts</h6>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty stockAlerts}">
                        <p class="text-center text-muted">No stock alerts at this time.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover" width="100%" cellspacing="0">
                                <thead>
                                    <tr>
                                        <th>Product</th>
                                        <th>SKU</th>
                                        <th>Current Stock</th>
                                        <th>Reorder Level</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="product" items="${stockAlerts}">
                                        <tr class="${product.stock.quantity == 0 ? 'table-danger' : 'table-warning'}">
                                            <td>${product.name}</td>
                                            <td>${product.sku}</td>
                                            <td>${product.stock.quantity}</td>
                                            <td>${product.reorderLevel}</td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/stock/view/${product.id}" class="btn btn-sm btn-primary">
                                                    <i class="fas fa-eye"></i> View
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<!-- Recent Activity -->
<div class="row">
    <div class="col-12">
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Recent Activity</h6>
            </div>
            <div class="card-body">
                <div class="timeline-container">
                    <c:forEach var="activity" items="${recentActivity}" varStatus="status">
                        <div class="timeline-item">
                            <div class="timeline-icon bg-${activity.type == 'ORDER' ? 'primary' : activity.type == 'STOCK' ? 'success' : activity.type == 'SUPPLIER' ? 'warning' : 'info'}">
                                <i class="fas ${activity.type == 'ORDER' ? 'fa-shopping-cart' : activity.type == 'STOCK' ? 'fa-warehouse' : activity.type == 'SUPPLIER' ? 'fa-truck' : 'fa-box'}"></i>
                            </div>
                            <div class="timeline-content">
                                <h6 class="mb-1">${activity.title}</h6>
                                <p class="mb-0">${activity.description}</p>
                                <small class="text-muted">
                                    <fmt:formatDate value="${activity.timestamp}" pattern="yyyy-MM-dd HH:mm" />
                                </small>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Export Report Form (Hidden) -->
<form id="exportReportForm" action="${pageContext.request.contextPath}/dashboard" method="post" style="display: none;">
    <input type="hidden" name="_csrf" value="${pageContext.request.getSession().getAttribute('csrfToken')}" />
    <input type="hidden" name="action" value="exportSalesReport" />
    <input type="hidden" name="startDate" value="${startDate}" />
    <input type="hidden" name="endDate" value="${endDate}" />
    <input type="hidden" name="format" value="PDF" />
</form>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp">
    <jsp:param name="extraScripts" value="/WEB-INF/jsp/dashboard/scripts.jsp" />
</jsp:include>

<!-- Page-specific scripts -->
<script>
document.addEventListener('DOMContentLoaded', function() {
    // Sales Chart
    const salesChartCtx = document.getElementById('salesChart').getContext('2d');
    const salesLabels = [];
    const salesData = [];
    
    <c:forEach var="dataPoint" items="${salesByPeriod}">
        salesLabels.push("${dataPoint.date}");
        salesData.push(${dataPoint.amount});
    </c:forEach>
    
    new Chart(salesChartCtx, {
        type: 'line',
        data: {
            labels: salesLabels,
            datasets: [{
                label: 'Sales',
                data: salesData,
                backgroundColor: 'rgba(78, 115, 223, 0.05)',
                borderColor: 'rgba(78, 115, 223, 1)',
                pointRadius: 3,
                pointBackgroundColor: 'rgba(78, 115, 223, 1)',
                pointBorderColor: 'rgba(78, 115, 223, 1)',
                pointHoverRadius: 5,
                pointHoverBackgroundColor: 'rgba(78, 115, 223, 1)',
                pointHoverBorderColor: 'rgba(78, 115, 223, 1)',
                pointHitRadius: 10,
                pointBorderWidth: 2,
                tension: 0.3,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: {
                    grid: {
                        display: false,
                        drawBorder: false
                    }
                },
                y: {
                    ticks: {
                        callback: function(value) {
                            return '$' + value;
                        }
                    }
                }
            },
            plugins: {
                legend: {
                    display: false
                }
            }
        }
    });
    
    // Top Products Chart
    const topProductsCtx = document.getElementById('topProductsChart').getContext('2d');
    const productLabels = [];
    const productData = [];
    
    <c:forEach var="product" items="${topProducts}">
        productLabels.push("${product.name}");
        productData.push(${product.quantity});
    </c:forEach>
    
    new Chart(topProductsCtx, {
        type: 'doughnut',
        data: {
            labels: productLabels,
            datasets: [{
                data: productData,
                backgroundColor: ['#4e73df', '#1cc88a', '#36b9cc', '#f6c23e', '#e74a3b'],
                hoverBackgroundColor: ['#2e59d9', '#17a673', '#2c9faf', '#dda20a', '#be2617'],
                hoverBorderColor: "rgba(234, 236, 244, 1)",
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            },
            cutout: '70%'
        }
    });
    
    // Export report handler
    document.getElementById('exportSalesReport').addEventListener('click', function(e) {
        e.preventDefault();
        document.getElementById('exportReportForm').submit();
    });
    
    // Date range form
    document.getElementById('dateRangeForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;
        
        if (startDate && endDate) {
            window.location.href = '${pageContext.request.contextPath}/dashboard?startDate=' + startDate + '&endDate=' + endDate;
        }
    });
});
</script> 
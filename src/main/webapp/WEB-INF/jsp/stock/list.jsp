<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://scm.com/tags/security" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="pageTitle" value="${not empty title ? title : 'Stock Overview'}" />
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1><i class="fas fa-warehouse me-2"></i>${not empty title ? title : 'Stock Overview'}</h1>
    <security:authorize hasPermission="stock:edit">
        <a href="${pageContext.request.contextPath}/stock/adjust" class="btn btn-primary">
            <i class="fas fa-plus-minus me-1"></i> Adjust Stock
        </a>
    </security:authorize>
</div>

<div class="card shadow mb-4">
    <div class="card-header py-3">
        <ul class="nav nav-tabs card-header-tabs">
            <li class="nav-item">
                <a class="nav-link active" href="${pageContext.request.contextPath}/stock">
                    <i class="fas fa-exclamation-triangle me-1"></i> Needs Reorder
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/stock/movements">
                    <i class="fas fa-exchange-alt me-1"></i> Stock Movements
                </a>
            </li>
        </ul>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${empty stocks}">
                <div class="alert alert-success">
                    <i class="fas fa-check-circle me-2"></i> No products need reordering at this time.
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-bordered table-hover" id="stockTable" width="100%" cellspacing="0">
                        <thead>
                            <tr>
                                <th>Product</th>
                                <th>SKU</th>
                                <th>Current Stock</th>
                                <th>Reorder Level</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="stock" items="${stocks}">
                                <tr class="${stock.quantity == 0 ? 'table-danger' : 'table-warning'}">
                                    <td>
                                        <a href="${pageContext.request.contextPath}/products/view/${stock.product.id}">
                                            ${stock.product.name}
                                        </a>
                                    </td>
                                    <td>${stock.product.sku}</td>
                                    <td>${stock.quantity}</td>
                                    <td>${stock.product.reorderLevel}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${stock.quantity == 0}">
                                                <span class="badge bg-danger">Out of Stock</span>
                                            </c:when>
                                            <c:when test="${stock.quantity <= stock.product.reorderLevel}">
                                                <span class="badge bg-warning text-dark">Low Stock</span>
                                            </c:when>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="btn-group" role="group">
                                            <a href="${pageContext.request.contextPath}/stock/view/${stock.product.id}" class="btn btn-sm btn-info">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <security:authorize hasPermission="stock:edit">
                                                <a href="${pageContext.request.contextPath}/stock/adjust?productId=${stock.product.id}" class="btn btn-sm btn-primary">
                                                    <i class="fas fa-plus-minus"></i>
                                                </a>
                                            </security:authorize>
                                        </div>
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

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Initialize DataTable if available
        if ($.fn.DataTable) {
            $('#stockTable').DataTable({
                "order": [[ 2, "asc" ]],
                "pageLength": 25
            });
        }
    });
</script> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://scm.com/tags/security" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="pageTitle" value="Stock Movements" />
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1><i class="fas fa-exchange-alt me-2"></i>Stock Movements</h1>
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
                <a class="nav-link" href="${pageContext.request.contextPath}/stock">
                    <i class="fas fa-exclamation-triangle me-1"></i> Needs Reorder
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link active" href="${pageContext.request.contextPath}/stock/movements">
                    <i class="fas fa-exchange-alt me-1"></i> Stock Movements
                </a>
            </li>
        </ul>
    </div>
    <div class="card-body">
        <!-- Filters -->
        <div class="row mb-4">
            <div class="col-md-12">
                <div class="card">
                    <div class="card-header bg-light">
                        <h6 class="mb-0">Filter Movements</h6>
                    </div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/stock/movements" method="get" class="row g-3">
                            <div class="col-md-3">
                                <label for="type" class="form-label">Movement Type</label>
                                <select class="form-select" id="type" name="type">
                                    <option value="">All Types</option>
                                    <option value="PURCHASE" ${typeFilter == 'PURCHASE' ? 'selected' : ''}>Purchase</option>
                                    <option value="SALE" ${typeFilter == 'SALE' ? 'selected' : ''}>Sale</option>
                                    <option value="ADJUSTMENT" ${typeFilter == 'ADJUSTMENT' ? 'selected' : ''}>Adjustment</option>
                                    <option value="RETURN" ${typeFilter == 'RETURN' ? 'selected' : ''}>Return</option>
                                    <option value="TRANSFER" ${typeFilter == 'TRANSFER' ? 'selected' : ''}>Transfer</option>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <label for="referenceId" class="form-label">Reference ID</label>
                                <input type="number" class="form-control" id="referenceId" name="referenceId" 
                                       value="${referenceIdFilter}" placeholder="Order/Purchase ID">
                            </div>
                            <div class="col-md-3">
                                <label for="startDate" class="form-label">Start Date</label>
                                <input type="date" class="form-control" id="startDate" name="startDate" value="${startDateFilter}">
                            </div>
                            <div class="col-md-3">
                                <label for="endDate" class="form-label">End Date</label>
                                <input type="date" class="form-control" id="endDate" name="endDate" value="${endDateFilter}">
                            </div>
                            <div class="col-12">
                                <button type="submit" class="btn btn-primary">Apply Filters</button>
                                <a href="${pageContext.request.contextPath}/stock/movements" class="btn btn-outline-secondary">Clear Filters</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        
        <c:choose>
            <c:when test="${empty movements}">
                <div class="alert alert-info">
                    No stock movements found for the selected filters.
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-bordered table-hover" id="movementsTable" width="100%" cellspacing="0">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Product</th>
                                <th>Type</th>
                                <th>Quantity</th>
                                <th>Before</th>
                                <th>After</th>
                                <th>Reference</th>
                                <th>Notes</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="movement" items="${movements}">
                                <tr>
                                    <td><fmt:formatDate value="${movement.timestamp}" pattern="yyyy-MM-dd HH:mm" /></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/products/view/${movement.product.id}">
                                            ${movement.product.name}
                                        </a>
                                    </td>
                                    <td>
                                        <span class="badge bg-${movement.type == 'PURCHASE' ? 'success' : 
                                                             movement.type == 'SALE' ? 'primary' : 
                                                             movement.type == 'ADJUSTMENT' ? 'warning' : 
                                                             movement.type == 'RETURN' ? 'info' : 'secondary'}">
                                            ${movement.type}
                                        </span>
                                    </td>
                                    <td class="${movement.quantityChange > 0 ? 'text-success' : 'text-danger'}">
                                        ${movement.quantityChange > 0 ? '+' : ''}${movement.quantityChange}
                                    </td>
                                    <td>${movement.quantityBefore}</td>
                                    <td>${movement.quantityAfter}</td>
                                    <td>
                                        <c:if test="${not empty movement.referenceId}">
                                            <c:choose>
                                                <c:when test="${movement.type == 'SALE'}">
                                                    <a href="${pageContext.request.contextPath}/orders/view/${movement.referenceId}">
                                                        Order #${movement.referenceId}
                                                    </a>
                                                </c:when>
                                                <c:when test="${movement.type == 'PURCHASE'}">
                                                    Purchase #${movement.referenceId}
                                                </c:when>
                                                <c:otherwise>
                                                    #${movement.referenceId}
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>
                                    </td>
                                    <td>${movement.notes}</td>
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
            $('#movementsTable').DataTable({
                "order": [[ 0, "desc" ]],
                "pageLength": 25
            });
        }
        
        // Date range validation
        const startDateInput = document.getElementById('startDate');
        const endDateInput = document.getElementById('endDate');
        
        if (startDateInput && endDateInput) {
            endDateInput.addEventListener('change', function() {
                if (startDateInput.value && this.value) {
                    const startDate = new Date(startDateInput.value);
                    const endDate = new Date(this.value);
                    
                    if (endDate < startDate) {
                        alert('End date cannot be before start date');
                        this.value = '';
                    }
                }
            });
            
            startDateInput.addEventListener('change', function() {
                if (endDateInput.value && this.value) {
                    const startDate = new Date(this.value);
                    const endDate = new Date(endDateInput.value);
                    
                    if (endDate < startDate) {
                        alert('Start date cannot be after end date');
                        this.value = '';
                    }
                }
            });
        }
    });
</script> 
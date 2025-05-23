<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://scm.com/tags/security" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="pageTitle" value="${not empty title ? title : 'Products'}" />
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1><i class="fas fa-box me-2"></i>${not empty title ? title : 'Products'}</h1>
    <div class="d-flex">
        <security:authorize hasPermission="product:create">
            <a href="${pageContext.request.contextPath}/products/create" class="btn btn-primary me-2">
                <i class="fas fa-plus me-1"></i> Add Product
            </a>
        </security:authorize>
        <div class="dropdown">
            <button class="btn btn-outline-secondary dropdown-toggle" type="button" id="productFilterDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                <i class="fas fa-filter me-1"></i> Filter
            </button>
            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="productFilterDropdown">
                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/products">All Products</a></li>
                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/products/low-stock">Low Stock</a></li>
                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/products/out-of-stock">Out of Stock</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="card shadow mb-4">
    <div class="card-header py-3">
        <form action="${pageContext.request.contextPath}/products" method="get" class="d-flex">
            <div class="input-group">
                <input type="text" class="form-control" placeholder="Search products..." name="search" value="${searchTerm}">
                <button class="btn btn-primary" type="submit">
                    <i class="fas fa-search"></i>
                </button>
            </div>
        </form>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${empty products}">
                <div class="alert alert-info">
                    No products found. 
                    <security:authorize hasPermission="product:create">
                        <a href="${pageContext.request.contextPath}/products/create">Add your first product</a>.
                    </security:authorize>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-bordered table-hover" id="productsTable" width="100%" cellspacing="0">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>SKU</th>
                                <th>Price</th>
                                <th>Stock</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="product" items="${products}">
                                <tr>
                                    <td>${product.id}</td>
                                    <td>${product.name}</td>
                                    <td>${product.sku}</td>
                                    <td><fmt:formatNumber value="${product.unitPrice}" type="currency" currencySymbol="$" /></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${product.stock.quantity == 0}">
                                                <span class="badge bg-danger">Out of Stock</span>
                                            </c:when>
                                            <c:when test="${product.stock.quantity <= product.reorderLevel}">
                                                <span class="badge bg-warning text-dark">${product.stock.quantity} (Low)</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-success">${product.stock.quantity}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="btn-group" role="group">
                                            <a href="${pageContext.request.contextPath}/products/view/${product.id}" class="btn btn-sm btn-info">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <security:authorize hasPermission="product:edit">
                                                <a href="${pageContext.request.contextPath}/products/edit/${product.id}" class="btn btn-sm btn-primary">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                            </security:authorize>
                                            <security:authorize hasPermission="product:delete">
                                                <button type="button" class="btn btn-sm btn-danger" 
                                                        data-bs-toggle="modal" data-bs-target="#deleteModal" 
                                                        data-product-id="${product.id}" 
                                                        data-product-name="${product.name}">
                                                    <i class="fas fa-trash"></i>
                                                </button>
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

<!-- Delete Confirmation Modal -->
<security:authorize hasPermission="product:delete">
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteModalLabel">Confirm Delete</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    Are you sure you want to delete the product <strong id="productName"></strong>?
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <form id="deleteForm" action="${pageContext.request.contextPath}/products/delete/" method="post">
                        <input type="hidden" name="_csrf" value="${pageContext.request.getSession().getAttribute('csrfToken')}" />
                        <button type="submit" class="btn btn-danger">Delete</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</security:authorize>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Handle delete modal
        const deleteModal = document.getElementById('deleteModal');
        if (deleteModal) {
            deleteModal.addEventListener('show.bs.modal', function(event) {
                const button = event.relatedTarget;
                const productId = button.getAttribute('data-product-id');
                const productName = button.getAttribute('data-product-name');
                
                document.getElementById('productName').textContent = productName;
                document.getElementById('deleteForm').action = '${pageContext.request.contextPath}/products/delete/' + productId;
            });
        }
        
        // Initialize DataTable if available
        if ($.fn.DataTable) {
            $('#productsTable').DataTable({
                "order": [[ 0, "desc" ]],
                "pageLength": 25
            });
        }
    });
</script> 
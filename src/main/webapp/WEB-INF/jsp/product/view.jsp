<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://scm.com/tags/security" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="pageTitle" value="Product Details" />
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1><i class="fas fa-box me-2"></i>Product Details</h1>
    <div>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">
            <i class="fas fa-arrow-left me-1"></i> Back to Products
        </a>
        <security:authorize hasPermission="product:edit">
            <a href="${pageContext.request.contextPath}/products/edit/${product.id}" class="btn btn-primary ms-2">
                <i class="fas fa-edit me-1"></i> Edit
            </a>
        </security:authorize>
    </div>
</div>

<div class="row">
    <div class="col-xl-8">
        <!-- Product Details Card -->
        <div class="card shadow mb-4">
            <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                <h6 class="m-0 font-weight-bold text-primary">Product Information</h6>
            </div>
            <div class="card-body">
                <div class="row mb-3">
                    <div class="col-md-3 fw-bold">ID:</div>
                    <div class="col-md-9">${product.id}</div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-3 fw-bold">Name:</div>
                    <div class="col-md-9">${product.name}</div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-3 fw-bold">SKU:</div>
                    <div class="col-md-9">${product.sku}</div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-3 fw-bold">Unit Price:</div>
                    <div class="col-md-9">
                        <fmt:formatNumber value="${product.unitPrice}" type="currency" currencySymbol="$" />
                        <security:authorize hasPermission="product:edit">
                            <button type="button" class="btn btn-sm btn-outline-primary ms-2" 
                                    data-bs-toggle="modal" data-bs-target="#updatePriceModal">
                                <i class="fas fa-edit"></i> Update Price
                            </button>
                        </security:authorize>
                    </div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-3 fw-bold">Description:</div>
                    <div class="col-md-9">
                        <c:choose>
                            <c:when test="${empty product.description}">
                                <span class="text-muted">No description available</span>
                            </c:when>
                            <c:otherwise>
                                ${product.description}
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-3 fw-bold">Created:</div>
                    <div class="col-md-9">
                        <fmt:formatDate value="${product.createdAt}" pattern="yyyy-MM-dd HH:mm" />
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-3 fw-bold">Last Updated:</div>
                    <div class="col-md-9">
                        <fmt:formatDate value="${product.updatedAt}" pattern="yyyy-MM-dd HH:mm" />
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <div class="col-xl-4">
        <!-- Stock Information Card -->
        <div class="card shadow mb-4">
            <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                <h6 class="m-0 font-weight-bold text-primary">Stock Information</h6>
                <security:authorize hasPermission="stock:view">
                    <a href="${pageContext.request.contextPath}/stock/view/${product.id}" class="btn btn-sm btn-info">
                        <i class="fas fa-warehouse me-1"></i> View Stock
                    </a>
                </security:authorize>
            </div>
            <div class="card-body">
                <div class="row mb-3">
                    <div class="col-md-6 fw-bold">Current Stock:</div>
                    <div class="col-md-6">
                        <c:choose>
                            <c:when test="${empty product.stock}">
                                <span class="badge bg-secondary">Not Available</span>
                            </c:when>
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
                    </div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-6 fw-bold">Reorder Level:</div>
                    <div class="col-md-6">
                        ${product.reorderLevel}
                        <security:authorize hasPermission="product:edit">
                            <button type="button" class="btn btn-sm btn-outline-primary ms-2" 
                                    data-bs-toggle="modal" data-bs-target="#updateReorderLevelModal">
                                <i class="fas fa-edit"></i>
                            </button>
                        </security:authorize>
                    </div>
                </div>
                <security:authorize hasPermission="stock:edit">
                    <div class="d-grid gap-2 mt-4">
                        <a href="${pageContext.request.contextPath}/stock/adjust?productId=${product.id}" class="btn btn-primary">
                            <i class="fas fa-plus-minus me-1"></i> Adjust Stock
                        </a>
                    </div>
                </security:authorize>
            </div>
        </div>
        
        <!-- Supplier Information Card -->
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Supplier Information</h6>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty product.suppliers}">
                        <p class="text-center text-muted">No suppliers associated with this product.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="list-group">
                            <c:forEach var="supplierProduct" items="${product.suppliers}">
                                <a href="${pageContext.request.contextPath}/suppliers/view/${supplierProduct.supplier.id}" 
                                   class="list-group-item list-group-item-action">
                                    <div class="d-flex w-100 justify-content-between">
                                        <h6 class="mb-1">${supplierProduct.supplier.name}</h6>
                                        <small>
                                            <fmt:formatNumber value="${supplierProduct.unitCost}" type="currency" currencySymbol="$" />
                                        </small>
                                    </div>
                                    <p class="mb-1">Lead time: ${supplierProduct.leadTimeDays} days</p>
                                    <small class="text-muted">
                                        ${supplierProduct.supplier.email}
                                    </small>
                                </a>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<!-- Update Price Modal -->
<security:authorize hasPermission="product:edit">
    <div class="modal fade" id="updatePriceModal" tabindex="-1" aria-labelledby="updatePriceModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="updatePriceModalLabel">Update Price</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="${pageContext.request.contextPath}/products/update-price/${product.id}" method="post">
                    <div class="modal-body">
                        <input type="hidden" name="_csrf" value="${pageContext.request.getSession().getAttribute('csrfToken')}" />
                        <div class="mb-3">
                            <label for="price" class="form-label">New Price</label>
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="form-control" id="price" name="price" 
                                       value="${product.unitPrice}" min="0" step="0.01" required>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Update Price</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</security:authorize>

<!-- Update Reorder Level Modal -->
<security:authorize hasPermission="product:edit">
    <div class="modal fade" id="updateReorderLevelModal" tabindex="-1" aria-labelledby="updateReorderLevelModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="updateReorderLevelModalLabel">Update Reorder Level</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="${pageContext.request.contextPath}/products/update-reorder-level/${product.id}" method="post">
                    <div class="modal-body">
                        <input type="hidden" name="_csrf" value="${pageContext.request.getSession().getAttribute('csrfToken')}" />
                        <div class="mb-3">
                            <label for="reorderLevel" class="form-label">New Reorder Level</label>
                            <input type="number" class="form-control" id="reorderLevel" name="reorderLevel" 
                                   value="${product.reorderLevel}" min="0" step="1" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Update Reorder Level</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</security:authorize>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" /> 
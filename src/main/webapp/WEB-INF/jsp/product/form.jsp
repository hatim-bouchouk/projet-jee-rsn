<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://scm.com/tags/security" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="pageTitle" value="${empty product ? 'Add Product' : 'Edit Product'}" />
    <jsp:param name="extraHead" value="/WEB-INF/jsp/product/form-head.jsp" />
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1>
        <i class="fas fa-box me-2"></i>
        <c:choose>
            <c:when test="${empty product}">Add Product</c:when>
            <c:otherwise>Edit Product: ${product.name}</c:otherwise>
        </c:choose>
    </h1>
    <div>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">
            <i class="fas fa-arrow-left me-1"></i> Back to Products
        </a>
    </div>
</div>

<div class="card shadow mb-4">
    <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">Product Information</h6>
    </div>
    <div class="card-body">
        <form id="productForm" action="${pageContext.request.contextPath}/products${empty product ? '/create' : '/edit/'.concat(product.id)}" 
              method="post" class="needs-validation" novalidate>
            
            <!-- CSRF Protection -->
            <input type="hidden" name="_csrf" value="${pageContext.request.getSession().getAttribute('csrfToken')}" />
            
            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="name" class="form-label">Product Name <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="name" name="name" 
                           value="${product.name}" required minlength="3" maxlength="100">
                    <div class="invalid-feedback">
                        Please provide a valid product name (3-100 characters).
                    </div>
                </div>
                <div class="col-md-6">
                    <label for="sku" class="form-label">SKU <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="sku" name="sku" 
                           value="${product.sku}" required pattern="[A-Za-z0-9-]+" maxlength="50">
                    <div class="invalid-feedback">
                        Please provide a valid SKU (alphanumeric characters and hyphens only).
                    </div>
                </div>
            </div>
            
            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="unitPrice" class="form-label">Unit Price <span class="text-danger">*</span></label>
                    <div class="input-group">
                        <span class="input-group-text">$</span>
                        <input type="number" class="form-control" id="unitPrice" name="unitPrice" 
                               value="${product.unitPrice}" required min="0" step="0.01">
                        <div class="invalid-feedback">
                            Please provide a valid price (minimum 0).
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <label for="reorderLevel" class="form-label">Reorder Level <span class="text-danger">*</span></label>
                    <input type="number" class="form-control" id="reorderLevel" name="reorderLevel" 
                           value="${product.reorderLevel}" required min="0" step="1">
                    <div class="invalid-feedback">
                        Please provide a valid reorder level (minimum 0).
                    </div>
                </div>
            </div>
            
            <div class="mb-3">
                <label for="description" class="form-label">Description</label>
                <textarea class="form-control" id="description" name="description" rows="5">${product.description}</textarea>
            </div>
            
            <hr class="my-4">
            
            <div class="d-flex justify-content-between">
                <button type="button" class="btn btn-secondary" onclick="window.history.back();">Cancel</button>
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save me-1"></i> ${empty product ? 'Create' : 'Update'} Product
                </button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Form validation
        const form = document.getElementById('productForm');
        
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            
            form.classList.add('was-validated');
        });
        
        // SKU validation
        const skuInput = document.getElementById('sku');
        
        skuInput.addEventListener('input', function() {
            this.value = this.value.replace(/[^A-Za-z0-9-]/g, '').toUpperCase();
        });
        
        // Price formatting
        const priceInput = document.getElementById('unitPrice');
        
        priceInput.addEventListener('blur', function() {
            if (this.value) {
                this.value = parseFloat(this.value).toFixed(2);
            }
        });
    });
</script> 
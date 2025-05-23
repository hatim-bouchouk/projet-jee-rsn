<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://scm.com/tags/security" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="pageTitle" value="Adjust Stock" />
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1><i class="fas fa-plus-minus me-2"></i>Adjust Stock</h1>
    <div>
        <a href="${pageContext.request.contextPath}/stock" class="btn btn-secondary">
            <i class="fas fa-arrow-left me-1"></i> Back to Stock
        </a>
    </div>
</div>

<div class="row">
    <div class="col-lg-8 mx-auto">
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Stock Adjustment Form</h6>
            </div>
            <div class="card-body">
                <form id="adjustStockForm" action="${pageContext.request.contextPath}/stock/adjust" method="post" class="needs-validation" novalidate>
                    <!-- CSRF Protection -->
                    <input type="hidden" name="_csrf" value="${pageContext.request.getSession().getAttribute('csrfToken')}" />
                    
                    <div class="mb-3">
                        <label for="productId" class="form-label">Product <span class="text-danger">*</span></label>
                        <select class="form-select" id="productId" name="productId" required>
                            <option value="">Select Product</option>
                            <c:forEach var="product" items="${products}">
                                <option value="${product.id}" ${param.productId == product.id ? 'selected' : ''}>
                                    ${product.name} (SKU: ${product.sku})
                                </option>
                            </c:forEach>
                        </select>
                        <div class="invalid-feedback">
                            Please select a product.
                        </div>
                    </div>
                    
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="currentStock" class="form-label">Current Stock</label>
                            <input type="text" class="form-control" id="currentStock" readonly>
                        </div>
                        <div class="col-md-6">
                            <label for="quantity" class="form-label">Adjustment Quantity <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <button type="button" class="btn btn-outline-secondary" id="decrementBtn">-</button>
                                <input type="number" class="form-control" id="quantity" name="quantity" required>
                                <button type="button" class="btn btn-outline-secondary" id="incrementBtn">+</button>
                            </div>
                            <div class="form-text">Use positive values for additions, negative for removals.</div>
                            <div class="invalid-feedback">
                                Please enter a valid quantity.
                            </div>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="notes" class="form-label">Notes</label>
                        <textarea class="form-control" id="notes" name="notes" rows="3" placeholder="Reason for adjustment"></textarea>
                    </div>
                    
                    <hr class="my-4">
                    
                    <div class="d-flex justify-content-between">
                        <button type="button" class="btn btn-secondary" onclick="window.history.back();">Cancel</button>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save me-1"></i> Submit Adjustment
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Form validation
        const form = document.getElementById('adjustStockForm');
        
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            
            form.classList.add('was-validated');
        });
        
        // Product selection change
        const productSelect = document.getElementById('productId');
        const currentStockInput = document.getElementById('currentStock');
        
        // Product stock data (would be populated from server in a real app)
        const productStocks = {};
        <c:forEach var="product" items="${products}">
            <c:if test="${not empty product.stock}">
                productStocks[${product.id}] = ${product.stock.quantity};
            </c:if>
            <c:if test="${empty product.stock}">
                productStocks[${product.id}] = 0;
            </c:if>
        </c:forEach>
        
        productSelect.addEventListener('change', function() {
            const productId = this.value;
            if (productId) {
                const stockLevel = productStocks[productId] || 0;
                currentStockInput.value = stockLevel;
            } else {
                currentStockInput.value = '';
            }
        });
        
        // Trigger change event if product is pre-selected
        if (productSelect.value) {
            productSelect.dispatchEvent(new Event('change'));
        }
        
        // Increment/decrement buttons
        const quantityInput = document.getElementById('quantity');
        const incrementBtn = document.getElementById('incrementBtn');
        const decrementBtn = document.getElementById('decrementBtn');
        
        incrementBtn.addEventListener('click', function() {
            const currentVal = parseInt(quantityInput.value) || 0;
            quantityInput.value = currentVal + 1;
        });
        
        decrementBtn.addEventListener('click', function() {
            const currentVal = parseInt(quantityInput.value) || 0;
            quantityInput.value = currentVal - 1;
        });
    });
</script> 
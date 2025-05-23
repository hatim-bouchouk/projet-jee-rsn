<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://scm.com/tags/security" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="pageTitle" value="${empty supplier ? 'Add Supplier' : 'Edit Supplier'}" />
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1>
        <i class="fas fa-truck me-2"></i>
        <c:choose>
            <c:when test="${empty supplier}">Add Supplier</c:when>
            <c:otherwise>Edit Supplier: ${supplier.name}</c:otherwise>
        </c:choose>
    </h1>
    <div>
        <a href="${pageContext.request.contextPath}/suppliers" class="btn btn-secondary">
            <i class="fas fa-arrow-left me-1"></i> Back to Suppliers
        </a>
    </div>
</div>

<div class="card shadow mb-4">
    <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">Supplier Information</h6>
    </div>
    <div class="card-body">
        <form id="supplierForm" action="${pageContext.request.contextPath}/suppliers${empty supplier ? '/create' : '/edit/'.concat(supplier.id)}" 
              method="post" class="needs-validation" novalidate>
            
            <!-- CSRF Protection -->
            <input type="hidden" name="_csrf" value="${pageContext.request.getSession().getAttribute('csrfToken')}" />
            
            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="name" class="form-label">Supplier Name <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="name" name="name" 
                           value="${supplier.name}" required minlength="3" maxlength="100">
                    <div class="invalid-feedback">
                        Please provide a valid supplier name (3-100 characters).
                    </div>
                </div>
                <div class="col-md-6">
                    <label for="contactPerson" class="form-label">Contact Person</label>
                    <input type="text" class="form-control" id="contactPerson" name="contactPerson" 
                           value="${supplier.contactPerson}" maxlength="100">
                </div>
            </div>
            
            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                    <input type="email" class="form-control" id="email" name="email" 
                           value="${supplier.email}" required maxlength="100">
                    <div class="invalid-feedback">
                        Please provide a valid email address.
                    </div>
                </div>
                <div class="col-md-6">
                    <label for="phone" class="form-label">Phone Number</label>
                    <input type="tel" class="form-control" id="phone" name="phone" 
                           value="${supplier.phone}" pattern="[0-9+\-\s()]+" maxlength="20">
                    <div class="invalid-feedback">
                        Please provide a valid phone number.
                    </div>
                </div>
            </div>
            
            <div class="mb-3">
                <label for="address" class="form-label">Address</label>
                <textarea class="form-control" id="address" name="address" rows="3">${supplier.address}</textarea>
            </div>
            
            <hr class="my-4">
            
            <div class="d-flex justify-content-between">
                <button type="button" class="btn btn-secondary" onclick="window.history.back();">Cancel</button>
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save me-1"></i> ${empty supplier ? 'Create' : 'Update'} Supplier
                </button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Form validation
        const form = document.getElementById('supplierForm');
        
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            
            form.classList.add('was-validated');
        });
        
        // Phone number formatting
        const phoneInput = document.getElementById('phone');
        
        phoneInput.addEventListener('input', function() {
            // Allow only numbers, +, -, spaces, and parentheses
            this.value = this.value.replace(/[^\d+\-\s()]/g, '');
        });
        
        // Email validation
        const emailInput = document.getElementById('email');
        
        emailInput.addEventListener('blur', function() {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (this.value && !emailRegex.test(this.value)) {
                this.setCustomValidity('Please enter a valid email address');
            } else {
                this.setCustomValidity('');
            }
        });
    });
</script> 
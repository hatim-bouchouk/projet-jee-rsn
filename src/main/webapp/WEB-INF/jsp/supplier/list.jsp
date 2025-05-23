<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://scm.com/tags/security" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="pageTitle" value="Suppliers" />
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1><i class="fas fa-truck me-2"></i>Suppliers</h1>
    <security:authorize hasPermission="supplier:create">
        <a href="${pageContext.request.contextPath}/suppliers/create" class="btn btn-primary">
            <i class="fas fa-plus me-1"></i> Add Supplier
        </a>
    </security:authorize>
</div>

<div class="card shadow mb-4">
    <div class="card-header py-3">
        <form action="${pageContext.request.contextPath}/suppliers" method="get" class="d-flex">
            <div class="input-group">
                <input type="text" class="form-control" placeholder="Search suppliers..." name="search" value="${searchTerm}">
                <button class="btn btn-primary" type="submit">
                    <i class="fas fa-search"></i>
                </button>
            </div>
        </form>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${empty suppliers}">
                <div class="alert alert-info">
                    No suppliers found. 
                    <security:authorize hasPermission="supplier:create">
                        <a href="${pageContext.request.contextPath}/suppliers/create">Add your first supplier</a>.
                    </security:authorize>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-bordered table-hover" id="suppliersTable" width="100%" cellspacing="0">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Contact Person</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>Products</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="supplier" items="${suppliers}">
                                <tr>
                                    <td>${supplier.id}</td>
                                    <td>${supplier.name}</td>
                                    <td>${supplier.contactPerson}</td>
                                    <td>
                                        <a href="mailto:${supplier.email}">${supplier.email}</a>
                                    </td>
                                    <td>${supplier.phone}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${empty supplier.products}">
                                                <span class="badge bg-secondary">0</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-info">${fn:length(supplier.products)}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="btn-group" role="group">
                                            <a href="${pageContext.request.contextPath}/suppliers/view/${supplier.id}" class="btn btn-sm btn-info">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <security:authorize hasPermission="supplier:edit">
                                                <a href="${pageContext.request.contextPath}/suppliers/edit/${supplier.id}" class="btn btn-sm btn-primary">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                            </security:authorize>
                                            <security:authorize hasPermission="supplier:delete">
                                                <button type="button" class="btn btn-sm btn-danger" 
                                                        data-bs-toggle="modal" data-bs-target="#deleteModal" 
                                                        data-supplier-id="${supplier.id}" 
                                                        data-supplier-name="${supplier.name}">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </security:authorize>
                                            <a href="${pageContext.request.contextPath}/suppliers/products/${supplier.id}" class="btn btn-sm btn-success">
                                                <i class="fas fa-boxes"></i>
                                            </a>
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
<security:authorize hasPermission="supplier:delete">
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteModalLabel">Confirm Delete</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>Are you sure you want to delete the supplier <strong id="supplierName"></strong>?</p>
                    <p class="text-danger">This will also remove all product associations with this supplier.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <form id="deleteForm" action="${pageContext.request.contextPath}/suppliers/delete/" method="post">
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
                const supplierId = button.getAttribute('data-supplier-id');
                const supplierName = button.getAttribute('data-supplier-name');
                
                document.getElementById('supplierName').textContent = supplierName;
                document.getElementById('deleteForm').action = '${pageContext.request.contextPath}/suppliers/delete/' + supplierId;
            });
        }
        
        // Initialize DataTable if available
        if ($.fn.DataTable) {
            $('#suppliersTable').DataTable({
                "order": [[ 0, "desc" ]],
                "pageLength": 25
            });
        }
    });
</script> 
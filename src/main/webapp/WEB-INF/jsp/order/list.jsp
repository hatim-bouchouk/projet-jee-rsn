<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://scm.com/tags/security" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="pageTitle" value="Orders" />
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1><i class="fas fa-shopping-cart me-2"></i>Orders</h1>
    <security:authorize hasPermission="order:create">
        <a href="${pageContext.request.contextPath}/orders/create" class="btn btn-primary">
            <i class="fas fa-plus me-1"></i> New Order
        </a>
    </security:authorize>
</div>

<div class="card shadow mb-4">
    <div class="card-header py-3">
        <ul class="nav nav-pills">
            <li class="nav-item">
                <a class="nav-link ${empty statusFilter ? 'active' : ''}" href="${pageContext.request.contextPath}/orders">All</a>
            </li>
            <c:forEach var="status" items="${['NEW', 'PROCESSING', 'PENDING_PAYMENT', 'SHIPPED', 'DELIVERED', 'COMPLETED', 'CANCELLED']}">
                <li class="nav-item">
                    <a class="nav-link ${statusFilter == status ? 'active' : ''}" 
                       href="${pageContext.request.contextPath}/orders?status=${status}">
                        <span class="badge bg-${status == 'NEW' ? 'primary' : 
                                             status == 'PROCESSING' ? 'info' : 
                                             status == 'PENDING_PAYMENT' ? 'warning' : 
                                             status == 'SHIPPED' ? 'secondary' : 
                                             status == 'DELIVERED' ? 'success' : 
                                             status == 'COMPLETED' ? 'success' : 'danger'}">
                            ${status}
                        </span>
                    </a>
                </li>
            </c:forEach>
        </ul>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${empty orders}">
                <div class="alert alert-info">
                    No orders found. 
                    <security:authorize hasPermission="order:create">
                        <a href="${pageContext.request.contextPath}/orders/create">Create a new order</a>.
                    </security:authorize>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-bordered table-hover" id="ordersTable" width="100%" cellspacing="0">
                        <thead>
                            <tr>
                                <th>Order #</th>
                                <th>Customer</th>
                                <th>Date</th>
                                <th>Status</th>
                                <th>Total</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="order" items="${orders}">
                                <tr>
                                    <td>${order.id}</td>
                                    <td>${order.customerName}</td>
                                    <td><fmt:formatDate value="${order.createdAt}" pattern="yyyy-MM-dd" /></td>
                                    <td>
                                        <span class="badge bg-${order.status == 'NEW' ? 'primary' : 
                                                             order.status == 'PROCESSING' ? 'info' : 
                                                             order.status == 'PENDING_PAYMENT' ? 'warning' : 
                                                             order.status == 'SHIPPED' ? 'secondary' : 
                                                             order.status == 'DELIVERED' ? 'success' : 
                                                             order.status == 'COMPLETED' ? 'success' : 'danger'}">
                                            ${order.status}
                                        </span>
                                    </td>
                                    <td>
                                        <fmt:formatNumber value="${order.total}" type="currency" currencySymbol="$" />
                                    </td>
                                    <td>
                                        <div class="btn-group" role="group">
                                            <a href="${pageContext.request.contextPath}/orders/view/${order.id}" class="btn btn-sm btn-info">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <security:authorize hasPermission="order:edit">
                                                <c:if test="${order.status != 'COMPLETED' && order.status != 'CANCELLED'}">
                                                    <a href="${pageContext.request.contextPath}/orders/edit/${order.id}" class="btn btn-sm btn-primary">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                </c:if>
                                            </security:authorize>
                                            <security:authorize hasPermission="order:edit">
                                                <c:if test="${order.status != 'CANCELLED'}">
                                                    <button type="button" class="btn btn-sm btn-danger" 
                                                            data-bs-toggle="modal" data-bs-target="#cancelModal" 
                                                            data-order-id="${order.id}">
                                                        <i class="fas fa-times"></i>
                                                    </button>
                                                </c:if>
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

<!-- Cancel Order Modal -->
<security:authorize hasPermission="order:edit">
    <div class="modal fade" id="cancelModal" tabindex="-1" aria-labelledby="cancelModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="cancelModalLabel">Confirm Cancellation</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>Are you sure you want to cancel this order?</p>
                    <p class="text-danger">This action cannot be undone.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <form id="cancelForm" action="${pageContext.request.contextPath}/orders/cancel/" method="post">
                        <input type="hidden" name="_csrf" value="${pageContext.request.getSession().getAttribute('csrfToken')}" />
                        <button type="submit" class="btn btn-danger">Cancel Order</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</security:authorize>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Handle cancel modal
        const cancelModal = document.getElementById('cancelModal');
        if (cancelModal) {
            cancelModal.addEventListener('show.bs.modal', function(event) {
                const button = event.relatedTarget;
                const orderId = button.getAttribute('data-order-id');
                
                document.getElementById('cancelForm').action = '${pageContext.request.contextPath}/orders/cancel/' + orderId;
            });
        }
        
        // Initialize DataTable if available
        if ($.fn.DataTable) {
            $('#ordersTable').DataTable({
                "order": [[ 0, "desc" ]],
                "pageLength": 25
            });
        }
    });
</script> 
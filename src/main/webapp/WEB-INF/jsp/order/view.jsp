<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://scm.com/tags/security" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="pageTitle" value="Order Details" />
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1><i class="fas fa-shopping-cart me-2"></i>Order #${order.id}</h1>
    <div>
        <a href="${pageContext.request.contextPath}/orders" class="btn btn-secondary">
            <i class="fas fa-arrow-left me-1"></i> Back to Orders
        </a>
        <security:authorize hasPermission="order:edit">
            <c:if test="${order.status != 'COMPLETED' && order.status != 'CANCELLED'}">
                <a href="${pageContext.request.contextPath}/orders/edit/${order.id}" class="btn btn-primary ms-2">
                    <i class="fas fa-edit me-1"></i> Edit
                </a>
            </c:if>
        </security:authorize>
    </div>
</div>

<div class="row">
    <div class="col-xl-8">
        <!-- Order Items Card -->
        <div class="card shadow mb-4">
            <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                <h6 class="m-0 font-weight-bold text-primary">Order Items</h6>
                <security:authorize hasPermission="order:edit">
                    <c:if test="${order.status == 'NEW' || order.status == 'PROCESSING'}">
                        <a href="${pageContext.request.contextPath}/orders/add-item/${order.id}" class="btn btn-sm btn-primary">
                            <i class="fas fa-plus me-1"></i> Add Item
                        </a>
                    </c:if>
                </security:authorize>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty order.items}">
                        <div class="alert alert-info">
                            No items in this order.
                            <security:authorize hasPermission="order:edit">
                                <c:if test="${order.status == 'NEW' || order.status == 'PROCESSING'}">
                                    <a href="${pageContext.request.contextPath}/orders/add-item/${order.id}">Add an item</a>.
                                </c:if>
                            </security:authorize>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-bordered">
                                <thead>
                                    <tr>
                                        <th>Product</th>
                                        <th>SKU</th>
                                        <th>Unit Price</th>
                                        <th>Quantity</th>
                                        <th>Subtotal</th>
                                        <security:authorize hasPermission="order:edit">
                                            <c:if test="${order.status == 'NEW' || order.status == 'PROCESSING'}">
                                                <th>Actions</th>
                                            </c:if>
                                        </security:authorize>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${order.items}">
                                        <tr>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/products/view/${item.product.id}">
                                                    ${item.product.name}
                                                </a>
                                            </td>
                                            <td>${item.product.sku}</td>
                                            <td><fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="$" /></td>
                                            <td>${item.quantity}</td>
                                            <td><fmt:formatNumber value="${item.unitPrice * item.quantity}" type="currency" currencySymbol="$" /></td>
                                            <security:authorize hasPermission="order:edit">
                                                <c:if test="${order.status == 'NEW' || order.status == 'PROCESSING'}">
                                                    <td>
                                                        <form action="${pageContext.request.contextPath}/orders/remove-item/${item.id}" method="post" 
                                                              onsubmit="return confirm('Are you sure you want to remove this item?');">
                                                            <input type="hidden" name="_csrf" value="${pageContext.request.getSession().getAttribute('csrfToken')}" />
                                                            <input type="hidden" name="orderId" value="${order.id}" />
                                                            <button type="submit" class="btn btn-sm btn-danger">
                                                                <i class="fas fa-trash"></i>
                                                            </button>
                                                        </form>
                                                    </td>
                                                </c:if>
                                            </security:authorize>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                                <tfoot>
                                    <tr>
                                        <th colspan="4" class="text-end">Total:</th>
                                        <th><fmt:formatNumber value="${order.total}" type="currency" currencySymbol="$" /></th>
                                        <security:authorize hasPermission="order:edit">
                                            <c:if test="${order.status == 'NEW' || order.status == 'PROCESSING'}">
                                                <th></th>
                                            </c:if>
                                        </security:authorize>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    
    <div class="col-xl-4">
        <!-- Order Status Card -->
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Order Status</h6>
            </div>
            <div class="card-body">
                <div class="mb-3">
                    <span class="badge bg-${order.status == 'NEW' ? 'primary' : 
                                         order.status == 'PROCESSING' ? 'info' : 
                                         order.status == 'PENDING_PAYMENT' ? 'warning' : 
                                         order.status == 'SHIPPED' ? 'secondary' : 
                                         order.status == 'DELIVERED' ? 'success' : 
                                         order.status == 'COMPLETED' ? 'success' : 'danger'} fs-6 w-100 py-2">
                        ${order.status}
                    </span>
                </div>
                
                <security:authorize hasPermission="order:edit">
                    <c:if test="${order.status != 'COMPLETED' && order.status != 'CANCELLED'}">
                        <form action="${pageContext.request.contextPath}/orders/update-status/${order.id}" method="post" class="mb-3">
                            <input type="hidden" name="_csrf" value="${pageContext.request.getSession().getAttribute('csrfToken')}" />
                            <div class="mb-3">
                                <label for="status" class="form-label">Update Status</label>
                                <select class="form-select" id="status" name="status" required>
                                    <option value="">Select Status</option>
                                    <c:forEach var="status" items="${['NEW', 'PROCESSING', 'PENDING_PAYMENT', 'SHIPPED', 'DELIVERED', 'COMPLETED']}">
                                        <option value="${status}" ${order.status == status ? 'selected' : ''}>${status}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="d-grid">
                                <button type="submit" class="btn btn-primary">Update Status</button>
                            </div>
                        </form>
                        
                        <hr>
                        
                        <c:if test="${order.status == 'PENDING_PAYMENT'}">
                            <button type="button" class="btn btn-success w-100 mb-3" data-bs-toggle="modal" data-bs-target="#paymentModal">
                                <i class="fas fa-credit-card me-1"></i> Process Payment
                            </button>
                        </c:if>
                        
                        <c:if test="${order.status == 'PROCESSING' || order.status == 'PENDING_PAYMENT'}">
                            <button type="button" class="btn btn-info w-100 mb-3" data-bs-toggle="modal" data-bs-target="#shipmentModal">
                                <i class="fas fa-shipping-fast me-1"></i> Process Shipment
                            </button>
                        </c:if>
                        
                        <c:if test="${order.status != 'CANCELLED'}">
                            <button type="button" class="btn btn-danger w-100" data-bs-toggle="modal" data-bs-target="#cancelModal">
                                <i class="fas fa-times me-1"></i> Cancel Order
                            </button>
                        </c:if>
                    </c:if>
                </security:authorize>
            </div>
        </div>
        
        <!-- Customer Information Card -->
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Customer Information</h6>
            </div>
            <div class="card-body">
                <div class="row mb-2">
                    <div class="col-md-4 fw-bold">Name:</div>
                    <div class="col-md-8">${order.customerName}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-md-4 fw-bold">Email:</div>
                    <div class="col-md-8">
                        <a href="mailto:${order.customerEmail}">${order.customerEmail}</a>
                    </div>
                </div>
                <div class="row mb-2">
                    <div class="col-md-4 fw-bold">Phone:</div>
                    <div class="col-md-8">${order.customerPhone}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-md-4 fw-bold">Shipping:</div>
                    <div class="col-md-8">${order.shippingAddress}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-md-4 fw-bold">Created:</div>
                    <div class="col-md-8">
                        <fmt:formatDate value="${order.createdAt}" pattern="yyyy-MM-dd HH:mm" />
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-4 fw-bold">Updated:</div>
                    <div class="col-md-8">
                        <fmt:formatDate value="${order.updatedAt}" pattern="yyyy-MM-dd HH:mm" />
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Order Notes Card -->
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Order Notes</h6>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty order.notes}">
                        <p class="text-muted">No notes for this order.</p>
                    </c:when>
                    <c:otherwise>
                        <p>${order.notes}</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<!-- Payment Modal -->
<security:authorize hasPermission="order:edit">
    <div class="modal fade" id="paymentModal" tabindex="-1" aria-labelledby="paymentModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="paymentModalLabel">Process Payment</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="${pageContext.request.contextPath}/orders/process-payment/${order.id}" method="post">
                    <div class="modal-body">
                        <input type="hidden" name="_csrf" value="${pageContext.request.getSession().getAttribute('csrfToken')}" />
                        <div class="mb-3">
                            <label for="paymentMethod" class="form-label">Payment Method</label>
                            <select class="form-select" id="paymentMethod" name="paymentMethod" required>
                                <option value="">Select Payment Method</option>
                                <option value="CREDIT_CARD">Credit Card</option>
                                <option value="BANK_TRANSFER">Bank Transfer</option>
                                <option value="PAYPAL">PayPal</option>
                                <option value="CASH">Cash</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="paymentReference" class="form-label">Payment Reference</label>
                            <input type="text" class="form-control" id="paymentReference" name="paymentReference" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Process Payment</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</security:authorize>

<!-- Shipment Modal -->
<security:authorize hasPermission="order:edit">
    <div class="modal fade" id="shipmentModal" tabindex="-1" aria-labelledby="shipmentModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="shipmentModalLabel">Process Shipment</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="${pageContext.request.contextPath}/orders/process-shipment/${order.id}" method="post">
                    <div class="modal-body">
                        <input type="hidden" name="_csrf" value="${pageContext.request.getSession().getAttribute('csrfToken')}" />
                        <div class="mb-3">
                            <label for="carrier" class="form-label">Shipping Carrier</label>
                            <select class="form-select" id="carrier" name="carrier" required>
                                <option value="">Select Carrier</option>
                                <option value="UPS">UPS</option>
                                <option value="FedEx">FedEx</option>
                                <option value="USPS">USPS</option>
                                <option value="DHL">DHL</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="trackingNumber" class="form-label">Tracking Number</label>
                            <input type="text" class="form-control" id="trackingNumber" name="trackingNumber" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Process Shipment</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</security:authorize>

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
                    <form action="${pageContext.request.contextPath}/orders/cancel/${order.id}" method="post">
                        <input type="hidden" name="_csrf" value="${pageContext.request.getSession().getAttribute('csrfToken')}" />
                        <button type="submit" class="btn btn-danger">Cancel Order</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</security:authorize>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" /> 
package com.scm.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

/**
 * Entity class for supplier orders in the Supply Chain Management system.
 */
@Entity
@Table(name = "supplier_orders")
public class SupplierOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Enum for supplier order status.
     */
    public enum Status {
        pending, placed, confirmed, shipped, delivered, cancelled, RECEIVED, COMPLETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Supplier is required")
    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.pending;

    @DecimalMin(value = "0.00", message = "Total amount cannot be negative")
    @Column(name = "total_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "expected_delivery")
    private LocalDate expectedDelivery;

    // Relationships
    @OneToMany(mappedBy = "supplierOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplierOrderItem> orderItems = new ArrayList<>();

    /**
     * Default constructor
     */
    public SupplierOrder() {
        this.orderDate = LocalDateTime.now();
    }

    /**
     * Constructor with essential fields
     */
    public SupplierOrder(Supplier supplier) {
        this.supplier = supplier;
        this.orderDate = LocalDateTime.now();
        this.status = Status.pending;
        this.totalAmount = BigDecimal.ZERO;
    }

    /**
     * Full constructor
     */
    public SupplierOrder(Supplier supplier, Status status, BigDecimal totalAmount, LocalDate expectedDelivery) {
        this.supplier = supplier;
        this.orderDate = LocalDateTime.now();
        this.status = status;
        this.totalAmount = totalAmount;
        this.expectedDelivery = expectedDelivery;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getExpectedDelivery() {
        return expectedDelivery;
    }

    public void setExpectedDelivery(LocalDate expectedDelivery) {
        this.expectedDelivery = expectedDelivery;
    }

    public List<SupplierOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<SupplierOrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    /**
     * Add an item to the order
     * @param item The order item to add
     */
    public void addOrderItem(SupplierOrderItem item) {
        orderItems.add(item);
        item.setSupplierOrder(this);
        recalculateTotalAmount();
    }

    /**
     * Remove an item from the order
     * @param item The order item to remove
     */
    public void removeOrderItem(SupplierOrderItem item) {
        orderItems.remove(item);
        item.setSupplierOrder(null);
        recalculateTotalAmount();
    }

    /**
     * Recalculate total amount based on order items
     */
    public void recalculateTotalAmount() {
        this.totalAmount = orderItems.stream()
                .map(item -> item.getUnitCost().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Check if the order has been delivered
     * @return true if status is delivered
     */
    public boolean isDelivered() {
        return status == Status.delivered;
    }

    /**
     * Check if the order is overdue
     * @return true if expected delivery date is in the past and order is not delivered
     */
    public boolean isOverdue() {
        return expectedDelivery != null && 
               expectedDelivery.isBefore(LocalDate.now()) && 
               !isDelivered();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupplierOrder that = (SupplierOrder) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SupplierOrder{" +
                "id=" + id +
                ", supplier=" + (supplier != null ? supplier.getName() : "null") +
                ", orderDate=" + orderDate +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", expectedDelivery=" + expectedDelivery +
                ", itemCount=" + (orderItems != null ? orderItems.size() : 0) +
                '}';
    }
} 
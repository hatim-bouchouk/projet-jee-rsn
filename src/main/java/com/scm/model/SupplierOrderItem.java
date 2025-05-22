package com.scm.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Entity class for supplier order items in the Supply Chain Management system.
 */
@Entity
@Table(name = "supplier_order_items", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"supplier_order_id", "product_id"}, name = "uk_supplier_order_items_order_product")
})
public class SupplierOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Supplier order is required")
    @ManyToOne
    @JoinColumn(name = "supplier_order_id", nullable = false)
    private SupplierOrder supplierOrder;

    @NotNull(message = "Product is required")
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull(message = "Unit cost is required")
    @DecimalMin(value = "0.01", message = "Unit cost must be greater than 0")
    @Column(name = "unit_cost", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitCost;

    /**
     * Default constructor
     */
    public SupplierOrderItem() {
    }

    /**
     * Constructor with essential fields
     */
    public SupplierOrderItem(SupplierOrder supplierOrder, Product product, Integer quantity) {
        this.supplierOrder = supplierOrder;
        this.product = product;
        this.quantity = quantity;
        
        // Try to get the unit cost from the supplier product relationship
        this.unitCost = findSupplierProductCost(supplierOrder, product);
    }

    /**
     * Full constructor
     */
    public SupplierOrderItem(SupplierOrder supplierOrder, Product product, Integer quantity, BigDecimal unitCost) {
        this.supplierOrder = supplierOrder;
        this.product = product;
        this.quantity = quantity;
        this.unitCost = unitCost;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SupplierOrder getSupplierOrder() {
        return supplierOrder;
    }

    public void setSupplierOrder(SupplierOrder supplierOrder) {
        this.supplierOrder = supplierOrder;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null && supplierOrder != null && this.unitCost == null) {
            this.unitCost = findSupplierProductCost(supplierOrder, product);
        }
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    /**
     * Calculate the total cost for this line item
     * @return the total cost
     */
    public BigDecimal getLineTotal() {
        return unitCost.multiply(new BigDecimal(quantity));
    }

    /**
     * Helper method to find the unit cost from supplier product relationship
     * @param supplierOrder the supplier order
     * @param product the product
     * @return the unit cost from supplier product or a default value
     */
    private BigDecimal findSupplierProductCost(SupplierOrder supplierOrder, Product product) {
        if (supplierOrder != null && supplierOrder.getSupplier() != null && product != null) {
            // Find the supplier product relationship
            for (SupplierProduct sp : product.getSupplierProducts()) {
                if (sp.getSupplier().equals(supplierOrder.getSupplier())) {
                    return sp.getUnitCost();
                }
            }
        }
        // Default to product unit price if not found
        return product != null ? product.getUnitPrice() : new BigDecimal("0.00");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupplierOrderItem that = (SupplierOrderItem) o;
        return Objects.equals(id, that.id) ||
               (supplierOrder != null && product != null &&
                Objects.equals(supplierOrder, that.supplierOrder) &&
                Objects.equals(product, that.product));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, supplierOrder, product);
    }

    @Override
    public String toString() {
        return "SupplierOrderItem{" +
                "id=" + id +
                ", supplierOrder=" + (supplierOrder != null ? supplierOrder.getId() : "null") +
                ", product=" + (product != null ? product.getSku() : "null") +
                ", quantity=" + quantity +
                ", unitCost=" + unitCost +
                '}';
    }
} 
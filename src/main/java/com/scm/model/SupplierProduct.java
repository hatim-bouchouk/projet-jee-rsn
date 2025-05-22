package com.scm.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Entity class for supplier products in the Supply Chain Management system.
 * This is a junction table for the many-to-many relationship between
 * Supplier and Product.
 */
@Entity
@Table(name = "supplier_products")
@IdClass(SupplierProductId.class)
public class SupplierProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "Unit cost is required")
    @DecimalMin(value = "0.01", message = "Unit cost must be greater than 0")
    @Column(name = "unit_cost", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitCost;

    @NotNull(message = "Lead time days is required")
    @Min(value = 1, message = "Lead time must be at least 1 day")
    @Column(name = "lead_time_days", nullable = false)
    private Integer leadTimeDays = 1;

    /**
     * Default constructor
     */
    public SupplierProduct() {
    }

    /**
     * Constructor with essential fields
     */
    public SupplierProduct(Supplier supplier, Product product, BigDecimal unitCost) {
        this.supplier = supplier;
        this.product = product;
        this.unitCost = unitCost;
        this.leadTimeDays = 1;
    }

    /**
     * Full constructor
     */
    public SupplierProduct(Supplier supplier, Product product, BigDecimal unitCost, Integer leadTimeDays) {
        this.supplier = supplier;
        this.product = product;
        this.unitCost = unitCost;
        this.leadTimeDays = leadTimeDays;
    }

    // Getters and Setters

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public Integer getLeadTimeDays() {
        return leadTimeDays;
    }

    public void setLeadTimeDays(Integer leadTimeDays) {
        this.leadTimeDays = leadTimeDays;
    }

    /**
     * Calculate profit margin percentage based on product's selling price
     * @return profit margin as a percentage
     */
    public BigDecimal getProfitMarginPercent() {
        if (product == null || product.getUnitPrice() == null || unitCost == null || unitCost.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal profit = product.getUnitPrice().subtract(unitCost);
        return profit.multiply(new BigDecimal("100")).divide(product.getUnitPrice(), 2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupplierProduct that = (SupplierProduct) o;
        return Objects.equals(supplier, that.supplier) &&
               Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(supplier, product);
    }

    @Override
    public String toString() {
        return "SupplierProduct{" +
                "supplier=" + (supplier != null ? supplier.getName() : "null") +
                ", product=" + (product != null ? product.getSku() : "null") +
                ", unitCost=" + unitCost +
                ", leadTimeDays=" + leadTimeDays +
                '}';
    }
} 
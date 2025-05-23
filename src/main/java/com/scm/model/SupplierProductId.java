package com.scm.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key class for SupplierProduct entity.
 * This class represents the composite key consisting of supplier_id and product_id.
 */
public class SupplierProductId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer supplier;
    private Integer product;

    /**
     * Default constructor
     */
    public SupplierProductId() {
    }

    /**
     * Constructor with key fields
     */
    public SupplierProductId(Integer supplier, Integer product) {
        this.supplier = supplier;
        this.product = product;
    }

    // Getters and Setters

    public Integer getSupplier() {
        return supplier;
    }

    public void setSupplier(Integer supplier) {
        this.supplier = supplier;
    }

    public Integer getProduct() {
        return product;
    }

    public void setProduct(Integer product) {
        this.product = product;
    }
    
    public void setSupplierId(Integer supplierId) {
        this.supplier = supplierId;
    }
    
    public void setProductId(Integer productId) {
        this.product = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupplierProductId that = (SupplierProductId) o;
        return Objects.equals(supplier, that.supplier) &&
               Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(supplier, product);
    }

    @Override
    public String toString() {
        return "SupplierProductId{" +
                "supplier=" + supplier +
                ", product=" + product +
                '}';
    }
} 
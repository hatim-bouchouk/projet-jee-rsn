package com.scm.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Entity class for stock movements in the Supply Chain Management system.
 */
@Entity
@Table(name = "stock_movements")
public class StockMovement implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Enum for movement types.
     */
    public enum MovementType {
        purchase, sale, adjustment, return_item, waste, ADJUSTMENT, CUSTOMER_ORDER, SUPPLIER_ORDER;

        // To handle the database enum value 'return' which is a Java keyword
        public String getValue() {
            return this == return_item ? "return" : this.name();
        }

        // Factory method to create from database value
        public static MovementType fromValue(String value) {
            if ("return".equals(value)) {
                return return_item;
            }
            return MovementType.valueOf(value);
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Product is required")
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "Movement type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    @NotNull(message = "Quantity is required")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "reference_id")
    private Integer referenceId;

    @Column(name = "movement_date", nullable = false, updatable = false)
    private LocalDateTime movementDate;
    
    @Column(name = "notes")
    private String notes;

    /**
     * Default constructor
     */
    public StockMovement() {
        this.movementDate = LocalDateTime.now();
    }

    /**
     * Constructor with essential fields
     */
    public StockMovement(Product product, MovementType movementType, Integer quantity) {
        this.product = product;
        this.movementType = movementType;
        this.quantity = quantity;
        this.movementDate = LocalDateTime.now();
    }

    /**
     * Full constructor
     */
    public StockMovement(Product product, MovementType movementType, Integer quantity, Integer referenceId) {
        this.product = product;
        this.movementType = movementType;
        this.quantity = quantity;
        this.referenceId = referenceId;
        this.movementDate = LocalDateTime.now();
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getMovementDate() {
        return movementDate;
    }

    public void setMovementDate(LocalDateTime movementDate) {
        this.movementDate = movementDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Check if this movement is incoming (increases stock)
     * @return true for purchase and return, false otherwise
     */
    public boolean isIncoming() {
        return movementType == MovementType.purchase || 
               movementType == MovementType.return_item;
    }

    /**
     * Get the actual quantity change for stock calculations
     * @return positive number for incoming, negative for outgoing
     */
    public Integer getStockChange() {
        if (isIncoming()) {
            return Math.abs(quantity);
        } else {
            return -Math.abs(quantity);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockMovement that = (StockMovement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "StockMovement{" +
                "id=" + id +
                ", product=" + (product != null ? product.getSku() : "null") +
                ", movementType=" + movementType +
                ", quantity=" + quantity +
                ", referenceId=" + referenceId +
                ", movementDate=" + movementDate +
                ", notes=" + notes +
                '}';
    }
} 
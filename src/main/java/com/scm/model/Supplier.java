package com.scm.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Entity class for suppliers in the Supply Chain Management system.
 */
@Entity
@Table(name = "suppliers")
public class Supplier implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Supplier name is required")
    @Size(max = 100, message = "Supplier name must be less than 100 characters")
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Size(max = 100, message = "Contact person name must be less than 100 characters")
    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9\\+\\-\\(\\) ]{5,20}$", message = "Phone number must be valid")
    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    @NotBlank(message = "Address is required")
    @Column(name = "address", columnDefinition = "TEXT", nullable = false)
    private String address;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplierProduct> supplierProducts = new ArrayList<>();

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    private List<SupplierOrder> supplierOrders = new ArrayList<>();

    /**
     * Default constructor
     */
    public Supplier() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Constructor with essential fields
     */
    public Supplier(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Full constructor
     */
    public Supplier(String name, String contactPerson, String email, String phone, String address) {
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<SupplierProduct> getSupplierProducts() {
        return supplierProducts;
    }

    public void setSupplierProducts(List<SupplierProduct> supplierProducts) {
        this.supplierProducts = supplierProducts;
    }

    public void addSupplierProduct(SupplierProduct supplierProduct) {
        this.supplierProducts.add(supplierProduct);
        supplierProduct.setSupplier(this);
    }

    public void removeSupplierProduct(SupplierProduct supplierProduct) {
        this.supplierProducts.remove(supplierProduct);
        supplierProduct.setSupplier(null);
    }

    public List<SupplierOrder> getSupplierOrders() {
        return supplierOrders;
    }

    public void setSupplierOrders(List<SupplierOrder> supplierOrders) {
        this.supplierOrders = supplierOrders;
    }

    public void addSupplierOrder(SupplierOrder supplierOrder) {
        this.supplierOrders.add(supplierOrder);
        supplierOrder.setSupplier(this);
    }

    public void removeSupplierOrder(SupplierOrder supplierOrder) {
        this.supplierOrders.remove(supplierOrder);
        supplierOrder.setSupplier(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Supplier supplier = (Supplier) o;
        return Objects.equals(id, supplier.id) ||
               (email != null && Objects.equals(email, supplier.email));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
} 
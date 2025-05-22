# Supply Chain Management System - Comprehensive Documentation

## Project Overview

This Supply Chain Management (SCM) System is a Java Enterprise Edition (JEE) web application designed to manage and optimize supply chain operations for businesses. The system provides end-to-end functionality for inventory management, supplier relationships, order processing, and stock movement tracking.

## System Architecture

The application follows a typical multi-tier JEE architecture:

1. **Presentation Layer**: JSP pages with CSS/JavaScript for the user interface
2. **Controller Layer**: Servlets that handle HTTP requests and responses
3. **Service Layer**: EJBs implementing business logic and transaction management
4. **Persistence Layer**: JPA entities and DAOs for database operations
5. **Database Layer**: MySQL relational database

## Technology Stack

- **Java EE 8**: Core development platform
- **Servlet API 4.0**: Web components
- **JSP & JSTL**: View templates
- **JPA 2.2**: Object-relational mapping
- **EJB 3.2**: Business logic and transaction management
- **MySQL 8.0**: Relational database
- **HikariCP**: High-performance JDBC connection pool
- **Maven**: Dependency management and build automation

## Database Design

The database consists of 10 interconnected tables designed to handle all aspects of supply chain management:

### Core Entities
- **users**: System users with role-based permissions
- **products**: Product catalog with inventory management details
- **suppliers**: Information about product suppliers
- **stock**: Current inventory levels for products

### Relationship Entities
- **supplier_products**: Many-to-many relationship between suppliers and products

### Transaction Entities
- **customer_orders**: Orders placed by customers
- **order_items**: Line items for customer orders
- **supplier_orders**: Purchase orders placed with suppliers
- **supplier_order_items**: Line items for supplier purchase orders
- **stock_movements**: Tracking of all inventory changes

### Key Relationships
- One-to-One: Products and Stock (each product has one stock record)
- One-to-Many: Customer Orders and Order Items, Supplier Orders and Supplier Order Items
- Many-to-Many: Suppliers and Products (through supplier_products junction table)

## Data Access Layer

The application implements a robust data access layer using the DAO (Data Access Object) pattern with JPA:

### Generic DAO Interface

The system uses a generic DAO interface that provides standard CRUD operations for all entities:

```java
public interface GenericDao<T, ID extends Serializable> {
    T save(T entity);
    T update(T entity);
    T saveOrUpdate(T entity);
    void delete(T entity);
    boolean deleteById(ID id);
    Optional<T> findById(ID id);
    List<T> findAll();
    List<T> findAll(int startPosition, int maxResults);
    long count();
    boolean exists(ID id);
    void flush();
    void clear();
}
```

### Entity-Specific DAOs

Each entity has its own DAO interface that extends the generic DAO and adds entity-specific query methods:

- **UserDao**: Methods for user authentication, role-based queries
- **ProductDao**: Methods for catalog management and inventory queries
- **SupplierDao**: Methods for supplier management
- **StockDao**: Methods for inventory level management
- **CustomerOrderDao**: Methods for customer order processing
- **SupplierOrderDao**: Methods for purchase order management
- **StockMovementDao**: Methods for tracking inventory changes

### DAO Implementation

The DAO implementations use JPA's EntityManager for database operations:

```java
@Stateless
public class AbstractJpaDao<T, ID extends Serializable> implements GenericDao<T, ID> {
    @PersistenceContext
    protected EntityManager entityManager;
    
    // Implementation of GenericDao methods...
}
```

### Transaction Management

The application uses both container-managed and programmatic transactions:

- EJB-based services use container-managed transactions (CMT)
- Complex operations use programmatic transaction management via a CDI interceptor

## Model Layer (JPA Entities)

The model layer consists of JPA entities that map to database tables:

### User Entity

```java
@Entity
@Table(name = "users")
public class User implements Serializable {
    public enum Role { ADMIN, MANAGER, USER }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors, getters, setters...
}
```

### Product Entity

```java
@Entity
@Table(name = "products")
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(unique = true, nullable = false)
    private String sku;
    
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;
    
    @Column(name = "reorder_level")
    private Integer reorderLevel;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Stock stock;
    
    @OneToMany(mappedBy = "product")
    private Set<SupplierProduct> supplierProducts = new HashSet<>();
    
    // Constructors, getters, setters...
}
```

### Other Key Entities

Similarly structured entities exist for all database tables, with appropriate JPA annotations for relationships, validations, and lifecycle callbacks.

## Service Layer

The service layer implements business logic and coordinates operations across multiple DAOs:

### Service Interfaces

- **UserService**: User management and authentication
- **ProductService**: Product catalog and inventory management
- **SupplierService**: Supplier management and product sourcing
- **OrderService**: Order processing for both customer and supplier orders
- **InventoryService**: Stock level management and movement tracking
- **ReportService**: Analytical reports and business intelligence

### Service Implementation

Service implementations are EJBs that inject DAOs and provide transaction management:

```java
@Stateless
public class ProductServiceImpl implements ProductService {
    @Inject
    private ProductDao productDao;
    
    @Inject
    private StockDao stockDao;
    
    @Inject
    private SupplierProductDao supplierProductDao;
    
    // Implementation of business methods...
}
```

## Controller Layer

The controller layer consists of servlets that handle HTTP requests:

- **AuthenticationServlet**: Handles login/logout
- **ProductController**: Manages product catalog operations
- **SupplierController**: Manages supplier operations
- **OrderController**: Processes customer orders
- **PurchaseOrderController**: Manages supplier orders
- **InventoryController**: Handles inventory operations
- **ReportController**: Generates reports

## Presentation Layer

The presentation layer uses JSP with JSTL, along with CSS and JavaScript:

### Key JSP Pages

- Login/authentication pages
- Dashboard with key metrics
- Product management screens
- Supplier management screens
- Order processing workflows
- Inventory management screens
- Reporting and analytics dashboards

## Security Features

The application implements several security features:

- Role-based access control (ADMIN, MANAGER, USER)
- Form-based authentication
- Password hashing with bcrypt
- Input validation and sanitization
- Protection against common web vulnerabilities (XSS, CSRF)

## Main Functionality

### Inventory Management
- Track stock levels in real-time
- Automatic alerts for low stock
- Record all stock movements with audit trail
- Barcode/SKU-based operations

### Supplier Management
- Maintain supplier catalog and contact information
- Track supplier performance metrics
- Compare pricing across suppliers
- Manage supplier contracts and terms

### Order Processing
- Customer order entry and tracking
- Order fulfillment workflow
- Backorder management
- Order status notifications

### Purchasing
- Generate purchase orders based on stock levels
- Supplier order tracking
- Goods receipt processing
- Invoice matching

### Reporting
- Inventory valuation reports
- Stock movement analysis
- Order history and status reports
- Supplier performance reports

## Deployment Guide

### System Requirements
- JDK 11 or higher
- Maven 3.6 or higher
- MySQL 8.0 or compatible database
- Java EE 8 compatible application server (WildFly, GlassFish, or TomEE)

### Database Setup
1. Create a MySQL database named `scm_db`
2. Create a user `scm_user` with password `scm_password`
3. Run the database setup scripts:
   - `schema.sql` - Creates database schema
   - `sample_data.sql` - (Optional) Loads sample data

### Application Deployment
1. Configure database connection in `persistence.xml`
2. Build the application:
   ```bash
   mvn clean package
   ```
3. Deploy the WAR file to your application server
4. Access the application at `http://localhost:8080/scm`

## Future Enhancements

1. RESTful API for integration with other systems
2. Mobile application for warehouse operations
3. Advanced analytics and forecasting
4. EDI integration with suppliers
5. Barcode/QR code scanning functionality

## Contributors

- Development Team
- Project Stakeholders
- QA Team

## License

This project is licensed under the MIT License. 
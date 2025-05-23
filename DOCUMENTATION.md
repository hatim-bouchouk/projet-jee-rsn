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
    public enum Role { ADMIN, MANAGER, CLIENT }
    
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

The service layer implements business logic using EJB stateless session beans with transaction management:

### Service Interfaces

- **UserService**: User management and authentication
- **ProductService**: Product catalog and inventory management
- **SupplierService**: Supplier management and product sourcing
- **OrderService**: Order processing for both customer and supplier orders
- **StockService**: Stock level management and movement tracking
- **DashboardService**: Analytical reports and business intelligence

### Service Exceptions

The service layer includes a structured exception hierarchy:

```java
// Base exception for all service layer exceptions
public class ServiceException extends Exception {
    public ServiceException(String message) { super(message); }
    public ServiceException(String message, Throwable cause) { super(message, cause); }
}

// Specific exception for validation errors
public class ValidationException extends ServiceException {
    private Map<String, String> validationErrors;
    
    public ValidationException(String message) { super(message); }
    public ValidationException(String field, String message) {
        super("Validation error");
        this.validationErrors = new HashMap<>();
        this.validationErrors.put(field, message);
    }
}

// Specific exception for authentication errors
public class AuthenticationException extends ServiceException {
    public AuthenticationException(String message) { super(message); }
}
```

### Service Implementation

Service implementations are EJBs that inject DAOs and provide transaction management:

```java
@Stateless
public class ProductServiceImpl implements ProductService {
    @Inject
    private ProductDao productDao;
    
    @Inject
    private StockDao stockDao;
    
    @Transactional
    @Override
    public Product createProduct(Product product) throws ValidationException {
        // Input validation
        validateProduct(product);
        
        // Business logic
        product.setCreatedAt(LocalDateTime.now());
        
        // Persistence
        Product savedProduct = productDao.save(product);
        
        // Initialize stock if needed
        if (savedProduct.getStock() == null) {
            Stock stock = new Stock();
            stock.setProduct(savedProduct);
            stock.setQuantity(0);
            stockDao.save(stock);
        }
        
        return savedProduct;
    }
    
    // Other business methods...
}
```

### Key Service Features

1. **Transaction Management**: 
   - Container-managed transactions with @Transactional annotations
   - Support for transaction rollback on exceptions

2. **Input Validation**:
   - Comprehensive validation of all service inputs
   - Detailed validation error reporting

3. **Exception Handling**:
   - Structured exception hierarchy
   - Meaningful exception messages for client feedback

4. **Security Integration**:
   - Authorization checks for sensitive operations
   - Auditing and logging of critical actions

5. **Business Logic**:
   - Implementation of complex business rules
   - Coordination across multiple related entities

## Controller Layer

The controller layer consists of servlets that handle HTTP requests and security filters:

### Core Controllers
- **ProductController**: Manages product catalog operations
- **SupplierController**: Manages supplier operations
- **OrderController**: Processes customer orders
- **PurchaseOrderController**: Manages supplier orders
- **StockController**: Handles inventory operations
- **DashboardController**: Presents system metrics and analytics

### Security Controllers
- **LoginServlet**: Handles user authentication with form-based login
- **LogoutServlet**: Manages secure user logout
- **AccessDeniedServlet**: Customized handling of unauthorized access attempts

### Security Filters
- **AuthenticationFilter**: Intercepts requests to enforce authentication and role-based access control
- **CsrfFilter**: Protects against Cross-Site Request Forgery by validating tokens on state-changing requests

### Common Controller Features
- Input validation with appropriate error handling
- Transaction management via service layer
- Response formatting for different view types (HTML, JSON)
- Error handling with user-friendly messages
- Audit logging for critical operations

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

The application implements a comprehensive security framework:

### Authentication and Authorization
- **Role-based access control** with three roles: ADMIN, MANAGER, CLIENT
- **Permission-based authorization** with granular permissions (e.g., product:view, product:edit)
- **Form-based authentication** with secure session management
- **Password hashing** with BCrypt for secure credential storage
- **Session management** with security features such as session timeout and protection against session fixation
- **Access control filters** for protecting application resources
- **CSRF protection** for preventing cross-site request forgery attacks

### Security Components

#### User Principal
A security model representing authenticated users with role and permission information:
```java
public class UserPrincipal implements Serializable {
    private Integer id;
    private String username;
    private String email;
    private Set<String> roles;
    private Set<String> permissions;
    private LocalDateTime lastActivity;
    private String sessionId;
    
    // Methods for role and permission checks
    public boolean hasRole(String role) { ... }
    public boolean hasPermission(String permission) { ... }
    public boolean hasAnyRole(String... roleList) { ... }
}
```

#### Session Manager
Handles secure session management for authenticated users:
```java
public class SessionManager {
    // Create a new session for authenticated user
    public static UserPrincipal createSession(HttpServletRequest request, 
        HttpServletResponse response, User user) { ... }
    
    // Get current authenticated user
    public static UserPrincipal getCurrentUser(HttpServletRequest request) { ... }
    
    // Validate CSRF token
    public static boolean validateCsrfToken(HttpServletRequest request, String token) { ... }
}
```

#### Authentication Filter
Servlet filter for authentication and authorization:
```java
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {
    // Intercept requests and validate user authentication and authorization
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) { ... }
}
```

#### CSRF Protection
Implements Cross-Site Request Forgery protection:
```java
@WebFilter(filterName = "CsrfFilter", urlPatterns = {"/*"})
public class CsrfFilter implements Filter {
    // Validate CSRF token for state-changing requests
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) { ... }
}
```

#### Custom JSP Security Tag
Tag library for conditional rendering based on user roles and permissions:
```xml
<security:authorize hasRole="ADMIN">
    <!-- Only visible to admins -->
</security:authorize>

<security:authorize hasPermission="product:edit">
    <!-- Only visible to users with product:edit permission -->
</security:authorize>
```

#### Login and Access Control Servlets
- **LoginServlet**: Handles user authentication
- **LogoutServlet**: Handles user logout
- **AccessDeniedServlet**: Handles unauthorized access attempts

### Security Best Practices
- Input validation and sanitization to prevent injection attacks
- Protection against common web vulnerabilities (XSS, CSRF, Session Hijacking)
- Secure password policies and storage
- Audit logging for security events
- Session timeout for inactive users
- Secure HTTP response headers

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

## Troubleshooting Guide

### Common Issues and Solutions

#### Authentication Filter Issues
The application uses a security filter (`AuthenticationFilter`) that controls access to protected resources. If users are experiencing 404 errors or redirect loops:

1. Check if the login page exists and is accessible
2. Verify that public resources are correctly configured in the `PUBLIC_PATHS` set
3. For debugging purposes, the filter can be temporarily disabled by modifying the `doFilter` method:
   ```java
   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
           throws IOException, ServletException {
       
       // Temporarily allow all requests to pass through without authentication
       chain.doFilter(request, response);
       
       /* Original code commented out for debugging */
   }
   ```

#### Database Connection Issues
If the application fails to connect to the database:

1. Verify database credentials in `database.properties` and `persistence.xml`
2. Ensure MySQL service is running
3. Check for connection pool configuration issues
4. Look for ClassCastException errors related to HikariCP configuration (common issue with integer vs string values)

#### Hibernate Configuration Issues
Common Hibernate issues include:

1. **ClassCastException**: When using HikariCP with Hibernate, ensure all connection pool parameters are provided as strings:
   ```java
   // Correct way to configure HikariCP in JPAUtil
   properties.put("hibernate.hikari.minimumIdle", 
           String.valueOf(DatabaseConfig.getIntProperty("db.pool.minIdle", 5)));
   ```

2. **Missing Cache Provider**: If you see errors related to missing cache providers like EhCache:
   ```
   Caused by: javax.cache.CacheException: Failed to load the CachingProvider [org.ehcache.jsr107.EhcacheCachingProvider]
   ```
   Either add the missing dependency or disable second-level caching in `persistence.xml`:
   ```xml
   <property name="hibernate.cache.use_second_level_cache" value="false" />
   <property name="hibernate.cache.use_query_cache" value="false" />
   ```

#### Tomcat Deployment Issues
When deploying to Tomcat:

1. Ensure Tomcat is configured with the correct Java version
2. Check port configuration in `server.xml` (default is 8080, but can be changed to 8090 or other)
3. Verify that the WAR file is correctly deployed to the `webapps` directory
4. Check Tomcat logs for startup errors:
   - `catalina.out` - Main Tomcat log
   - `localhost.log` - Application-specific errors

### Diagnostic Process

When troubleshooting the application, follow this systematic approach:

1. **Check Tomcat Status**:
   ```powershell
   netstat -ano | findstr "8090"  # Check if Tomcat is listening on port 8090
   ```

2. **Verify MySQL Connection**:
   ```powershell
   Get-Service -Name "*mysql*"  # Check if MySQL service is running
   ```

3. **Review Application Logs**:
   - Tomcat logs in `[TOMCAT_HOME]/logs/`
   - Application-specific logs in `[TOMCAT_HOME]/logs/scm/`

4. **Test Database Connection**:
   - Use a database client to verify connection with the same credentials
   - Check for database schema issues or missing tables

5. **Verify Application Deployment**:
   - Check if the application is deployed in `[TOMCAT_HOME]/webapps/scm/`
   - Verify that all required resources are present

6. **Browser Access Test**:
   ```powershell
   Invoke-WebRequest -Uri "http://localhost:8090/scm" -UseBasicParsing
   ```

### Environment-Specific Configuration

The application supports different environments (development, test, production) through property files:

- `application.properties` - Common settings
- `application-development.properties` - Development-specific settings
- `application-test.properties` - Test environment settings
- `application-production.properties` - Production settings
- `database.properties` - Database connection settings

To change environments, modify the `spring.profiles.active` property in `application.properties`:
```properties
spring.profiles.active=development
```

### Recovery Steps

If the application fails to start:

1. Stop Tomcat and any running Java processes:
   ```powershell
   taskkill /F /IM java.exe /T
   ```

2. Clear Tomcat work directory:
   ```powershell
   Remove-Item -Path "[TOMCAT_HOME]/work/Catalina" -Recurse -Force
   ```

3. Rebuild and redeploy the application:
   ```powershell
   mvn clean package
   Copy-Item -Path "target/scm.war" -Destination "[TOMCAT_HOME]/webapps/" -Force
   ```

4. Start Tomcat with clean environment:
   ```powershell
   cd "[TOMCAT_HOME]/bin"
   .\startup.bat
   ```

5. Monitor logs for errors:
   ```powershell
   Get-Content -Path "[TOMCAT_HOME]/logs/catalina.out" -Wait
   ``` 
# Supply Chain Management System

A Java Enterprise Edition (JEE) web application for managing supply chain operations.

## Project Structure

The project follows a standard Maven JEE project structure:

```
supply-chain-management/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── scm/
│   │   │           ├── controller/    # Servlet controllers
│   │   │           ├── model/         # Entity classes
│   │   │           ├── service/       # Business logic
│   │   │           ├── dao/           # Data access objects
│   │   │           └── util/          # Utility classes
│   │   ├── resources/
│   │   │   ├── database/              # Database scripts and documentation
│   │   │   │   └── init_data.sql      # Sample data initialization script
│   │   │   └── META-INF/
│   │   │       └── persistence.xml    # JPA configuration
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── web.xml            # Web application config
│   │       ├── resources/
│   │       │   ├── css/               # CSS stylesheets
│   │       │   ├── js/                # JavaScript files
│   │       │   └── images/            # Image assets
│   │       ├── jsp/
│   │       │   ├── common/            # Common JSP pages
│   │       │   ├── admin/             # Admin JSP pages
│   │       │   ├── inventory/         # Inventory JSP pages
│   │       │   ├── supplier/          # Supplier JSP pages
│   │       │   ├── order/             # Order JSP pages
│   │       │   └── reports/           # Reports JSP pages
│   │       └── index.jsp              # Main entry point
├── pom.xml                            # Maven configuration
└── README.md                          # This file
```

## Technologies Used

- Java EE 8
- Servlet API 4.0
- JSP & JSTL
- JPA with Hibernate for database persistence
- EJB for business logic
- MySQL as the database
- HikariCP for connection pooling
- Spring Security for password hashing (BCrypt)
- Maven for dependency management

## System Requirements

- JDK 11 or higher
- Maven 3.6 or higher
- MySQL 8.0 or compatible database
- Java EE 8 compatible application server (e.g., Tomcat, WildFly, GlassFish, or TomEE)

## Setup Instructions

1. **Clone the repository**

2. **Configure database**
   - Create a MySQL database named `scm_db`
   - Create a user `scm_user` with password `scm_password` (or update the persistence.xml with your credentials)
   - Grant the user all privileges on the `scm_db` database

3. **Build the project**
   ```bash
   mvn clean package
   ```

4. **Deploy the application**
   - Copy the generated WAR file from `target/scm.war` to your application server's deployment directory
   - For Tomcat: Copy to `TOMCAT_HOME/webapps/`

5. **Access the application**
   - Open a web browser and navigate to `http://localhost:8080/scm`

## Features

- **Inventory Management**
  - Track product stock levels
  - Automatic alerts for low stock items
  - Record all stock movements (purchases, sales, adjustments)

- **Supplier Management**
  - Maintain supplier information and contacts
  - Track supplier-product relationships
  - Compare supplier costs and lead times

- **Order Processing**
  - Customer order management with status tracking
  - Supplier purchase order creation and monitoring
  - Order history and reporting

- **Reporting and Analytics**
  - Inventory level reports
  - Order status reports
  - Supplier performance metrics

- **User Authentication and Authorization**
  - Role-based access control
  - Secure password storage using BCrypt

## Database Schema

The SCM database consists of 10 interconnected tables:

1. **users** - System users with authentication and role information
2. **products** - Product catalog with pricing and inventory management details
3. **suppliers** - Information about product suppliers
4. **stock** - Current inventory levels for products
5. **supplier_products** - Many-to-many relationship between suppliers and products they provide
6. **customer_orders** - Orders placed by customers
7. **order_items** - Line items for customer orders
8. **supplier_orders** - Purchase orders placed with suppliers
9. **supplier_order_items** - Line items for supplier purchase orders
10. **stock_movements** - Tracking of all inventory changes

## Sample Data

The application includes a sample dataset for testing and development purposes. This data is automatically loaded when the application starts if no existing data is found.

### Default Users

| Username | Password   | Role    | Email             |
|----------|------------|---------|-------------------|
| admin    | admin123   | admin   | admin@scm.com     |
| manager  | manager123 | manager | manager@scm.com   |
| client   | client123  | user    | client@scm.com    |

**Note:** Passwords are stored using BCrypt hashing for security.

### Sample Products and Inventory

The sample data includes 10 products (laptops, monitors, accessories, etc.) with varying stock levels. Some products are intentionally set below their reorder points to demonstrate the low stock alert functionality.

### Sample Orders

The dataset includes:
- 5 customer orders in different statuses (new, pending, processing, shipped, delivered)
- 4 supplier orders in different statuses (pending, shipped, delivered)
- Various stock movements representing purchases, sales, returns, and adjustments

## Database Initialization

The database is initialized automatically when the application starts through the `DatabaseInitializer` class, which:

1. Checks if database initialization is enabled in the configuration
2. Creates/updates the database schema using JPA/Hibernate
3. Checks if sample data initialization is enabled
4. If no users exist in the database, executes the `init_data.sql` script to populate sample data

## Security

The application implements form-based authentication with the following roles:
- `admin`: Full access to all features
- `manager`: Access to reports and operational features
- `user`: Limited access to basic operations

User passwords are hashed using BCrypt, which:
1. Incorporates a salt to protect against rainbow table attacks
2. Is an adaptive function that can be made slower as hardware gets faster
3. Is widely recognized as a secure password hashing algorithm

## License

This project is licensed under the MIT License.

## Contact

For any inquiries, please contact the development team. 
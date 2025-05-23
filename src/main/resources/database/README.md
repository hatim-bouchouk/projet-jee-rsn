# Supply Chain Management Database Documentation

This document provides comprehensive information about the database schema for the Supply Chain Management (SCM) system.

## Database Schema Overview

The SCM database consists of 10 interconnected tables designed to manage all aspects of a supply chain system:

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

## Entity Relationship Diagram

```
                                 +---------------+
                                 |     users     |
                                 +---------------+
                                 | id            |
                                 | username      |
                                 | password      |
                                 | role          |
                                 | email         |
                                 | created_at    |
                                 +---------------+
                                         
                                 +---------------+
                                 |   products    |
                                 +---------------+
                                 | id            |
                                 | name          |
                                 | description   |
                                 | sku           |
                                 | unit_price    |
                                 | reorder_level |
                                 | created_at    |
                                 +---------------+
                                        ^
                                        |
      +----------------+     +----------+------------+     +--------------------+
      |   suppliers    |     |        stock         |     | stock_movements     |
      +----------------+     +---------------------+      +--------------------+
      | id             |     | id                  |      | id                 |
      | name           |     | product_id (FK)     |      | product_id (FK)    |
      | contact_person |     | quantity_available  |      | movement_type      |
      | email          |     | last_updated        |      | quantity           |
      | phone          |     +---------------------+      | reference_id       |
      | address        |                                  | movement_date      |
      | created_at     |                                  +--------------------+
      +----------------+          
             ^                         +---------------------+
             |                         |   customer_orders   |
             |                         +---------------------+
             |                         | id                  |
             |                         | customer_name       |
             |                         | customer_email      |
     +---------------+                 | order_date          |
     | supplier_     |                 | status              |
     | products      |                 | total_amount        |
     +---------------+                 | order_date          |
     | supplier_id(FK|                 | status              |
     | product_id(FK)|                 | total_amount        |
     | unit_cost     |                 +--------------------+
     | lead_time_days|                        ^
     +---------------+                        |
             ^                         +--------------------+
             |                         |     order_items    |
             |                         +--------------------+
   +--------------------+              | id                 |
   |  supplier_orders   |              | order_id (FK)      |
   +--------------------+              | product_id (FK)    |
   | id                 |              | quantity           |
   | supplier_id (FK)   |              | unit_price         |
   | order_date         |              +--------------------+
   | status             |
   | total_amount       |
   | expected_delivery  |
   +--------------------+
             ^
             |
   +----------------------+
   | supplier_order_items |
   +----------------------+
   | id                   |
   | supplier_order_id(FK)|
   | product_id (FK)      |
   | quantity             |
   | unit_cost            |
   +----------------------+
```

## Table Details

### users
Stores information about system users with role-based access control.
- Primary Key: `id`
- Unique Constraints: `username`, `email`
- Indexes: `idx_users_username`, `idx_users_email`, `idx_users_role`

### products
Contains the product catalog with pricing information.
- Primary Key: `id`
- Unique Constraints: `sku`
- Indexes: `idx_products_sku`, `idx_products_name`

### suppliers
Stores information about product suppliers.
- Primary Key: `id`
- Unique Constraints: `email`
- Indexes: `idx_suppliers_email`, `idx_suppliers_name`, `idx_suppliers_phone`

### stock
Tracks current inventory levels for each product.
- Primary Key: `id`
- Foreign Keys: `product_id` references `products(id)`
- Unique Constraints: one stock record per product
- Indexes: `idx_stock_product`

### supplier_products
Manages the many-to-many relationship between suppliers and products.
- Composite Primary Key: (`supplier_id`, `product_id`)
- Foreign Keys: 
  - `supplier_id` references `suppliers(id)`
  - `product_id` references `products(id)`
- Indexes: `idx_supplier_products_product`, `idx_supplier_products_supplier`

### customer_orders
Records orders placed by customers.
- Primary Key: `id`
- Indexes: `idx_customer_orders_customer_email`, `idx_customer_orders_order_date`, `idx_customer_orders_status`

### order_items
Contains line items for customer orders.
- Primary Key: `id`
- Foreign Keys:
  - `order_id` references `customer_orders(id)`
  - `product_id` references `products(id)`
- Unique Constraints: one line item per product per order
- Indexes: `idx_order_items_order_id`, `idx_order_items_product_id`

### supplier_orders
Records purchase orders placed with suppliers.
- Primary Key: `id`
- Foreign Keys: `supplier_id` references `suppliers(id)`
- Indexes: `idx_supplier_orders_supplier_id`, `idx_supplier_orders_order_date`, `idx_supplier_orders_status`, `idx_supplier_orders_expected_delivery`

### supplier_order_items
Contains line items for supplier purchase orders.
- Primary Key: `id`
- Foreign Keys:
  - `supplier_order_id` references `supplier_orders(id)`
  - `product_id` references `products(id)`
- Unique Constraints: one line item per product per supplier order
- Indexes: `idx_supplier_order_items_order_id`, `idx_supplier_order_items_product_id`

### stock_movements
Tracks all inventory movements (purchases, sales, adjustments, returns, waste).
- Primary Key: `id`
- Foreign Keys: `product_id` references `products(id)`
- Indexes: `idx_stock_movements_product_id`, `idx_stock_movements_movement_type`, `idx_stock_movements_movement_date`, `idx_stock_movements_reference_id`

## Database Setup

### 1. Prerequisites
- MySQL 8.0 or later
- User with database creation privileges

### 2. Creating the Database

```sql
CREATE DATABASE scm_db;
CREATE USER 'scm_user'@'localhost' IDENTIFIED BY 'scm_password';
GRANT ALL PRIVILEGES ON scm_db.* TO 'scm_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Running the Schema Scripts

The database schema is automatically created by JPA/Hibernate when the application starts. The `DatabaseInitializer` class handles this process.

## Sample Data

The `init_data.sql` file contains sample data to populate the database with test records. This includes:

### Users

Three default user accounts are created with the following credentials:

| Username | Password   | Role    | Email             |
|----------|------------|---------|-------------------|
| admin    | admin123   | admin   | admin@scm.com     |
| manager  | manager123 | manager | manager@scm.com   |
| client   | client123  | user    | client@scm.com    |

**Note:** The passwords are stored in BCrypt hashed format for security. The plaintext passwords shown above are only for development and testing purposes.

### Products

Ten sample products are created with different stock levels:
- Some products are intentionally set with stock levels below their reorder points to test low stock alerts
- Products include electronics and accessories with various prices and reorder levels

### Suppliers

Four sample suppliers are created with contact information and addresses.

### Supplier-Product Relationships

The sample data establishes relationships between suppliers and products, including:
- Multiple suppliers for some products to test alternate sourcing
- Different lead times and costs for each supplier-product relationship

### Customer Orders

Five sample customer orders are created with different statuses:
- Delivered orders
- Shipped orders
- Processing orders
- Pending orders
- New orders

### Supplier Orders

Four sample supplier orders are created with different statuses:
- Delivered orders
- Shipped orders
- Pending orders

### Stock Movements

Various stock movement records are created to demonstrate different inventory transactions:
- Purchases from suppliers
- Sales to customers
- Inventory adjustments
- Customer returns
- Damaged/waste items

## Password Hashing

User passwords are hashed using BCrypt, a secure password hashing algorithm. The hashed passwords in the sample data are:

- admin:admin123 = `$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS`
- manager:manager123 = `$2a$10$FKdvC4NZ5/3hx.A.Z.yfO.KeUuV.crs5g4eAoR.I6NQnoGiIUCnxy`
- client:client123 = `$2a$10$UYrGNY9bT2TQC8A.Ql8jR.Xf0aYLxFG4KgbBy5GqB.NJA1CV1nKZ2`

BCrypt is used because it:
1. Incorporates a salt to protect against rainbow table attacks
2. Is an adaptive function that can be made slower as hardware gets faster
3. Is widely recognized as a secure password hashing algorithm

## Database Initialization Process

The database is initialized automatically when the application starts through the `DatabaseInitializer` class, which:

1. Checks if database initialization is enabled in the configuration
2. Creates/updates the database schema using JPA/Hibernate
3. Checks if sample data initialization is enabled
4. If no users exist in the database, executes the `init_data.sql` script to populate sample data

This process ensures that the application has the necessary database structure and sample data for testing and development.

## Data Relationships and Business Rules

1. **Inventory Management**:
   - Each product has exactly one stock record
   - Stock quantities are updated through stock_movements
   - Products have reorder levels to indicate when new stock should be ordered

2. **Supplier Management**:
   - Multiple suppliers can provide the same product at different costs
   - Lead time is tracked for each product-supplier relationship
   - Supplier orders trigger stock movements when delivered

3. **Order Processing**:
   - Customer orders decrease available stock
   - Supplier orders increase available stock when received
   - Order statuses are tracked through the order lifecycle

4. **Stock Movement Tracking**:
   - All inventory changes (positive or negative) are recorded
   - Movement types categorize the reason for the change
   - Reference IDs can link movements to specific orders

## Database Maintenance

### Indexes
The schema includes indexes on frequently queried columns to improve performance:
- Foreign key columns
- Columns commonly used in WHERE clauses (status, email, dates)
- Columns used for sorting or grouping

### Recommended Regular Maintenance
- Backup the database daily
- Check for orphaned records monthly
- Analyze table statistics quarterly
- Consider archiving old orders and movements after 2 years

## Security Considerations
- User passwords are stored using bcrypt hashing
- Database user permissions should be restricted based on application needs
- Consider encrypting sensitive supplier and customer information 

# Database Initialization and Sample Data

This directory contains SQL scripts and resources for initializing the Supply Chain Management (SCM) database.

## Schema Creation

The `schema.sql` file contains the SQL statements to create all necessary tables, indexes, and constraints for the SCM system. This includes:

- Users table for authentication and authorization
- Products table for inventory management
- Suppliers table for vendor information
- Stock table for current inventory levels
- Customer Orders and Order Items tables for sales tracking
- Supplier Orders and Supplier Order Items tables for purchase tracking
- Stock Movements table for inventory transaction history
- Supplier Products table for product-supplier relationships

## Sample Data

The `init_data.sql` file contains sample data to populate the database with test records. This includes:

### Users

Three default user accounts are created with the following credentials:

| Username | Password   | Role    | Email             |
|----------|------------|---------|-------------------|
| admin    | admin123   | admin   | admin@scm.com     |
| manager  | manager123 | manager | manager@scm.com   |
| client   | client123  | user    | client@scm.com    |

**Note:** The passwords are stored in BCrypt hashed format for security. The plaintext passwords shown above are only for development and testing purposes.

### Products

Ten sample products are created with different stock levels:
- Some products are intentionally set with stock levels below their reorder points to test low stock alerts
- Products include electronics and accessories with various prices and reorder levels

### Suppliers

Four sample suppliers are created with contact information and addresses.

### Supplier-Product Relationships

The sample data establishes relationships between suppliers and products, including:
- Multiple suppliers for some products to test alternate sourcing
- Different lead times and costs for each supplier-product relationship

### Customer Orders

Five sample customer orders are created with different statuses:
- Delivered orders
- Shipped orders
- Processing orders
- Pending orders
- New orders

### Supplier Orders

Four sample supplier orders are created with different statuses:
- Delivered orders
- Shipped orders
- Pending orders

### Stock Movements

Various stock movement records are created to demonstrate different inventory transactions:
- Purchases from suppliers
- Sales to customers
- Inventory adjustments
- Customer returns
- Damaged/waste items

## Password Hashing

User passwords are hashed using BCrypt, a secure password hashing algorithm. The hashed passwords in the sample data are:

- admin:admin123 = `$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS`
- manager:manager123 = `$2a$10$FKdvC4NZ5/3hx.A.Z.yfO.KeUuV.crs5g4eAoR.I6NQnoGiIUCnxy`
- client:client123 = `$2a$10$UYrGNY9bT2TQC8A.Ql8jR.Xf0aYLxFG4KgbBy5GqB.NJA1CV1nKZ2`

BCrypt is used because it:
1. Incorporates a salt to protect against rainbow table attacks
2. Is an adaptive function that can be made slower as hardware gets faster
3. Is widely recognized as a secure password hashing algorithm

## Database Initialization Process

The database is initialized automatically when the application starts through the `DatabaseInitializer` class, which:

1. Checks if database initialization is enabled in the configuration
2. Creates/updates the database schema using JPA/Hibernate
3. Checks if sample data initialization is enabled
4. If no users exist in the database, executes the `init_data.sql` script to populate sample data

This process ensures that the application has the necessary database structure and sample data for testing and development. 
# Supply Chain Management Database Documentation

## Overview

This document provides comprehensive information about the database schema for the Supply Chain Management (SCM) system. The database is designed to support all aspects of supply chain operations, including:

- User management and authentication
- Product catalog management
- Supplier relationship management
- Inventory tracking
- Customer order processing
- Supplier order management
- Stock movement tracking

## Database Schema

The SCM database consists of 10 interconnected tables designed to manage all aspects of a supply chain system. Below is a detailed description of each table and its relationships.

### Tables Overview

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

### Key Business Flows

1. **Inventory Management**
   - Products are defined in the `products` table with reorder levels
   - Current inventory levels are tracked in the `stock` table
   - All changes to inventory are recorded in the `stock_movements` table
   - Low stock alerts can be generated based on reorder levels

2. **Order Processing**
   - Customer orders are created in `customer_orders` with line items in `order_items`
   - Orders progress through various statuses (pending, processing, shipped, delivered)
   - When orders are fulfilled, stock levels are reduced and movements recorded

3. **Purchasing**
   - Supplier information is stored in the `suppliers` table
   - Products can be sourced from multiple suppliers at different costs (tracked in `supplier_products`)
   - Purchase orders are created in `supplier_orders` with line items in `supplier_order_items`
   - When orders are received, stock levels are increased and movements recorded

## Detailed Table Descriptions

### users

Stores information about system users with role-based access control.

**Columns:**
- `id` - Primary key, auto-increment integer
- `username` - Unique username for login
- `password` - Hashed password (using bcrypt)
- `role` - User role (admin, manager, user)
- `email` - User's email address
- `created_at` - Timestamp when user was created

**Indexes:**
- Primary Key: `id`
- Unique: `username`, `email`
- Index: `role`

### products

Contains the product catalog with pricing information.

**Columns:**
- `id` - Primary key, auto-increment integer
- `name` - Product name
- `description` - Detailed product description
- `sku` - Stock Keeping Unit (unique identifier)
- `unit_price` - Selling price per unit
- `reorder_level` - Threshold quantity for reordering
- `created_at` - Timestamp when product was added

**Indexes:**
- Primary Key: `id`
- Unique: `sku`
- Index: `name`

### suppliers

Stores information about product suppliers.

**Columns:**
- `id` - Primary key, auto-increment integer
- `name` - Supplier company name
- `contact_person` - Primary contact at supplier
- `email` - Contact email address
- `phone` - Contact phone number
- `address` - Physical address
- `created_at` - Timestamp when supplier was added

**Indexes:**
- Primary Key: `id`
- Unique: `email`
- Index: `name`, `phone`

### stock

Tracks current inventory levels for each product.

**Columns:**
- `id` - Primary key, auto-increment integer
- `product_id` - Foreign key to products.id
- `quantity_available` - Current stock quantity
- `last_updated` - Timestamp of last inventory update

**Indexes:**
- Primary Key: `id`
- Foreign Key: `product_id` references `products(id)`
- Unique: `product_id` (one stock record per product)

### supplier_products

Manages the many-to-many relationship between suppliers and products.

**Columns:**
- `supplier_id` - Foreign key to suppliers.id
- `product_id` - Foreign key to products.id
- `unit_cost` - Cost per unit from this supplier
- `lead_time_days` - Expected delivery time in days

**Indexes:**
- Primary Key: Composite (`supplier_id`, `product_id`)
- Foreign Keys:
  - `supplier_id` references `suppliers(id)`
  - `product_id` references `products(id)`

### customer_orders

Records orders placed by customers.

**Columns:**
- `id` - Primary key, auto-increment integer
- `customer_name` - Name of the customer
- `customer_email` - Customer's email address
- `order_date` - Date and time of order placement
- `status` - Order status (pending, processing, shipped, delivered, cancelled)
- `total_amount` - Total order amount

**Indexes:**
- Primary Key: `id`
- Index: `customer_email`, `order_date`, `status`

### order_items

Contains line items for customer orders.

**Columns:**
- `id` - Primary key, auto-increment integer
- `order_id` - Foreign key to customer_orders.id
- `product_id` - Foreign key to products.id
- `quantity` - Quantity ordered
- `unit_price` - Price per unit (captured at time of order)

**Indexes:**
- Primary Key: `id`
- Foreign Keys:
  - `order_id` references `customer_orders(id)`
  - `product_id` references `products(id)`
- Unique: Composite (`order_id`, `product_id`) - one line item per product per order
- Index: `order_id`, `product_id`

### supplier_orders

Records purchase orders placed with suppliers.

**Columns:**
- `id` - Primary key, auto-increment integer
- `supplier_id` - Foreign key to suppliers.id
- `order_date` - Date and time order was placed
- `status` - Order status (pending, placed, confirmed, shipped, delivered, cancelled)
- `total_amount` - Total order amount
- `expected_delivery` - Expected delivery date

**Indexes:**
- Primary Key: `id`
- Foreign Key: `supplier_id` references `suppliers(id)`
- Index: `supplier_id`, `order_date`, `status`, `expected_delivery`

### supplier_order_items

Contains line items for supplier purchase orders.

**Columns:**
- `id` - Primary key, auto-increment integer
- `supplier_order_id` - Foreign key to supplier_orders.id
- `product_id` - Foreign key to products.id
- `quantity` - Quantity ordered
- `unit_cost` - Cost per unit (captured at time of order)

**Indexes:**
- Primary Key: `id`
- Foreign Keys:
  - `supplier_order_id` references `supplier_orders(id)`
  - `product_id` references `products(id)`
- Unique: Composite (`supplier_order_id`, `product_id`) - one line item per product per order
- Index: `supplier_order_id`, `product_id`

### stock_movements

Tracks all inventory movements (purchases, sales, adjustments, returns, waste).

**Columns:**
- `id` - Primary key, auto-increment integer
- `product_id` - Foreign key to products.id
- `movement_type` - Type of movement (purchase, sale, adjustment, return, waste)
- `quantity` - Quantity change (positive for incoming, negative for outgoing)
- `reference_id` - Optional reference to related order ID
- `movement_date` - Date and time of the movement

**Indexes:**
- Primary Key: `id`
- Foreign Key: `product_id` references `products(id)`
- Index: `product_id`, `movement_type`, `movement_date`, `reference_id`

## Data Relationships Diagram

```
                               +---------------+
                               |     users     |
                               +---------------+
                               | id (PK)       |
                               | username      |
                               | password      |
                               | role          |
                               | email         |
                               | created_at    |
                               +---------------+
                                        
                               +---------------+
                               |   products    |
                               +---------------+
                               | id (PK)       |
                               | name          |
                               | description   |
                               | sku           |
                               | unit_price    |
                               | reorder_level |
                               | created_at    |
                               +---------------+
                                      ^
                                     /|\
                                      |
+----------------+           +---------------------+           +--------------------+
|   suppliers    |           |        stock        |           | stock_movements    |
+----------------+           +---------------------+           +--------------------+
| id (PK)        |           | id (PK)             |           | id (PK)            |
| name           |           | product_id (FK)     |           | product_id (FK)    |
| contact_person |           | quantity_available  |           | movement_type      |
| email          |           | last_updated        |           | quantity           |
| phone          |           +---------------------+           | reference_id       |
| address        |                                             | movement_date      |
| created_at     |                                             +--------------------+
+----------------+                                                     ^
        ^                                                              |
       /|\                     +---------------------+                 |
        |                      |   customer_orders   |                 |
        |                      +---------------------+                 |
        |                      | id (PK)             |                 |
        |                      | customer_name       |                 |
+------------------+           | customer_email      |                 |
| supplier_products |          | order_date          |                 |
+------------------+           | status              |                 |
| supplier_id (FK) |           | total_amount        |                 |
| product_id (FK)  |           +---------------------+                 |
| unit_cost        |                   ^                               |
| lead_time_days   |                  /|\                              |
+------------------+                   |                               |
        ^                     +--------------------+                   |
       /|\                    |    order_items     |                   |
        |                     +--------------------+                   |
        |                     | id (PK)            |                   |
        |                     | order_id (FK)      |                   |
+--------------------+        | product_id (FK)    |-----+             |
| supplier_orders    |        | quantity           |     |             |
+--------------------+        | unit_price         |     |             |
| id (PK)            |        +--------------------+     |             |
| supplier_id (FK)   |                                   |             |
| order_date         |                                   |             |
| status             |                                   |             |
| total_amount       |                                   |             |
| expected_delivery  |                                   |             |
+--------------------+                                   |             |
        ^                                                |             |
       /|\                                               |             |
        |                                                |             |
+----------------------+                                 |             |
| supplier_order_items |                                 |             |
+----------------------+                                 |             |
| id (PK)              |                                 |             |
| supplier_order_id(FK)|                                 |             |
| product_id (FK)      |-----+----------------------------             |
| quantity             |     |                                         |
| unit_cost            |     |                                         |
+----------------------+     |                                         |
                             +----------------------------------------+
```

## Key Business Rules

1. **Stock Integrity**:
   - Stock quantities must reflect the sum of all related stock movements
   - Negative stock is allowed, but should trigger alerts or reordering

2. **Order Processing**:
   - Customer order total must match the sum of its line items
   - Supplier order total must match the sum of its line items

3. **Stock Movement Tracking**:
   - Each movement must have a valid type (purchase, sale, adjustment, return, waste)
   - Each movement has a quantity (positive for incoming, negative for outgoing)

4. **Data Integrity**:
   - Each product should appear only once per order
   - Each product should have exactly one stock record
   - Each product can be supplied by multiple suppliers (different costs/lead times)

5. **Referential Integrity**:
   - All foreign key relationships must be maintained
   - Appropriate actions should be taken on delete/update (cascade vs. restrict)

## Database Setup Instructions

### Prerequisites
- MySQL 8.0 or later
- User with database creation privileges

### Setup Process

1. **Create the database and user**:
   ```sql
   CREATE DATABASE scm_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE USER 'scm_user'@'localhost' IDENTIFIED BY 'scm_password';
   GRANT ALL PRIVILEGES ON scm_db.* TO 'scm_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

2. **Run the schema creation script**:
   ```bash
   mysql -u scm_user -p scm_db < schema.sql
   ```

3. **Load sample data (optional)**:
   ```bash
   mysql -u scm_user -p scm_db < sample_data.sql
   ```

### Automated Setup

For convenience, setup scripts are provided:

- For Linux/Mac: `setup.sh`
- For Windows: `schema.bat`

These scripts accept parameters to customize the database name, user, password, and host. Run with `-h` or `--help` to see options.

## Common Database Operations

The `query_examples.sql` file contains sample queries for common operations:

1. User authentication
2. Product management
3. Supplier management
4. Order processing
5. Inventory tracking
6. Reporting
7. Data modification examples

## Performance Considerations

The schema includes indexes on frequently queried columns to improve performance:
- Foreign key columns
- Columns commonly used in WHERE clauses (status, email, dates)
- Columns used for sorting or grouping

For large deployments, consider:
- Table partitioning for historical data
- Read replicas for reporting queries
- Query optimization for complex reports

## Security Considerations

- User passwords are stored using bcrypt hashing
- Database user permissions should be restricted based on application needs
- Consider encrypting sensitive supplier and customer information
- Implement row-level security for multi-tenant deployments if needed

## Maintenance Recommendations

- Backup the database daily
- Check for orphaned records monthly
- Analyze table statistics quarterly
- Consider archiving old orders and movements after 2 years 
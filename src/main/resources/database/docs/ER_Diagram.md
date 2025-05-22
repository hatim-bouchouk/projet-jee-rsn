# Supply Chain Management System - ER Diagram

## Entity Relationship Diagram

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

## Table Descriptions

### users
- Stores user authentication and role information
- Primary Key: `id`
- Key Fields: `username` (unique), `email` (unique)
- Relationships: none

### products
- Stores product catalog information
- Primary Key: `id`
- Key Fields: `sku` (unique)
- Relationships:
  - One-to-One with `stock`
  - One-to-Many with `stock_movements`
  - Many-to-Many with `suppliers` through `supplier_products`
  - One-to-Many with `order_items`
  - One-to-Many with `supplier_order_items`

### suppliers
- Stores supplier information
- Primary Key: `id`
- Key Fields: `email` (unique)
- Relationships:
  - Many-to-Many with `products` through `supplier_products`
  - One-to-Many with `supplier_orders`

### stock
- Tracks current inventory levels
- Primary Key: `id`
- Key Fields: `product_id` (unique, FK)
- Relationships:
  - One-to-One with `products`

### supplier_products
- Junction table for the many-to-many relationship between suppliers and products
- Primary Key: Composite (`supplier_id`, `product_id`)
- Key Fields: Both FK columns
- Relationships:
  - Many-to-One with `suppliers`
  - Many-to-One with `products`

### customer_orders
- Stores customer order information
- Primary Key: `id`
- Key Fields: `customer_email`, `order_date`
- Relationships:
  - One-to-Many with `order_items`

### order_items
- Stores line items for customer orders
- Primary Key: `id`
- Key Fields: `order_id` (FK), `product_id` (FK)
- Relationships:
  - Many-to-One with `customer_orders`
  - Many-to-One with `products`

### supplier_orders
- Stores purchase orders to suppliers
- Primary Key: `id`
- Key Fields: `supplier_id` (FK), `order_date`
- Relationships:
  - Many-to-One with `suppliers`
  - One-to-Many with `supplier_order_items`

### supplier_order_items
- Stores line items for supplier orders
- Primary Key: `id`
- Key Fields: `supplier_order_id` (FK), `product_id` (FK)
- Relationships:
  - Many-to-One with `supplier_orders`
  - Many-to-One with `products`

### stock_movements
- Tracks all inventory changes
- Primary Key: `id`
- Key Fields: `product_id` (FK), `movement_date`
- Relationships:
  - Many-to-One with `products`
  - Reference relationship to `customer_orders` or `supplier_orders` through `reference_id`

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
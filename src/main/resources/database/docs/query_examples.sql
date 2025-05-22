-- Supply Chain Management System
-- Example SQL Queries for Common Operations

-- 1. User Authentication
-- Get user by username with role for login authentication
SELECT id, username, password, role, email
FROM users
WHERE username = 'admin';

-- 2. Product Management
-- List all products with current stock levels
SELECT p.id, p.name, p.sku, p.unit_price, p.reorder_level, 
       s.quantity_available, s.last_updated
FROM products p
JOIN stock s ON p.id = s.product_id
ORDER BY p.name;

-- Find products below reorder level
SELECT p.id, p.name, p.sku, p.reorder_level, s.quantity_available
FROM products p
JOIN stock s ON p.id = s.product_id
WHERE s.quantity_available < p.reorder_level
ORDER BY (p.reorder_level - s.quantity_available) DESC;

-- 3. Supplier Management
-- List all suppliers with contact information
SELECT id, name, contact_person, email, phone, address
FROM suppliers
ORDER BY name;

-- Find which suppliers provide a specific product
SELECT s.id, s.name, s.contact_person, s.email, s.phone,
       sp.unit_cost, sp.lead_time_days
FROM suppliers s
JOIN supplier_products sp ON s.id = sp.supplier_id
WHERE sp.product_id = 1  -- Replace with actual product_id
ORDER BY sp.unit_cost ASC;

-- Find the supplier with the lowest price for each product
SELECT p.id, p.name, p.sku, s.name AS supplier_name, 
       sp.unit_cost, sp.lead_time_days
FROM products p
JOIN supplier_products sp ON p.id = sp.product_id
JOIN suppliers s ON sp.supplier_id = s.id
WHERE (p.id, sp.unit_cost) IN (
    SELECT sp2.product_id, MIN(sp2.unit_cost)
    FROM supplier_products sp2
    GROUP BY sp2.product_id
)
ORDER BY p.name;

-- 4. Order Management
-- List recent customer orders with status
SELECT id, customer_name, customer_email, order_date, status, total_amount
FROM customer_orders
ORDER BY order_date DESC
LIMIT 10;

-- Get detailed customer order with line items
SELECT co.id, co.customer_name, co.order_date, co.status,
       p.name AS product_name, oi.quantity, oi.unit_price,
       (oi.quantity * oi.unit_price) AS line_total
FROM customer_orders co
JOIN order_items oi ON co.id = oi.order_id
JOIN products p ON oi.product_id = p.id
WHERE co.id = 1  -- Replace with actual order_id
ORDER BY p.name;

-- List recent supplier orders with status
SELECT so.id, s.name AS supplier_name, so.order_date, so.status, 
       so.total_amount, so.expected_delivery
FROM supplier_orders so
JOIN suppliers s ON so.supplier_id = s.id
ORDER BY so.order_date DESC
LIMIT 10;

-- Get detailed supplier order with line items
SELECT so.id, s.name AS supplier_name, so.order_date, so.status,
       p.name AS product_name, soi.quantity, soi.unit_cost,
       (soi.quantity * soi.unit_cost) AS line_total
FROM supplier_orders so
JOIN supplier_order_items soi ON so.id = soi.supplier_order_id
JOIN products p ON soi.product_id = p.id
JOIN suppliers s ON so.supplier_id = s.id
WHERE so.id = 1  -- Replace with actual supplier order ID
ORDER BY p.name;

-- 5. Inventory Management
-- Show stock movements for a specific product
SELECT sm.id, p.name AS product_name, sm.movement_type, sm.quantity,
       sm.reference_id, sm.movement_date
FROM stock_movements sm
JOIN products p ON sm.product_id = p.id
WHERE sm.product_id = 1  -- Replace with actual product_id
ORDER BY sm.movement_date DESC;

-- Get stock movement history with reference to orders
SELECT sm.id, p.name AS product_name, sm.movement_type, sm.quantity,
       CASE
           WHEN sm.movement_type = 'sale' THEN CONCAT('Customer Order #', sm.reference_id)
           WHEN sm.movement_type = 'purchase' THEN CONCAT('Supplier Order #', sm.reference_id)
           ELSE CONCAT('Reference #', IFNULL(sm.reference_id, 'N/A'))
       END AS reference,
       sm.movement_date
FROM stock_movements sm
JOIN products p ON sm.product_id = p.id
ORDER BY sm.movement_date DESC
LIMIT 20;

-- 6. Reporting Queries
-- Sales by product (last 30 days)
SELECT p.id, p.name, p.sku, 
       SUM(oi.quantity) AS units_sold,
       SUM(oi.quantity * oi.unit_price) AS total_sales
FROM products p
JOIN order_items oi ON p.id = oi.product_id
JOIN customer_orders co ON oi.order_id = co.id
WHERE co.order_date > DATE_SUB(NOW(), INTERVAL 30 DAY)
  AND co.status != 'cancelled'
GROUP BY p.id, p.name, p.sku
ORDER BY total_sales DESC;

-- Purchases by supplier (last 30 days)
SELECT s.id, s.name,
       COUNT(DISTINCT so.id) AS order_count,
       SUM(so.total_amount) AS total_purchases
FROM suppliers s
JOIN supplier_orders so ON s.id = so.supplier_id
WHERE so.order_date > DATE_SUB(NOW(), INTERVAL 30 DAY)
  AND so.status != 'cancelled'
GROUP BY s.id, s.name
ORDER BY total_purchases DESC;

-- Inventory valuation
SELECT p.id, p.name, p.sku, s.quantity_available,
       p.unit_price AS unit_value,
       (s.quantity_available * p.unit_price) AS total_value
FROM products p
JOIN stock s ON p.id = s.product_id
ORDER BY total_value DESC;

-- Low stock alert with supplier information
SELECT p.id, p.name, p.sku, p.reorder_level, 
       s.quantity_available,
       (p.reorder_level - s.quantity_available) AS units_needed,
       MIN(sp.unit_cost) AS min_unit_cost,
       (MIN(sp.unit_cost) * (p.reorder_level - s.quantity_available)) AS restock_cost
FROM products p
JOIN stock s ON p.id = s.product_id
JOIN supplier_products sp ON p.id = sp.product_id
WHERE s.quantity_available < p.reorder_level
GROUP BY p.id, p.name, p.sku, p.reorder_level, s.quantity_available
ORDER BY (p.reorder_level - s.quantity_available) DESC;

-- 7. Advanced Reporting Queries
-- Profit margin by product (based on last supplier cost)
SELECT p.id, p.name, p.sku, p.unit_price,
       MIN(sp.unit_cost) AS min_cost,
       (p.unit_price - MIN(sp.unit_cost)) AS unit_profit,
       ((p.unit_price - MIN(sp.unit_cost)) / p.unit_price * 100) AS profit_margin_pct
FROM products p
JOIN supplier_products sp ON p.id = sp.product_id
GROUP BY p.id, p.name, p.sku, p.unit_price
ORDER BY profit_margin_pct DESC;

-- Monthly sales trends
SELECT 
    DATE_FORMAT(co.order_date, '%Y-%m') AS month,
    COUNT(DISTINCT co.id) AS order_count,
    SUM(co.total_amount) AS total_sales,
    AVG(co.total_amount) AS avg_order_value
FROM customer_orders co
WHERE co.order_date > DATE_SUB(NOW(), INTERVAL 12 MONTH)
  AND co.status != 'cancelled'
GROUP BY DATE_FORMAT(co.order_date, '%Y-%m')
ORDER BY month;

-- Inventory turnover rate (sales quantity / average inventory)
-- Assuming we have 30 days of data
SELECT 
    p.id, p.name, p.sku,
    SUM(CASE WHEN sm.movement_type = 'sale' THEN ABS(sm.quantity) ELSE 0 END) AS units_sold,
    AVG(s.quantity_available) AS avg_inventory,
    CASE 
        WHEN AVG(s.quantity_available) > 0 
        THEN SUM(CASE WHEN sm.movement_type = 'sale' THEN ABS(sm.quantity) ELSE 0 END) / AVG(s.quantity_available)
        ELSE 0 
    END AS turnover_rate
FROM products p
JOIN stock s ON p.id = s.product_id
LEFT JOIN stock_movements sm ON p.id = sm.product_id
WHERE sm.movement_date > DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY p.id, p.name, p.sku
ORDER BY turnover_rate DESC;

-- 8. Data Modification Examples
-- Create a new customer order
START TRANSACTION;

-- Insert the order header
INSERT INTO customer_orders (customer_name, customer_email, status, total_amount)
VALUES ('New Customer', 'customer@example.com', 'pending', 0);

-- Get the new order ID
SET @new_order_id = LAST_INSERT_ID();

-- Insert order items
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
VALUES 
    (@new_order_id, 1, 2, (SELECT unit_price FROM products WHERE id = 1)),
    (@new_order_id, 3, 1, (SELECT unit_price FROM products WHERE id = 3));

-- Update the order total
UPDATE customer_orders
SET total_amount = (
    SELECT SUM(quantity * unit_price)
    FROM order_items
    WHERE order_id = @new_order_id
)
WHERE id = @new_order_id;

-- Create inventory movements for the items
INSERT INTO stock_movements (product_id, movement_type, quantity, reference_id, movement_date)
VALUES 
    (1, 'sale', -2, @new_order_id, NOW()),
    (3, 'sale', -1, @new_order_id, NOW());

-- Update inventory levels
UPDATE stock
SET quantity_available = quantity_available - 2
WHERE product_id = 1;

UPDATE stock
SET quantity_available = quantity_available - 1
WHERE product_id = 3;

COMMIT;

-- Process a supplier order receipt
START TRANSACTION;

-- Update the supplier order status
UPDATE supplier_orders
SET status = 'delivered'
WHERE id = 1;

-- Get the supplier order items
SELECT supplier_order_id, product_id, quantity
INTO @order_id, @product_id, @quantity
FROM supplier_order_items
WHERE supplier_order_id = 1;

-- Create inventory movements for each received item
INSERT INTO stock_movements (product_id, movement_type, quantity, reference_id, movement_date)
SELECT product_id, 'purchase', quantity, supplier_order_id, NOW()
FROM supplier_order_items
WHERE supplier_order_id = 1;

-- Update inventory levels
UPDATE stock s
JOIN supplier_order_items soi ON s.product_id = soi.product_id
SET s.quantity_available = s.quantity_available + soi.quantity
WHERE soi.supplier_order_id = 1;

COMMIT; 
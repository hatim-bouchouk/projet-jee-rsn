-- Supply Chain Management System Initialization Data
-- This script creates sample data for the system including users, products, suppliers, and orders

-- Clear existing data (if any)
DELETE FROM stock_movements;
DELETE FROM supplier_order_items;
DELETE FROM supplier_orders;
DELETE FROM order_items;
DELETE FROM customer_orders;
DELETE FROM supplier_products;
DELETE FROM stock;
DELETE FROM suppliers;
DELETE FROM products;
DELETE FROM users;

-- Insert sample users with BCrypt hashed passwords
-- Default passwords:
-- admin:admin123 = $2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS
-- manager:manager123 = $2a$10$FKdvC4NZ5/3hx.A.Z.yfO.KeUuV.crs5g4eAoR.I6NQnoGiIUCnxy
-- client:client123 = $2a$10$UYrGNY9bT2TQC8A.Ql8jR.Xf0aYLxFG4KgbBy5GqB.NJA1CV1nKZ2
INSERT INTO users (username, password, role, email, created_at) VALUES
    ('admin', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'admin', 'admin@scm.com', NOW()),
    ('manager', '$2a$10$FKdvC4NZ5/3hx.A.Z.yfO.KeUuV.crs5g4eAoR.I6NQnoGiIUCnxy', 'manager', 'manager@scm.com', NOW()),
    ('client', '$2a$10$UYrGNY9bT2TQC8A.Ql8jR.Xf0aYLxFG4KgbBy5GqB.NJA1CV1nKZ2', 'user', 'client@scm.com', NOW());

-- Insert sample products with different stock levels
-- Some products have low stock to test alerts
INSERT INTO products (name, description, sku, unit_price, reorder_level, product_type, created_at) VALUES
    ('Laptop', 'High-performance business laptop', 'TECH-LAP-001', 899.99, 10, 'Electronics', NOW()),
    ('Desktop Computer', 'Office desktop computer', 'TECH-DES-001', 699.99, 5, 'Electronics', NOW()),
    ('Wireless Mouse', 'Ergonomic wireless mouse', 'ACC-MOU-001', 24.99, 20, 'Accessories', NOW()),
    ('Mechanical Keyboard', 'RGB mechanical keyboard', 'ACC-KEY-001', 59.99, 15, 'Accessories', NOW()),
    ('24" Monitor', '24-inch Full HD monitor', 'TECH-MON-001', 159.99, 8, 'Electronics', NOW()),
    ('Printer', 'Color laser printer', 'TECH-PRI-001', 249.99, 5, 'Electronics', NOW()),
    ('USB Drive 64GB', '64GB USB 3.0 flash drive', 'ACC-USB-001', 19.99, 30, 'Accessories', NOW()),
    ('External Hard Drive', '1TB external hard drive', 'ACC-HDD-001', 89.99, 12, 'Accessories', NOW()),
    ('Webcam HD', 'HD webcam for video conferencing', 'ACC-CAM-001', 49.99, 15, 'Accessories', NOW()),
    ('Noise-Canceling Headphones', 'Wireless noise-canceling headphones', 'ACC-AUD-001', 79.99, 10, 'Accessories', NOW());

-- Insert sample suppliers
INSERT INTO suppliers (name, contact_person, email, phone, address, created_at) VALUES
    ('Tech Solutions Inc.', 'John Smith', 'contact@techsolutions.com', '555-123-4567', '123 Tech Avenue, Silicon Valley, CA 94043', NOW()),
    ('Global Electronics', 'Sarah Johnson', 'info@globalelectronics.com', '555-987-6543', '456 Digital Street, New York, NY 10001', NOW()),
    ('Office Supplies Co.', 'Michael Brown', 'sales@officesupplies.com', '555-456-7890', '789 Business Road, Chicago, IL 60601', NOW()),
    ('Computer Parts Ltd.', 'David Wilson', 'support@computerparts.com', '555-369-8741', '321 Hardware Lane, Austin, TX 78701', NOW());

-- Insert stock records
-- Some with low stock for testing alerts
INSERT INTO stock (product_id, quantity_available, last_updated) VALUES
    (1, 15, NOW()),  -- Laptop (above reorder level)
    (2, 3, NOW()),   -- Desktop Computer (below reorder level - should trigger alert)
    (3, 50, NOW()),  -- Wireless Mouse (above reorder level)
    (4, 8, NOW()),   -- Mechanical Keyboard (below reorder level - should trigger alert)
    (5, 10, NOW()),  -- Monitor (above reorder level)
    (6, 2, NOW()),   -- Printer (below reorder level - should trigger alert)
    (7, 100, NOW()), -- USB Drive (above reorder level)
    (8, 15, NOW()),  -- External Hard Drive (above reorder level)
    (9, 5, NOW()),   -- Webcam (below reorder level - should trigger alert)
    (10, 30, NOW()); -- Headphones (above reorder level)

-- Insert supplier product relationships
-- Tech Solutions Inc. supplies laptops, desktops, and monitors
INSERT INTO supplier_products (supplier_id, product_id, unit_cost, lead_time_days) VALUES
    (1, 1, 699.99, 7),  -- Tech Solutions - Laptop
    (1, 2, 499.99, 5),  -- Tech Solutions - Desktop Computer
    (1, 5, 119.99, 3);  -- Tech Solutions - Monitor

-- Global Electronics supplies various accessories
INSERT INTO supplier_products (supplier_id, product_id, unit_cost, lead_time_days) VALUES
    (2, 3, 14.99, 2),   -- Global Electronics - Wireless Mouse
    (2, 4, 39.99, 2),   -- Global Electronics - Keyboard
    (2, 7, 9.99, 1),    -- Global Electronics - USB Drive
    (2, 9, 29.99, 3),   -- Global Electronics - Webcam
    (2, 10, 49.99, 4);  -- Global Electronics - Headphones

-- Office Supplies Co. supplies printers and some accessories
INSERT INTO supplier_products (supplier_id, product_id, unit_cost, lead_time_days) VALUES
    (3, 4, 45.99, 3),   -- Office Supplies - Keyboard (alternate supplier)
    (3, 6, 189.99, 5),  -- Office Supplies - Printer
    (3, 7, 12.99, 2);   -- Office Supplies - USB Drive (alternate supplier)

-- Computer Parts Ltd. supplies computers and storage devices
INSERT INTO supplier_products (supplier_id, product_id, unit_cost, lead_time_days) VALUES
    (4, 2, 549.99, 6),  -- Computer Parts - Desktop Computer (alternate supplier)
    (4, 8, 69.99, 4);   -- Computer Parts - External Hard Drive

-- Insert sample customer orders with different statuses
INSERT INTO customer_orders (customer_name, customer_email, customer_phone, shipping_address, order_date, status, total_amount, notes) VALUES
    ('Jane Doe', 'jane.doe@example.com', '555-111-2222', '123 Main St, Anytown, USA', DATE_SUB(NOW(), INTERVAL 10 DAY), 'DELIVERED', 984.97, 'Priority shipping requested'),
    ('Robert Johnson', 'robert.j@example.com', '555-222-3333', '456 Oak Ave, Somewhere, USA', DATE_SUB(NOW(), INTERVAL 5 DAY), 'SHIPPED', 1019.98, NULL),
    ('Alice Williams', 'alice.w@example.com', '555-333-4444', '789 Pine Rd, Elsewhere, USA', DATE_SUB(NOW(), INTERVAL 2 DAY), 'PROCESSING', 119.98, 'Please include gift receipt'),
    ('Mike Davis', 'mike.d@example.com', '555-444-5555', '101 Elm St, Nowhere, USA', DATE_SUB(NOW(), INTERVAL 1 DAY), 'PENDING', 399.97, NULL),
    ('Susan Miller', 'susan.m@example.com', '555-555-6666', '202 Maple Dr, Anyplace, USA', NOW(), 'NEW', 899.99, 'Corporate order');

-- Insert order items for the customer orders
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES
    (1, 1, 1, 899.99),  -- Jane Doe - Laptop
    (1, 3, 1, 24.99),   -- Jane Doe - Wireless Mouse
    (1, 7, 3, 19.99),   -- Jane Doe - USB Drive

    (2, 2, 1, 699.99),  -- Robert Johnson - Desktop Computer
    (2, 5, 2, 159.99),  -- Robert Johnson - Monitor

    (3, 4, 2, 59.99),   -- Alice Williams - Keyboard

    (4, 6, 1, 249.99),  -- Mike Davis - Printer
    (4, 8, 1, 89.99),   -- Mike Davis - External Hard Drive
    (4, 9, 1, 49.99),   -- Mike Davis - Webcam

    (5, 1, 1, 899.99);  -- Susan Miller - Laptop

-- Insert sample supplier orders with different statuses
INSERT INTO supplier_orders (supplier_id, order_date, status, total_amount, expected_delivery) VALUES
    (1, DATE_SUB(NOW(), INTERVAL 15 DAY), 'delivered', 10499.85, DATE_SUB(NOW(), INTERVAL 8 DAY)),
    (2, DATE_SUB(NOW(), INTERVAL 7 DAY), 'delivered', 2199.20, DATE_SUB(NOW(), INTERVAL 5 DAY)),
    (3, DATE_SUB(NOW(), INTERVAL 3 DAY), 'shipped', 1899.90, DATE_ADD(NOW(), INTERVAL 2 DAY)),
    (4, NOW(), 'pending', 3149.85, DATE_ADD(NOW(), INTERVAL 6 DAY));

-- Insert supplier order items
INSERT INTO supplier_order_items (supplier_order_id, product_id, quantity, unit_cost) VALUES
    (1, 1, 15, 699.99),   -- 15 Laptops from Tech Solutions
    
    (2, 3, 50, 14.99),    -- 50 Wireless Mice from Global Electronics
    (2, 4, 20, 39.99),    -- 20 Keyboards from Global Electronics
    (2, 10, 10, 49.99),   -- 10 Headphones from Global Electronics
    
    (3, 6, 10, 189.99),   -- 10 Printers from Office Supplies
    
    (4, 2, 5, 549.99),    -- 5 Desktop Computers from Computer Parts
    (4, 8, 10, 69.99);    -- 10 External Hard Drives from Computer Parts

-- Insert stock movements of various types
INSERT INTO stock_movements (product_id, movement_type, quantity, reference_id, movement_date, notes) VALUES
    -- Incoming stock from supplier orders (purchases)
    (1, 'purchase', 15, 1, DATE_SUB(NOW(), INTERVAL 8 DAY), 'Received from Tech Solutions Inc.'),
    (3, 'purchase', 50, 2, DATE_SUB(NOW(), INTERVAL 5 DAY), 'Received from Global Electronics'),
    (4, 'purchase', 20, 2, DATE_SUB(NOW(), INTERVAL 5 DAY), 'Received from Global Electronics'),
    (10, 'purchase', 10, 2, DATE_SUB(NOW(), INTERVAL 5 DAY), 'Received from Global Electronics'),
    
    -- Outgoing stock from customer orders (sales)
    (1, 'sale', -1, 1, DATE_SUB(NOW(), INTERVAL 10 DAY), 'Order #1 - Jane Doe'),
    (3, 'sale', -1, 1, DATE_SUB(NOW(), INTERVAL 10 DAY), 'Order #1 - Jane Doe'),
    (7, 'sale', -3, 1, DATE_SUB(NOW(), INTERVAL 10 DAY), 'Order #1 - Jane Doe'),
    (2, 'sale', -1, 2, DATE_SUB(NOW(), INTERVAL 5 DAY), 'Order #2 - Robert Johnson'),
    (5, 'sale', -2, 2, DATE_SUB(NOW(), INTERVAL 5 DAY), 'Order #2 - Robert Johnson'),
    
    -- Stock adjustments (inventory counts, damage, etc.)
    (7, 'adjustment', -5, NULL, DATE_SUB(NOW(), INTERVAL 12 DAY), 'Inventory count adjustment'),
    (9, 'adjustment', -2, NULL, DATE_SUB(NOW(), INTERVAL 9 DAY), 'Damaged items'),
    
    -- Returns
    (4, 'return_item', 2, 3, DATE_SUB(NOW(), INTERVAL 1 DAY), 'Customer return - defective'),
    
    -- Waste/Damage
    (8, 'waste', -1, NULL, DATE_SUB(NOW(), INTERVAL 6 DAY), 'Damaged during handling'); 
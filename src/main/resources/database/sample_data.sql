-- Supply Chain Management System Sample Data
-- This script populates the database with initial sample data

-- Insert sample Users
INSERT INTO users (username, password, role, email) VALUES
    ('admin', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'admin', 'admin@scm.com'),
    ('manager', '$2a$10$CBKF0yFu9Tjtg3TVfpOHOeRQvdqCRcPwZEeSx9zQotR/Q9XcXQTIG', 'manager', 'manager@scm.com'),
    ('user', '$2a$10$CBKF0yFu9Tjtg3TVfpOHOeRQvdqCRcPwZEeSx9zQotR/Q9XcXQTIG', 'user', 'user@scm.com');
-- Note: Passwords are bcrypt hashed. Plain text is 'password'

-- Insert sample Products
INSERT INTO products (name, description, sku, unit_price, reorder_level) VALUES
    ('Laptop', 'High performance laptop', 'TECH-LAP-001', 899.99, 5),
    ('Desktop Computer', 'Office desktop computer', 'TECH-DES-001', 699.99, 3),
    ('Wireless Mouse', 'Ergonomic wireless mouse', 'ACC-MOU-001', 24.99, 10),
    ('Keyboard', 'Mechanical keyboard', 'ACC-KEY-001', 59.99, 8),
    ('Monitor', '24-inch LCD monitor', 'TECH-MON-001', 159.99, 5),
    ('Printer', 'Color laser printer', 'TECH-PRI-001', 249.99, 2),
    ('USB Drive', '64GB USB 3.0 flash drive', 'ACC-USB-001', 19.99, 15),
    ('External Hard Drive', '1TB external hard drive', 'ACC-HDD-001', 89.99, 7),
    ('Webcam', 'HD webcam', 'ACC-CAM-001', 49.99, 10),
    ('Headphones', 'Noise-canceling headphones', 'ACC-AUD-001', 79.99, 8);

-- Insert sample Suppliers
INSERT INTO suppliers (name, contact_person, email, phone, address) VALUES
    ('Tech Solutions Inc.', 'John Smith', 'contact@techsolutions.com', '555-123-4567', '123 Tech Avenue, Silicon Valley, CA 94043'),
    ('Global Electronics', 'Sarah Johnson', 'info@globalelectronics.com', '555-987-6543', '456 Digital Street, New York, NY 10001'),
    ('Office Supplies Co.', 'Michael Brown', 'sales@officesupplies.com', '555-456-7890', '789 Business Road, Chicago, IL 60601'),
    ('Computer Parts Ltd.', 'David Wilson', 'support@computerparts.com', '555-369-8741', '321 Hardware Lane, Austin, TX 78701');

-- Insert Stock records
INSERT INTO stock (product_id, quantity_available) VALUES
    (1, 15), -- Laptop
    (2, 10), -- Desktop Computer
    (3, 50), -- Wireless Mouse
    (4, 35), -- Keyboard
    (5, 20), -- Monitor
    (6, 8),  -- Printer
    (7, 100), -- USB Drive
    (8, 25), -- External Hard Drive
    (9, 40), -- Webcam
    (10, 30); -- Headphones

-- Insert Supplier_Products relationships
-- Tech Solutions Inc. supplies laptops, desktops, and monitors
INSERT INTO supplier_products (supplier_id, product_id, unit_cost, lead_time_days) VALUES
    (1, 1, 699.99, 7), -- Tech Solutions - Laptop
    (1, 2, 499.99, 5), -- Tech Solutions - Desktop Computer
    (1, 5, 119.99, 3); -- Tech Solutions - Monitor

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

-- Insert sample Customer_Orders
INSERT INTO customer_orders (customer_name, customer_email, order_date, status, total_amount) VALUES
    ('Jane Doe', 'jane.doe@email.com', DATE_SUB(NOW(), INTERVAL 10 DAY), 'delivered', 984.97),
    ('Robert Johnson', 'robert.j@email.com', DATE_SUB(NOW(), INTERVAL 5 DAY), 'shipped', 1019.98),
    ('Alice Williams', 'alice.w@email.com', DATE_SUB(NOW(), INTERVAL 2 DAY), 'processing', 119.98),
    ('Mike Davis', 'mike.d@email.com', DATE_SUB(NOW(), INTERVAL 1 DAY), 'pending', 399.97);

-- Insert Order_Items for the customer orders
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
    (4, 10, 1, 79.99);  -- Mike Davis - Headphones

-- Insert sample Supplier_Orders
INSERT INTO supplier_orders (supplier_id, order_date, status, total_amount, expected_delivery) VALUES
    (1, DATE_SUB(NOW(), INTERVAL 15 DAY), 'delivered', 10499.85, DATE_SUB(NOW(), INTERVAL 8 DAY)),
    (2, DATE_SUB(NOW(), INTERVAL 7 DAY), 'delivered', 2199.20, DATE_SUB(NOW(), INTERVAL 5 DAY)),
    (3, DATE_SUB(NOW(), INTERVAL 3 DAY), 'shipped', 1899.90, DATE_ADD(NOW(), INTERVAL 2 DAY)),
    (4, NOW(), 'pending', 3149.85, DATE_ADD(NOW(), INTERVAL 6 DAY));

-- Insert Supplier_Order_Items
INSERT INTO supplier_order_items (supplier_order_id, product_id, quantity, unit_cost) VALUES
    (1, 1, 15, 699.99),   -- 15 Laptops from Tech Solutions
    
    (2, 3, 50, 14.99),    -- 50 Wireless Mice from Global Electronics
    (2, 4, 20, 39.99),    -- 20 Keyboards from Global Electronics
    (2, 10, 10, 49.99),   -- 10 Headphones from Global Electronics
    
    (3, 6, 10, 189.99),   -- 10 Printers from Office Supplies
    
    (4, 2, 5, 549.99),    -- 5 Desktop Computers from Computer Parts
    (4, 8, 10, 69.99);    -- 10 External Hard Drives from Computer Parts

-- Insert Stock_Movements
INSERT INTO stock_movements (product_id, movement_type, quantity, reference_id, movement_date) VALUES
    -- Incoming stock from supplier orders
    (1, 'purchase', 15, 1, DATE_SUB(NOW(), INTERVAL 8 DAY)),
    (3, 'purchase', 50, 2, DATE_SUB(NOW(), INTERVAL 5 DAY)),
    (4, 'purchase', 20, 2, DATE_SUB(NOW(), INTERVAL 5 DAY)),
    (10, 'purchase', 10, 2, DATE_SUB(NOW(), INTERVAL 5 DAY)),
    
    -- Outgoing stock from customer orders
    (1, 'sale', -1, 1, DATE_SUB(NOW(), INTERVAL 10 DAY)),
    (3, 'sale', -1, 1, DATE_SUB(NOW(), INTERVAL 10 DAY)),
    (7, 'sale', -3, 1, DATE_SUB(NOW(), INTERVAL 10 DAY)),
    (2, 'sale', -1, 2, DATE_SUB(NOW(), INTERVAL 5 DAY)),
    (5, 'sale', -2, 2, DATE_SUB(NOW(), INTERVAL 5 DAY)),
    
    -- Stock adjustments (could be from inventory counts, damage, etc.)
    (7, 'adjustment', -5, NULL, DATE_SUB(NOW(), INTERVAL 12 DAY)),
    (9, 'adjustment', -2, NULL, DATE_SUB(NOW(), INTERVAL 9 DAY)),
    
    -- Returns
    (4, 'return', 2, 3, DATE_SUB(NOW(), INTERVAL 1 DAY)),
    
    -- Waste/Damage
    (8, 'waste', -1, NULL, DATE_SUB(NOW(), INTERVAL 6 DAY)); 
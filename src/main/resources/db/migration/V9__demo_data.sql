
-- Demo accounts [passwords: AdminPass123!/WarehousePass123!/CustomerPass123!]
--      ** DEV/DEMO-ONLY (REMOVE BEFORE DEPLOYMENT) **
INSERT INTO users (email, password_hash, role) VALUES
    ('admin@example.com', '$2a$10$gtdikLfdDE8.x8vIpPftv.w9Ioa188m5Fl7/jf9LYWurHcLD3PpWS', 'ADMIN'),
    ('warehouse@example.com', '$2a$10$5HRAIluEvAMhNHi9RmXhAeFyVt/C3PFVOVCjRgDA7cm8gQyk0/AA6', 'WAREHOUSE_MANAGER'),
    ('customer@example.com', '$2a$10$l2kBA7A5xS41vokGLca4Cu4YlVyY7CCIw7N2THpr9baKDWOaGcAzG', 'CUSTOMER');

-- Customer profile
INSERT INTO customers (user_id, first_name, last_name, phone)
SELECT id, 'Demo', 'Customer', '+1-234-555-6666'
FROM users WHERE email = 'customer@example.com';

-- Demo products
INSERT INTO products (sku, name, description, price, active) VALUES
    ('KM-KB-MECH-001', 'KeyMaster: Mechanical Keyboard',
     'KeyMaster''s full-size keyboard with tactile mechanical switches',
     89.99, TRUE),

    ('3M-MS-WRLSS-001', '3Mice: Wireless Mouse',
     '3Mice''s ergonomic wireless mouse with USB receiver',
     39.99, TRUE),

     ('PS-HD-USB-001', 'Portsmith: USB-C Hub',
      'Portsmith''s 7-in-1 USB-C hub',
      49.99, TRUE),

     ('CV-MNTR-27LED-001', 'CompuVision: 27" LED Monitor',
      '27-inch IPS LED 100Hz monitor with HDMI & D-sub from CompuVision',
      219.99, TRUE),

     ('MXS-CHR-ERG-001', 'MaxSit: Ergonomic Chair',
      'MaxSit''s adjustable ergonomic chair with lumbar support',
      299.99, TRUE);

-- Product inventory data
INSERT INTO inventory_items (product_id, quantity_on_hand, quantity_reserved, reorder_threshold)
SELECT p.id, v.qty, 0, 5
FROM products p
JOIN (VALUES
    ('KM-KB-MECH-001', 25),
    ('3M-MS-WRLSS-001', 50),
    ('PS-HD-USB-001',  30),
    ('CV-MNTR-27LED-001',10),
    ('MXS-CHR-ERG-001', 5)
) AS v(sku, qty) ON p.sku = v.sku;


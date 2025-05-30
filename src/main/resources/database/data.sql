-- User data remains the same
INSERT INTO "user" (name, email, password, status) VALUES
    ('user', 'user@email.com', '$2a$10$ngSdw1kgIS40jwcvUqw48Osyd9NE8PjuMUatovpt6hlCBF0TDFUJu', 'ACTIVE');

-- Categories
INSERT INTO category (name) VALUES
    ('A'), ('B'), ('C'), ('D'), ('E'), ('F'), ('G'), ('H');

-- Products
INSERT INTO product (name, category_id, quantity, price) VALUES
    ('Product A', 1, 50, 100.00),
    ('Product B', 2, 100, 150.00),
    ('Product C', 3, 150, 200.00),
    ('Product D', 4, 200, 250.00),
    ('Product E', 5, 250, 300.00),
    ('Product F', 6, 300, 350.00),
    ('Product G', 7, 350, 400.00),
    ('Product H', 8, 400, 450.00);

-- Customers
INSERT INTO customer (name, address, phone) VALUES
    ('Customer A', '123 Main St', '555-0101'),
    ('Customer B', '456 Oak Ave', '555-0102'),
    ('Customer C', '789 Pine Rd', '555-0103'),
    ('Customer D', '321 Elm Blvd', '555-0104');

-- Initialize order sequence
INSERT INTO order_sequence (id, counter) VALUES (1, 1);

-- Orders (numbers will be auto-generated by the trigger)
INSERT INTO "order" (status, customer_id) VALUES
    ('UNPAID', 1),
    ('UNPAID', 2),
    ('PAID', 3),
    ('PAID', 4);

-- Order items
INSERT INTO order_item (quantity, product_id, order_id, "index") VALUES
    (1, 1, 1, 1),
    (2, 2, 1, 2),
    (1, 3, 2, 1),
    (3, 4, 2, 2),
    (2, 5, 3, 1),
    (1, 6, 4, 1);
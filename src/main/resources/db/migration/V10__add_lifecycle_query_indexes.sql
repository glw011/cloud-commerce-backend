CREATE INDEX idx_reservations_status_expires
    ON inventory_reservations (status, expires_at);

CREATE INDEX idx_orders_customer_created
    ON orders (customer_id, created_at DESC);

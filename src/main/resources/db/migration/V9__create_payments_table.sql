CREATE TABLE payments (
    id NUMBER AUTO_INCREMENT PRIMARY KEY,
    order_id NUMBER NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    payment_method VARCHAR2(20) NOT NULL,
    payment_status VARCHAR2(20) NOT NULL,
    payment_date TIMESTAMP,
    transaction_id VARCHAR2(255),
    gateway_response VARCHAR2(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR2(255),
    updated_by VARCHAR2(255),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    UNIQUE (order_id),
    UNIQUE (transaction_id)
);

CREATE INDEX idx_payment_order ON payments(order_id);
CREATE INDEX idx_payment_transaction ON payments(transaction_id);
CREATE INDEX idx_payment_status ON payments(payment_status);


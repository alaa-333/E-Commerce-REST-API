CREATE TABLE orders (
    id NUMBER AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR2(30) NOT NULL UNIQUE,
    customer_id NUMBER,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    total_amount NUMERIC(12,2) NOT NULL,
    order_status VARCHAR2(20) DEFAULT 'PENDING' NOT NULL,
    shipping_city VARCHAR2(100),
    shipping_street VARCHAR2(255),
    shipping_postal_code VARCHAR2(20),
    shipping_country VARCHAR2(100),
    notes VARCHAR2(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR2(255),
    updated_by VARCHAR2(255),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE INDEX idx_order_number ON orders(order_number);
CREATE INDEX idx_order_customer ON orders(customer_id);
CREATE INDEX idx_order_status ON orders(order_status);
CREATE INDEX idx_order_date ON orders(order_date);

CREATE TABLE order_items (
    id NUMBER AUTO_INCREMENT PRIMARY KEY,
    order_id NUMBER NOT NULL,
    product_id NUMBER NOT NULL,
    quantity INT NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR2(255),
    updated_by VARCHAR2(255),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT unique_order_product UNIQUE (order_id, product_id)
);


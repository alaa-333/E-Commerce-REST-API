CREATE TABLE wishlists (
    id NUMBER AUTO_INCREMENT PRIMARY KEY,
    customer_id NUMBER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR2(255),
    updated_by VARCHAR2(255),
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    UNIQUE KEY unique_customer_wishlist (customer_id)
);

CREATE TABLE wishlist_items (
    id NUMBER AUTO_INCREMENT PRIMARY KEY,
    wishlist_id NUMBER NOT NULL,
    product_id NUMBER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR2(255),
    updated_by VARCHAR2(255),
    FOREIGN KEY (wishlist_id) REFERENCES wishlists(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT unique_wishlist_product UNIQUE (wishlist_id, product_id)
);


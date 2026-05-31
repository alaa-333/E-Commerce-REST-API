CREATE TABLE customers (

            id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
            user_id NUMBER NOT NULL UNIQUE ,
            phone VARCHAR2(255) NOT NULL ,
            city VARCHAR2(255) NOT NULL ,
            street VARCHAR2(255) NOT NULL ,
            postal_code VARCHAR2(255) NOT NULL ,
            country VARCHAR2(255) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            created_by VARCHAR2(255),
            updated_by VARCHAR2(255),

    CONSTRAINT FK_CUSTOMERS_USER FOREIGN KEY (user_id) references users(id)
);
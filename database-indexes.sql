-- ============================================
-- E-Commerce Database Indexing Strategy
-- Oracle Database 19c+
-- Implementation Script
-- ============================================
-- 
-- Purpose: Create optimized indexes for all tables
-- Author: Database Team
-- Date: January 2026
-- Version: 1.0
--
-- Usage:
--   1. Review indexes before execution
--   2. Execute during maintenance window
--   3. Monitor performance after creation
--   4. Verify index usage after 1 week
-- ============================================

-- ============================================
-- SECTION 1: USERS TABLE INDEXES
-- ============================================
-- Table: users
-- Purpose: Authentication and user management
-- Query Patterns: Login, user lookup, registration reports

-- Email index (CRITICAL - used in authentication)
-- Unique constraint ensures no duplicate emails
CREATE UNIQUE INDEX idx_users_email ON users(email);

-- Created at index (for registration reports and analytics)
CREATE INDEX idx_users_created_at ON users(created_at);

-- Enabled status (bitmap index for Oracle - low cardinality)
-- Used for filtering active/inactive users
CREATE BITMAP INDEX idx_users_enabled ON users(enabled);

-- Account locked status (bitmap index)
CREATE BITMAP INDEX idx_users_account_locked ON users(account_non_locked);

COMMIT;

-- ============================================
-- SECTION 2: CUSTOMERS TABLE INDEXES
-- ============================================
-- Table: customers
-- Purpose: Customer information and relationships
-- Query Patterns: Customer lookup, search, geographic filtering

-- User ID foreign key (CRITICAL - one-to-one relationship)
CREATE INDEX idx_customers_user_id ON customers(user_id);

-- Phone number lookup (customer service queries)
CREATE INDEX idx_customers_phone ON customers(phone_number);

-- Name search (last name first for better selectivity)
-- Supports queries like: WHERE last_name = 'Smith' AND first_name = 'John'
CREATE INDEX idx_customers_name ON customers(last_name, first_name);

-- City for geographic filtering and shipping analytics
CREATE INDEX idx_customers_city ON customers(city);

-- State for regional reports
CREATE INDEX idx_customers_state ON customers(state);

-- Composite index for full address search
CREATE INDEX idx_customers_location ON customers(country, state, city);

COMMIT;

-- ============================================
-- SECTION 3: PRODUCTS TABLE INDEXES
-- ============================================
-- Table: products
-- Purpose: Product catalog and inventory
-- Query Patterns: Search, filter, sort, stock management

-- Active status (bitmap index - filters out inactive products)
-- Used with @Where(clause = "active=true") in entity
CREATE BITMAP INDEX idx_products_active ON products(active);

-- Composite: category + price (common query pattern)
-- Supports: SELECT * FROM products WHERE category = 'Electronics' ORDER BY price
CREATE INDEX idx_products_category_price ON products(category, price);

-- Function-based: case-insensitive name search
-- Supports: WHERE UPPER(name) LIKE UPPER('%headphone%')
CREATE INDEX idx_products_name_upper ON products(UPPER(name));

-- Stock quantity (inventory management and low stock alerts)
CREATE INDEX idx_products_stock ON products(stock_quantity);

-- Partial index: active products by category (reduces index size)
-- Only indexes active products
CREATE INDEX idx_products_active_category 
ON products(active, category) 
WHERE active = 1;

-- Composite: category + active + price (covering index)
-- Optimizes filtered product listings
CREATE INDEX idx_products_catalog 
ON products(category, active, price, stock_quantity);

COMMIT;

-- ============================================
-- SECTION 4: ORDERS TABLE INDEXES
-- ============================================
-- Table: orders
-- Purpose: Order management and tracking
-- Query Patterns: Customer orders, status filtering, date ranges

-- Customer ID foreign key (CRITICAL - high query frequency)
-- Used in: findByCustomerId, customer order history
CREATE INDEX idx_orders_customer_id ON orders(customer_id);

-- Order status (bitmap index - low cardinality)
-- Used in: findByOrderStatus, admin dashboard
CREATE BITMAP INDEX idx_orders_status ON orders(order_status);

-- Order date (time-based queries and reports)
CREATE INDEX idx_orders_date ON orders(order_date);

-- Composite: customer + date (DESC for recent orders first)
-- Optimizes: "Show me customer's recent orders"
CREATE INDEX idx_orders_customer_date 
ON orders(customer_id, order_date DESC);

-- Composite: status + date (admin queries)
-- Optimizes: "Show pending orders by date"
CREATE INDEX idx_orders_status_date 
ON orders(order_status, order_date DESC);

-- Composite: customer + status (customer's pending orders)
CREATE INDEX idx_orders_customer_status 
ON orders(customer_id, order_status);

-- Total amount (for revenue queries and analytics)
CREATE INDEX idx_orders_total_amount ON orders(total_amount);

COMMIT;

-- ============================================
-- SECTION 5: ORDER_ITEMS TABLE INDEXES
-- ============================================
-- Table: order_items
-- Purpose: Order line items and product relationships
-- Query Patterns: Order details, product analytics

-- Order ID foreign key (CRITICAL - very high query frequency)
-- Used in: Fetching items for an order
CREATE INDEX idx_order_items_order_id ON order_items(order_id);

-- Product ID foreign key (CRITICAL - product analytics)
-- Used in: Product sales reports, popular products
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

-- Unique composite: prevent duplicate items in same order
-- Also optimizes queries using both columns
CREATE UNIQUE INDEX idx_order_items_order_product 
ON order_items(order_id, product_id);

-- Composite: product + quantity (sales analytics)
CREATE INDEX idx_order_items_product_qty 
ON order_items(product_id, quantity);

COMMIT;

-- ============================================
-- SECTION 6: PAYMENTS TABLE INDEXES
-- ============================================
-- Table: payments
-- Purpose: Payment processing and financial records
-- Query Patterns: Payment lookup, status tracking, reports

-- Order ID (CRITICAL - unique, one-to-one relationship)
-- Used in: findByOrderId
CREATE UNIQUE INDEX idx_payments_order_id ON payments(order_id);

-- Transaction ID (CRITICAL - unique, gateway integration)
-- Used in: findByTransactionId, refund processing
CREATE UNIQUE INDEX idx_payments_transaction_id ON payments(transaction_id);

-- Payment status (bitmap index - low cardinality)
-- Used in: Payment status reports, failed payment tracking
CREATE BITMAP INDEX idx_payments_status ON payments(payment_status);

-- Payment date (financial reports and analytics)
CREATE INDEX idx_payments_date ON payments(payment_date);

-- Composite: method + status (payment method analytics)
-- Optimizes: Success rate by payment method
CREATE INDEX idx_payments_method_status 
ON payments(payment_method, payment_status);

-- Composite: status + date (pending payments by date)
CREATE INDEX idx_payments_status_date 
ON payments(payment_status, payment_date DESC);

-- Amount (for revenue queries)
CREATE INDEX idx_payments_amount ON payments(amount);

COMMIT;

-- ============================================
-- VERIFICATION QUERIES
-- ============================================

-- Check all created indexes
SELECT 
    table_name,
    index_name,
    index_type,
    uniqueness,
    status,
    num_rows,
    distinct_keys
FROM user_indexes
WHERE table_name IN (
    'USERS', 'CUSTOMERS', 'PRODUCTS', 
    'ORDERS', 'ORDER_ITEMS', 'PAYMENTS'
)
ORDER BY table_name, index_name;

-- Check index columns and their positions
SELECT 
    ic.table_name,
    ic.index_name,
    ic.column_name,
    ic.column_position,
    i.index_type,
    i.uniqueness
FROM user_ind_columns ic
JOIN user_indexes i ON ic.index_name = i.index_name
WHERE ic.table_name IN (
    'USERS', 'CUSTOMERS', 'PRODUCTS', 
    'ORDERS', 'ORDER_ITEMS', 'PAYMENTS'
)
ORDER BY ic.table_name, ic.index_name, ic.column_position;

-- Count indexes per table
SELECT 
    table_name,
    COUNT(*) as index_count
FROM user_indexes
WHERE table_name IN (
    'USERS', 'CUSTOMERS', 'PRODUCTS', 
    'ORDERS', 'ORDER_ITEMS', 'PAYMENTS'
)
GROUP BY table_name
ORDER BY table_name;

-- ============================================
-- INDEX MONITORING SETUP
-- ============================================

-- Enable monitoring for critical indexes
ALTER INDEX idx_users_email MONITORING USAGE;
ALTER INDEX idx_customers_user_id MONITORING USAGE;
ALTER INDEX idx_orders_customer_id MONITORING USAGE;
ALTER INDEX idx_order_items_order_id MONITORING USAGE;
ALTER INDEX idx_payments_order_id MONITORING USAGE;
ALTER INDEX idx_payments_transaction_id MONITORING USAGE;

-- Check monitoring status
SELECT 
    index_name,
    table_name,
    monitoring,
    used
FROM v$object_usage
WHERE index_name IN (
    'IDX_USERS_EMAIL',
    'IDX_CUSTOMERS_USER_ID',
    'IDX_ORDERS_CUSTOMER_ID',
    'IDX_ORDER_ITEMS_ORDER_ID',
    'IDX_PAYMENTS_ORDER_ID',
    'IDX_PAYMENTS_TRANSACTION_ID'
);

-- ============================================
-- STATISTICS GATHERING
-- ============================================

-- Gather statistics for all tables
BEGIN
    DBMS_STATS.GATHER_TABLE_STATS(USER, 'USERS');
    DBMS_STATS.GATHER_TABLE_STATS(USER, 'CUSTOMERS');
    DBMS_STATS.GATHER_TABLE_STATS(USER, 'PRODUCTS');
    DBMS_STATS.GATHER_TABLE_STATS(USER, 'ORDERS');
    DBMS_STATS.GATHER_TABLE_STATS(USER, 'ORDER_ITEMS');
    DBMS_STATS.GATHER_TABLE_STATS(USER, 'PAYMENTS');
END;
/

-- Gather index statistics
BEGIN
    FOR idx IN (
        SELECT index_name 
        FROM user_indexes 
        WHERE table_name IN (
            'USERS', 'CUSTOMERS', 'PRODUCTS', 
            'ORDERS', 'ORDER_ITEMS', 'PAYMENTS'
        )
    ) LOOP
        DBMS_STATS.GATHER_INDEX_STATS(
            ownname => USER,
            indname => idx.index_name
        );
    END LOOP;
END;
/

-- ============================================
-- PERFORMANCE TESTING QUERIES
-- ============================================

-- Test 1: User login (should use idx_users_email)
EXPLAIN PLAN FOR
SELECT * FROM users WHERE email = 'user@example.com';
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- Test 2: Customer orders (should use idx_orders_customer_id)
EXPLAIN PLAN FOR
SELECT * FROM orders WHERE customer_id = 1 ORDER BY order_date DESC;
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- Test 3: Product search (should use idx_products_category_price)
EXPLAIN PLAN FOR
SELECT * FROM products 
WHERE category = 'Electronics' 
AND active = 1
ORDER BY price;
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- Test 4: Order items (should use idx_order_items_order_id)
EXPLAIN PLAN FOR
SELECT * FROM order_items WHERE order_id = 1;
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- Test 5: Payment lookup (should use idx_payments_transaction_id)
EXPLAIN PLAN FOR
SELECT * FROM payments WHERE transaction_id = 'pi_123456789';
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- ============================================
-- MAINTENANCE PROCEDURES
-- ============================================

-- Procedure to rebuild fragmented indexes
CREATE OR REPLACE PROCEDURE rebuild_fragmented_indexes(
    p_fragmentation_threshold NUMBER DEFAULT 20
) AS
    v_sql VARCHAR2(1000);
BEGIN
    FOR idx IN (
        SELECT index_name
        FROM user_indexes
        WHERE table_name IN (
            'USERS', 'CUSTOMERS', 'PRODUCTS', 
            'ORDERS', 'ORDER_ITEMS', 'PAYMENTS'
        )
        AND (clustering_factor / NULLIF(num_rows, 0)) * 100 > p_fragmentation_threshold
    ) LOOP
        v_sql := 'ALTER INDEX ' || idx.index_name || ' REBUILD ONLINE';
        EXECUTE IMMEDIATE v_sql;
        DBMS_OUTPUT.PUT_LINE('Rebuilt index: ' || idx.index_name);
    END LOOP;
END;
/

-- Procedure to gather statistics for all e-commerce tables
CREATE OR REPLACE PROCEDURE gather_ecommerce_stats AS
BEGIN
    DBMS_STATS.GATHER_SCHEMA_STATS(
        ownname => USER,
        options => 'GATHER AUTO',
        estimate_percent => DBMS_STATS.AUTO_SAMPLE_SIZE,
        method_opt => 'FOR ALL COLUMNS SIZE AUTO',
        degree => 4
    );
    DBMS_OUTPUT.PUT_LINE('Statistics gathered successfully');
END;
/

-- ============================================
-- ROLLBACK SCRIPT (IF NEEDED)
-- ============================================

/*
-- Uncomment to drop all indexes (CAUTION!)

-- Users indexes
DROP INDEX idx_users_email;
DROP INDEX idx_users_created_at;
DROP INDEX idx_users_enabled;
DROP INDEX idx_users_account_locked;

-- Customers indexes
DROP INDEX idx_customers_user_id;
DROP INDEX idx_customers_phone;
DROP INDEX idx_customers_name;
DROP INDEX idx_customers_city;
DROP INDEX idx_customers_state;
DROP INDEX idx_customers_location;

-- Products indexes
DROP INDEX idx_products_active;
DROP INDEX idx_products_category_price;
DROP INDEX idx_products_name_upper;
DROP INDEX idx_products_stock;
DROP INDEX idx_products_active_category;
DROP INDEX idx_products_catalog;

-- Orders indexes
DROP INDEX idx_orders_customer_id;
DROP INDEX idx_orders_status;
DROP INDEX idx_orders_date;
DROP INDEX idx_orders_customer_date;
DROP INDEX idx_orders_status_date;
DROP INDEX idx_orders_customer_status;
DROP INDEX idx_orders_total_amount;

-- Order items indexes
DROP INDEX idx_order_items_order_id;
DROP INDEX idx_order_items_product_id;
DROP INDEX idx_order_items_order_product;
DROP INDEX idx_order_items_product_qty;

-- Payments indexes
DROP INDEX idx_payments_order_id;
DROP INDEX idx_payments_transaction_id;
DROP INDEX idx_payments_status;
DROP INDEX idx_payments_date;
DROP INDEX idx_payments_method_status;
DROP INDEX idx_payments_status_date;
DROP INDEX idx_payments_amount;

COMMIT;
*/

-- ============================================
-- COMPLETION MESSAGE
-- ============================================

SELECT 'Index creation completed successfully!' as status FROM dual;
SELECT 'Total indexes created: ' || COUNT(*) as summary
FROM user_indexes
WHERE table_name IN (
    'USERS', 'CUSTOMERS', 'PRODUCTS', 
    'ORDERS', 'ORDER_ITEMS', 'PAYMENTS'
)
AND index_name LIKE 'IDX_%';

-- ============================================
-- END OF SCRIPT
-- ============================================

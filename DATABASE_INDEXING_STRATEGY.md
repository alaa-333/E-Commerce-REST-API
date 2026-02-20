# Database Indexing Strategy

## 📋 Table of Contents

- [Overview](#overview)
- [Indexing Principles](#indexing-principles)
- [Current Index Analysis](#current-index-analysis)
- [Recommended Index Strategy](#recommended-index-strategy)
- [Index Implementation](#index-implementation)
- [Performance Monitoring](#performance-monitoring)
- [Maintenance Guidelines](#maintenance-guidelines)
- [Best Practices](#best-practices)

---

## Overview

This document outlines the comprehensive database indexing strategy for the E-Commerce REST API. Proper indexing is critical for:

- **Query Performance**: Reducing query execution time
- **Scalability**: Handling increased data volume efficiently
- **User Experience**: Faster response times for API endpoints
- **Resource Optimization**: Reducing CPU and I/O overhead

### Database Context

- **Database**: Oracle Database 19c+
- **ORM**: Hibernate 6.x with Spring Data JPA
- **Connection Pool**: HikariCP
- **Batch Processing**: Enabled (batch_size: 50)

---

## Indexing Principles

### 1. When to Create an Index

✅ **Create indexes for:**
- Primary keys (automatic)
- Foreign keys (critical for joins)
- Columns frequently used in WHERE clauses
- Columns used in ORDER BY clauses
- Columns used in JOIN conditions
- Columns with high selectivity (many unique values)
- Columns used in GROUP BY operations

❌ **Avoid indexes for:**
- Small tables (< 1000 rows)
- Columns with low cardinality (few unique values)
- Columns frequently updated
- Tables with high insert/update ratio
- Columns rarely used in queries

### 2. Index Types

| Index Type | Use Case | Example |
|:-----------|:---------|:--------|
| **B-Tree** | General purpose, range queries | `price BETWEEN 10 AND 100` |
| **Unique** | Enforce uniqueness, fast lookups | `email`, `orderNumber` |
| **Composite** | Multi-column queries | `(category, price)` |
| **Function-Based** | Queries on expressions | `UPPER(email)` |
| **Bitmap** | Low cardinality columns (Oracle) | `orderStatus`, `active` |

### 3. Index Selectivity

**High Selectivity** (Good for indexing):
- Email addresses
- Order numbers
- Transaction IDs
- User IDs

**Low Selectivity** (Consider bitmap or skip):
- Boolean flags (active/inactive)
- Status enums with few values
- Gender fields

---

## Current Index Analysis

### Existing Indexes (From Entity Annotations)

#### ✅ Products Table
```java
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_price", columnList = "price"),
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_created_at", columnList = "created_at")
    }
)
```
**Status**: Good coverage for common queries

#### ✅ Customers Table
```java
@Table(
    name = "customers",
    indexes = {
        @Index(name = "idx_customer_created_at", columnList = "created_at")
    }
)
```
**Status**: Minimal - needs enhancement

#### ✅ Orders Table
```java
@Table(
    name = "orders",
    indexes = {
        @Index(name = "idx_order_number", columnList = "order_number")
    }
)
```
**Status**: Minimal - needs enhancement

#### ❌ Users Table
**Status**: No explicit indexes defined (only PK)

#### ❌ Order Items Table
**Status**: No explicit indexes defined (only PK)

#### ❌ Payments Table
**Status**: No explicit indexes defined (only PK)

---

## Recommended Index Strategy

### 1. Users Table

#### Current State
```sql
-- Automatic indexes
PK: id
```

#### Recommended Indexes

**A. Email Index (Critical)**
```sql
CREATE UNIQUE INDEX idx_users_email ON users(email);
```
**Justification**:
- Used in authentication (`findByEmail`)
- Used in user lookup (`existsByEmail`)
- High selectivity (unique constraint)
- Frequent access pattern

**B. Created At Index**
```sql
CREATE INDEX idx_users_created_at ON users(created_at);
```
**Justification**:
- Used for user registration reports
- Sorting by registration date
- Time-based analytics

**C. Enabled Status Index (Bitmap)**
```sql
CREATE BITMAP INDEX idx_users_enabled ON users(enabled);
```
**Justification**:
- Low cardinality (true/false)
- Used for filtering active users
- Oracle bitmap index efficient for this

#### Query Impact Analysis

| Query Pattern | Before Index | After Index | Improvement |
|:--------------|:-------------|:------------|:------------|
| `findByEmail` | Full table scan | Index seek | 95%+ |
| `existsByEmail` | Full table scan | Index seek | 95%+ |
| User reports by date | Full scan + sort | Index range scan | 80%+ |

---

### 2. Customers Table

#### Current State
```sql
PK: id
FK: user_id
INDEX: idx_customer_created_at (created_at)
```

#### Recommended Indexes

**A. User ID Foreign Key Index (Critical)**
```sql
CREATE INDEX idx_customers_user_id ON customers(user_id);
```
**Justification**:
- Foreign key to users table
- Used in JOIN operations
- One-to-one relationship lookup

**B. Phone Number Index**
```sql
CREATE INDEX idx_customers_phone ON customers(phone_number);
```
**Justification**:
- Potential search/lookup field
- Customer service queries
- Moderate selectivity

**C. Composite Index for Search**
```sql
CREATE INDEX idx_customers_name_search 
ON customers(last_name, first_name);
```
**Justification**:
- Common search pattern (last name first)
- Supports partial matches
- Customer lookup queries

**D. Address City Index**
```sql
CREATE INDEX idx_customers_city ON customers(city);
```
**Justification**:
- Geographic filtering
- Regional reports
- Shipping analytics

#### Query Impact Analysis

| Query Pattern | Before Index | After Index | Improvement |
|:--------------|:-------------|:------------|:------------|
| Find by user_id | Full table scan | Index seek | 90%+ |
| Search by name | Full table scan | Index range scan | 85%+ |
| Filter by city | Full table scan | Index range scan | 80%+ |

---

### 3. Products Table

#### Current State
```sql
PK: id
INDEX: idx_price (price)
INDEX: idx_category (category)
INDEX: idx_created_at (created_at)
```

#### Recommended Indexes

**A. Active Status Index (Bitmap)**
```sql
CREATE BITMAP INDEX idx_products_active ON products(active);
```
**Justification**:
- Low cardinality (true/false)
- Used with @Where clause
- Filters out inactive products

**B. Composite Index for Category + Price**
```sql
CREATE INDEX idx_products_category_price 
ON products(category, price);
```
**Justification**:
- Common query pattern: filter by category, sort by price
- Supports `findByCategory` with price sorting
- More efficient than separate indexes

**C. Name Search Index (Function-Based)**
```sql
CREATE INDEX idx_products_name_upper 
ON products(UPPER(name));
```
**Justification**:
- Supports case-insensitive search
- Used in `findByNameStartingWithIgnoreCase`
- Improves search performance

**D. Stock Quantity Index**
```sql
CREATE INDEX idx_products_stock_quantity 
ON products(stock_quantity);
```
**Justification**:
- Used in stock availability checks
- Low stock alerts
- Inventory management queries

**E. Composite Index for Active Products by Category**
```sql
CREATE INDEX idx_products_active_category 
ON products(active, category) WHERE active = true;
```
**Justification**:
- Partial index for active products only
- Reduces index size
- Optimizes most common query pattern

#### Query Impact Analysis

| Query Pattern | Before Index | After Index | Improvement |
|:--------------|:-------------|:------------|:------------|
| `findByCategory` | Index scan | Composite index | 40%+ |
| `findByPriceBetween` | Index range scan | Optimized range | 30%+ |
| `findByNameStartingWith` | Full scan | Function index | 90%+ |
| Stock check queries | Full scan | Index seek | 85%+ |

---

### 4. Orders Table

#### Current State
```sql
PK: id
FK: customer_id
INDEX: idx_order_number (order_number)
```

#### Recommended Indexes

**A. Customer ID Foreign Key Index (Critical)**
```sql
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
```
**Justification**:
- Foreign key to customers table
- Used in `findByCustomerId`
- Used in JOIN operations
- High query frequency

**B. Order Status Index (Bitmap)**
```sql
CREATE BITMAP INDEX idx_orders_status ON orders(order_status);
```
**Justification**:
- Low cardinality (PENDING, PROCESSING, SHIPPED, etc.)
- Used in `findByOrderStatus`
- Status-based filtering

**C. Order Date Index**
```sql
CREATE INDEX idx_orders_order_date ON orders(order_date);
```
**Justification**:
- Time-based queries
- Date range filtering
- Order history reports

**D. Composite Index for Customer Orders by Date**
```sql
CREATE INDEX idx_orders_customer_date 
ON orders(customer_id, order_date DESC);
```
**Justification**:
- Common pattern: customer's recent orders
- Supports sorting by date
- Optimizes customer order history

**E. Composite Index for Status + Date**
```sql
CREATE INDEX idx_orders_status_date 
ON orders(order_status, order_date DESC);
```
**Justification**:
- Admin dashboard queries
- Pending orders by date
- Status-based reports

#### Query Impact Analysis

| Query Pattern | Before Index | After Index | Improvement |
|:--------------|:-------------|:------------|:------------|
| `findByCustomerId` | Full table scan | Index seek | 95%+ |
| `findByOrderStatus` | Full table scan | Bitmap index | 90%+ |
| Customer order history | Full scan + sort | Composite index | 85%+ |
| Status reports | Full scan | Composite index | 80%+ |

---

### 5. Order Items Table

#### Current State
```sql
PK: id
FK: order_id
FK: product_id
```

#### Recommended Indexes

**A. Order ID Foreign Key Index (Critical)**
```sql
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
```
**Justification**:
- Foreign key to orders table
- Used in JOIN operations
- Fetching items for an order
- Very high query frequency

**B. Product ID Foreign Key Index (Critical)**
```sql
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
```
**Justification**:
- Foreign key to products table
- Product sales analytics
- Inventory tracking
- Popular product queries

**C. Composite Index for Order + Product**
```sql
CREATE UNIQUE INDEX idx_order_items_order_product 
ON order_items(order_id, product_id);
```
**Justification**:
- Prevents duplicate items in same order
- Optimizes order item lookup
- Supports both foreign keys

#### Query Impact Analysis

| Query Pattern | Before Index | After Index | Improvement |
|:--------------|:-------------|:------------|:------------|
| Fetch order items | Full table scan | Index seek | 95%+ |
| Product sales analytics | Full table scan | Index seek | 90%+ |
| Duplicate prevention | Full scan | Unique index | 100% |

---

### 6. Payments Table

#### Current State
```sql
PK: id
FK: order_id (unique)
UNIQUE: transaction_id
```

#### Recommended Indexes

**A. Order ID Foreign Key Index (Critical)**
```sql
CREATE UNIQUE INDEX idx_payments_order_id ON payments(order_id);
```
**Justification**:
- Foreign key to orders table
- One-to-one relationship
- Used in `findByOrderId`
- Ensures one payment per order

**B. Transaction ID Index (Already Unique)**
```sql
-- Already defined in entity
@Column(name = "transaction_id", unique = true)
```
**Justification**:
- Used in `findByTransactionId`
- Payment gateway lookups
- Refund processing

**C. Payment Status Index (Bitmap)**
```sql
CREATE BITMAP INDEX idx_payments_status ON payments(payment_status);
```
**Justification**:
- Low cardinality (PENDING, CONFIRMED, FAILED, etc.)
- Payment status reports
- Failed payment tracking

**D. Payment Date Index**
```sql
CREATE INDEX idx_payments_payment_date ON payments(payment_date);
```
**Justification**:
- Financial reports
- Date range queries
- Revenue analytics

**E. Composite Index for Payment Method + Status**
```sql
CREATE INDEX idx_payments_method_status 
ON payments(payment_method, payment_status);
```
**Justification**:
- Payment method analytics
- Success rate by method
- Gateway performance tracking

#### Query Impact Analysis

| Query Pattern | Before Index | After Index | Improvement |
|:--------------|:-------------|:------------|:------------|
| `findByOrderId` | Full table scan | Unique index | 95%+ |
| `findByTransactionId` | Full table scan | Unique index | 95%+ |
| Payment reports | Full scan | Composite index | 85%+ |
| Status filtering | Full scan | Bitmap index | 90%+ |

---

## Index Implementation

### Option 1: JPA Entity Annotations (Recommended)

Update entity classes with index definitions:

#### Users Entity
```java
@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_users_email", columnList = "email", unique = true),
        @Index(name = "idx_users_created_at", columnList = "created_at"),
        @Index(name = "idx_users_enabled", columnList = "enabled")
    }
)
public class User implements UserDetails {
    // ... entity fields
}
```

#### Customers Entity
```java
@Entity
@Table(
    name = "customers",
    indexes = {
        @Index(name = "idx_customers_user_id", columnList = "user_id"),
        @Index(name = "idx_customers_phone", columnList = "phone_number"),
        @Index(name = "idx_customers_name", columnList = "last_name, first_name"),
        @Index(name = "idx_customers_city", columnList = "city"),
        @Index(name = "idx_customers_created_at", columnList = "created_at")
    }
)
public class Customer {
    // ... entity fields
}
```

#### Products Entity
```java
@Entity
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_products_price", columnList = "price"),
        @Index(name = "idx_products_category", columnList = "category"),
        @Index(name = "idx_products_created_at", columnList = "created_at"),
        @Index(name = "idx_products_active", columnList = "active"),
        @Index(name = "idx_products_category_price", columnList = "category, price"),
        @Index(name = "idx_products_stock", columnList = "stock_quantity")
    }
)
public class Product {
    // ... entity fields
}
```

#### Orders Entity
```java
@Entity
@Table(
    name = "orders",
    indexes = {
        @Index(name = "idx_orders_order_number", columnList = "order_number", unique = true),
        @Index(name = "idx_orders_customer_id", columnList = "customer_id"),
        @Index(name = "idx_orders_status", columnList = "order_status"),
        @Index(name = "idx_orders_date", columnList = "order_date"),
        @Index(name = "idx_orders_customer_date", columnList = "customer_id, order_date"),
        @Index(name = "idx_orders_status_date", columnList = "order_status, order_date")
    }
)
public class Order {
    // ... entity fields
}
```

#### OrderItems Entity
```java
@Entity
@Table(
    name = "order_items",
    indexes = {
        @Index(name = "idx_order_items_order_id", columnList = "order_id"),
        @Index(name = "idx_order_items_product_id", columnList = "product_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_order_items_order_product",
            columnNames = {"order_id", "product_id"}
        )
    }
)
public class OrderItem {
    // ... entity fields
}
```

#### Payments Entity
```java
@Entity
@Table(
    name = "payments",
    indexes = {
        @Index(name = "idx_payments_order_id", columnList = "order_id", unique = true),
        @Index(name = "idx_payments_transaction_id", columnList = "transaction_id", unique = true),
        @Index(name = "idx_payments_status", columnList = "payment_status"),
        @Index(name = "idx_payments_date", columnList = "payment_date"),
        @Index(name = "idx_payments_method_status", columnList = "payment_method, payment_status")
    }
)
public class Payment {
    // ... entity fields
}
```

### Option 2: SQL Migration Scripts

Create a migration script for existing databases:

```sql
-- ============================================
-- E-Commerce Database Indexing Strategy
-- Oracle Database 19c+
-- ============================================

-- ============================================
-- 1. USERS TABLE INDEXES
-- ============================================

-- Email index (unique, high selectivity)
CREATE UNIQUE INDEX idx_users_email ON users(email);

-- Created at index (for reports)
CREATE INDEX idx_users_created_at ON users(created_at);

-- Enabled status (bitmap for Oracle)
CREATE BITMAP INDEX idx_users_enabled ON users(enabled);

-- ============================================
-- 2. CUSTOMERS TABLE INDEXES
-- ============================================

-- User ID foreign key
CREATE INDEX idx_customers_user_id ON customers(user_id);

-- Phone number lookup
CREATE INDEX idx_customers_phone ON customers(phone_number);

-- Name search (last name, first name)
CREATE INDEX idx_customers_name ON customers(last_name, first_name);

-- City for geographic filtering
CREATE INDEX idx_customers_city ON customers(city);

-- ============================================
-- 3. PRODUCTS TABLE INDEXES
-- ============================================

-- Active status (bitmap)
CREATE BITMAP INDEX idx_products_active ON products(active);

-- Composite: category + price
CREATE INDEX idx_products_category_price ON products(category, price);

-- Function-based: case-insensitive name search
CREATE INDEX idx_products_name_upper ON products(UPPER(name));

-- Stock quantity
CREATE INDEX idx_products_stock ON products(stock_quantity);

-- Partial index: active products by category
CREATE INDEX idx_products_active_category 
ON products(active, category) WHERE active = 1;

-- ============================================
-- 4. ORDERS TABLE INDEXES
-- ============================================

-- Customer ID foreign key
CREATE INDEX idx_orders_customer_id ON orders(customer_id);

-- Order status (bitmap)
CREATE BITMAP INDEX idx_orders_status ON orders(order_status);

-- Order date
CREATE INDEX idx_orders_date ON orders(order_date);

-- Composite: customer + date (DESC for recent first)
CREATE INDEX idx_orders_customer_date 
ON orders(customer_id, order_date DESC);

-- Composite: status + date
CREATE INDEX idx_orders_status_date 
ON orders(order_status, order_date DESC);

-- ============================================
-- 5. ORDER_ITEMS TABLE INDEXES
-- ============================================

-- Order ID foreign key
CREATE INDEX idx_order_items_order_id ON order_items(order_id);

-- Product ID foreign key
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

-- Unique composite: prevent duplicate items
CREATE UNIQUE INDEX idx_order_items_order_product 
ON order_items(order_id, product_id);

-- ============================================
-- 6. PAYMENTS TABLE INDEXES
-- ============================================

-- Order ID (unique, one-to-one)
CREATE UNIQUE INDEX idx_payments_order_id ON payments(order_id);

-- Transaction ID (unique)
CREATE UNIQUE INDEX idx_payments_transaction_id ON payments(transaction_id);

-- Payment status (bitmap)
CREATE BITMAP INDEX idx_payments_status ON payments(payment_status);

-- Payment date
CREATE INDEX idx_payments_date ON payments(payment_date);

-- Composite: method + status
CREATE INDEX idx_payments_method_status 
ON payments(payment_method, payment_status);

-- ============================================
-- VERIFY INDEXES
-- ============================================

-- Check all indexes
SELECT 
    table_name,
    index_name,
    index_type,
    uniqueness,
    status
FROM user_indexes
WHERE table_name IN (
    'USERS', 'CUSTOMERS', 'PRODUCTS', 
    'ORDERS', 'ORDER_ITEMS', 'PAYMENTS'
)
ORDER BY table_name, index_name;

-- Check index columns
SELECT 
    ic.table_name,
    ic.index_name,
    ic.column_name,
    ic.column_position,
    i.index_type
FROM user_ind_columns ic
JOIN user_indexes i ON ic.index_name = i.index_name
WHERE ic.table_name IN (
    'USERS', 'CUSTOMERS', 'PRODUCTS', 
    'ORDERS', 'ORDER_ITEMS', 'PAYMENTS'
)
ORDER BY ic.table_name, ic.index_name, ic.column_position;
```

### Option 3: Flyway/Liquibase Migration

#### Flyway Migration (V2__create_indexes.sql)
```sql
-- Flyway migration script
-- Version: 2
-- Description: Create performance indexes

-- Users indexes
CREATE UNIQUE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Customers indexes
CREATE INDEX idx_customers_user_id ON customers(user_id);
CREATE INDEX idx_customers_phone ON customers(phone_number);

-- Products indexes
CREATE INDEX idx_products_category_price ON products(category, price);
CREATE INDEX idx_products_stock ON products(stock_quantity);

-- Orders indexes
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_customer_date ON orders(customer_id, order_date DESC);

-- Order items indexes
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

-- Payments indexes
CREATE UNIQUE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_date ON payments(payment_date);
```

---

## Performance Monitoring

### 1. Index Usage Statistics

Monitor which indexes are being used:

```sql
-- Oracle: Check index usage
SELECT 
    i.index_name,
    i.table_name,
    i.num_rows,
    i.distinct_keys,
    i.clustering_factor,
    i.status,
    s.statistic_name,
    s.value
FROM user_indexes i
LEFT JOIN v$segment_statistics s 
    ON i.index_name = s.object_name
WHERE i.table_name IN (
    'USERS', 'CUSTOMERS', 'PRODUCTS', 
    'ORDERS', 'ORDER_ITEMS', 'PAYMENTS'
)
ORDER BY i.table_name, i.index_name;
```

### 2. Unused Indexes

Identify indexes that are never used:

```sql
-- Find unused indexes (Oracle)
SELECT 
    owner,
    index_name,
    table_name,
    monitoring,
    used
FROM v$object_usage
WHERE used = 'NO'
AND owner = USER;
```

### 3. Query Execution Plans

Analyze query performance:

```sql
-- Enable execution plan
EXPLAIN PLAN FOR
SELECT * FROM orders o
JOIN customers c ON o.customer_id = c.id
WHERE o.order_status = 'PENDING'
ORDER BY o.order_date DESC;

-- View execution plan
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);
```

### 4. Index Fragmentation

Check index health:

```sql
-- Check index fragmentation (Oracle)
SELECT 
    index_name,
    table_name,
    blevel,
    leaf_blocks,
    distinct_keys,
    clustering_factor,
    num_rows,
    ROUND((clustering_factor / num_rows) * 100, 2) as fragmentation_pct
FROM user_indexes
WHERE table_name IN (
    'USERS', 'CUSTOMERS', 'PRODUCTS', 
    'ORDERS', 'ORDER_ITEMS', 'PAYMENTS'
)
ORDER BY fragmentation_pct DESC;
```

---

## Maintenance Guidelines

### 1. Regular Index Maintenance

**Monthly Tasks**:
```sql
-- Rebuild fragmented indexes (>20% fragmentation)
ALTER INDEX idx_orders_customer_date REBUILD ONLINE;

-- Update index statistics
EXEC DBMS_STATS.GATHER_INDEX_STATS(
    ownname => USER,
    indname => 'IDX_ORDERS_CUSTOMER_DATE'
);
```

**Quarterly Tasks**:
```sql
-- Analyze all tables
EXEC DBMS_STATS.GATHER_SCHEMA_STATS(
    ownname => USER,
    options => 'GATHER AUTO',
    estimate_percent => DBMS_STATS.AUTO_SAMPLE_SIZE
);
```

### 2. Index Monitoring

Enable index monitoring:

```sql
-- Enable monitoring for specific index
ALTER INDEX idx_orders_customer_id MONITORING USAGE;

-- Check monitoring status
SELECT * FROM v$object_usage WHERE index_name = 'IDX_ORDERS_CUSTOMER_ID';

-- Disable monitoring
ALTER INDEX idx_orders_customer_id NOMONITORING USAGE;
```

### 3. Index Rebuild Strategy

**When to rebuild**:
- Fragmentation > 20%
- After bulk data loads
- After major data deletions
- Performance degradation observed

**Rebuild script**:
```sql
-- Rebuild online (no downtime)
ALTER INDEX idx_orders_customer_date REBUILD ONLINE;

-- Rebuild with parallel processing
ALTER INDEX idx_products_category_price REBUILD PARALLEL 4;

-- Rebuild all indexes for a table
BEGIN
    FOR idx IN (
        SELECT index_name 
        FROM user_indexes 
        WHERE table_name = 'ORDERS'
    ) LOOP
        EXECUTE IMMEDIATE 'ALTER INDEX ' || idx.index_name || ' REBUILD ONLINE';
    END LOOP;
END;
/
```

---

## Best Practices

### 1. Index Design Principles

✅ **DO**:
- Index foreign keys
- Index columns in WHERE clauses
- Use composite indexes for multi-column queries
- Consider index column order (most selective first)
- Use unique indexes where applicable
- Monitor index usage regularly

❌ **DON'T**:
- Over-index (max 5-7 indexes per table)
- Index low-cardinality columns (except bitmap)
- Index frequently updated columns
- Create redundant indexes
- Ignore index maintenance

### 2. Composite Index Column Order

**Rule**: Most selective column first

```sql
-- Good: High selectivity first
CREATE INDEX idx_orders_customer_date 
ON orders(customer_id, order_date);

-- Bad: Low selectivity first
CREATE INDEX idx_orders_date_customer 
ON orders(order_date, customer_id);
```

### 3. Covering Indexes

Include frequently accessed columns:

```sql
-- Covering index for order summary queries
CREATE INDEX idx_orders_summary 
ON orders(customer_id, order_date, total_amount, order_status);
```

### 4. Partial Indexes

Index only relevant data:

```sql
-- Index only active products
CREATE INDEX idx_products_active_category 
ON products(category, price) 
WHERE active = true;
```

### 5. Function-Based Indexes

Support case-insensitive searches:

```sql
-- Case-insensitive email search
CREATE INDEX idx_users_email_upper 
ON users(UPPER(email));

-- Query must use same function
SELECT * FROM users WHERE UPPER(email) = UPPER('user@example.com');
```

---

## Performance Benchmarks

### Expected Improvements

| Query Type | Before Indexing | After Indexing | Improvement |
|:-----------|:----------------|:---------------|:------------|
| User login (email lookup) | 50-100ms | 1-5ms | 95%+ |
| Customer orders | 200-500ms | 10-20ms | 95%+ |
| Product search | 100-300ms | 5-15ms | 95%+ |
| Order status filter | 150-400ms | 10-25ms | 93%+ |
| Payment lookup | 80-200ms | 2-8ms | 96%+ |

### Load Testing Recommendations

```bash
# Use Apache JMeter or similar tools
# Test scenarios:
1. Concurrent user logins (1000 users)
2. Product catalog browsing (500 concurrent)
3. Order creation (200 concurrent)
4. Payment processing (100 concurrent)
5. Admin dashboard queries (50 concurrent)
```

---

## Summary

### Critical Indexes (Must Have)

1. **users.email** - Authentication
2. **customers.user_id** - User-customer relationship
3. **orders.customer_id** - Customer orders
4. **orders.order_number** - Order lookup
5. **order_items.order_id** - Order details
6. **order_items.product_id** - Product analytics
7. **payments.order_id** - Payment lookup
8. **payments.transaction_id** - Gateway integration

### High-Priority Indexes (Recommended)

1. **products.category** - Product filtering
2. **products.price** - Price sorting
3. **orders.order_status** - Status filtering
4. **orders.order_date** - Date-based queries
5. **payments.payment_status** - Payment reports

### Optional Indexes (Performance Optimization)

1. **products.stock_quantity** - Inventory management
2. **customers.phone_number** - Customer lookup
3. **customers.city** - Geographic filtering
4. **users.created_at** - Registration reports

---

## Implementation Checklist

- [ ] Review current database schema
- [ ] Analyze query patterns from application logs
- [ ] Implement critical indexes first
- [ ] Test query performance before/after
- [ ] Monitor index usage for 1 week
- [ ] Add high-priority indexes
- [ ] Rebuild fragmented indexes
- [ ] Update application.yaml (ddl-auto: validate)
- [ ] Document index strategy
- [ ] Schedule regular maintenance
- [ ] Set up monitoring alerts
- [ ] Train team on index best practices

---

## Additional Resources

- [Oracle Database Performance Tuning Guide](https://docs.oracle.com/en/database/oracle/oracle-database/19/tgdba/)
- [Hibernate Performance Tuning](https://docs.jboss.org/hibernate/orm/6.0/userguide/html_single/Hibernate_User_Guide.html#performance)
- [Spring Data JPA Best Practices](https://spring.io/guides/gs/accessing-data-jpa/)

---

<div align="center">

**Database Indexing Strategy** · E-Commerce REST API

Last Updated: January 2026 · Version 1.0

</div>

<div align="center">

<br/>

<img src="https://img.icons8.com/fluency/96/shopping-cart.png" width="85"/>

<br/>

# E-Commerce REST API

### Enterprise-Grade Backend for Modern Commerce Platforms

<br/>

[![Build Status](https://img.shields.io/badge/Build-In_Development-F4B400?style=for-the-badge&labelColor=2B2B2B)](/)
[![Java](https://img.shields.io/badge/Java-17-E8E8E8?style=for-the-badge&logo=openjdk&logoColor=white&labelColor=ED8B00)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.7-E8E8E8?style=for-the-badge&logo=springboot&logoColor=white&labelColor=6DB33F)](https://spring.io/projects/spring-boot)
[![Oracle](https://img.shields.io/badge/Oracle-Database-E8E8E8?style=for-the-badge&logo=oracle&logoColor=white&labelColor=F80000)](https://www.oracle.com/database/)

<br/>

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

<sub>

*A production-ready RESTful API delivering comprehensive e-commerce functionality*
*with customer management, product catalog, order processing, and payment integration.*

</sub>

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

<br/>

[Overview](#-overview) · [Architecture](#-architecture) · [Quick Start](#-quick-start) · [API Documentation](#-api-documentation) · [Roadmap](#-roadmap)

<br/>

</div>

> **⚠️ Development Status**  
> This project is under active development. Core features are functional and tested, while advanced features are being incrementally rolled out. The API contract may evolve during this phase.

<br/>

---

## ▎Overview

This REST API serves as a comprehensive backend solution for e-commerce applications, implementing industry-standard patterns and best practices for scalability, maintainability, and security.

<br/>

<div align="center">
<table>
<tr>
<td align="center" width="220">
<br/>
<img src="https://img.icons8.com/fluency/48/user.png" width="36"/>
<br/><br/>
<b>Customer Management</b>
<br/>
<sub>Complete CRUD operations<br/>with validation & search</sub>
<br/><br/>
</td>
<td align="center" width="220">
<br/>
<img src="https://img.icons8.com/fluency/48/product.png" width="36"/>
<br/><br/>
<b>Product Catalog</b>
<br/>
<sub>Inventory management<br/>with stock tracking</sub>
<br/><br/>
</td>
<td align="center" width="220">
<br/>
<img src="https://img.icons8.com/fluency/48/order-history.png" width="36"/>
<br/><br/>
<b>Order Processing</b>
<br/>
<sub>End-to-end order lifecycle<br/>with status tracking</sub>
<br/><br/>
</td>
<td align="center" width="220">
<br/>
<img src="https://img.icons8.com/fluency/48/bank-card-back-side.png" width="36"/>
<br/><br/>
<b>Payment Integration</b>
<br/>
<sub>Stripe SDK with<br/>strategy pattern</sub>
<br/><br/>
</td>
</tr>
</table>
</div>

<br/>

---

## ▎Technology Stack

<br/>

<table>
<tr>
<td width="50%" valign="top">

### Core Framework

| Component | Version | Purpose |
|:----------|:-------:|:--------|
| **Spring Boot** | 3.5.7 | Application framework |
| **Spring Data JPA** | — | Data persistence layer |
| **Hibernate** | — | ORM implementation |
| **Jakarta Validation** | — | Input validation |

</td>
<td width="50%" valign="top">

### Infrastructure

| Component | Version | Purpose |
|:----------|:-------:|:--------|
| **Java** | 17 | Runtime environment |
| **Maven** | 3.6+ | Build automation |
| **Oracle Database** | — | Data storage |
| **MapStruct** | 1.5.3 | Object mapping |

</td>
</tr>
<tr>
<td colspan="2">

### Additional Libraries

<table width="100%">
<tr>
<td><b>Lombok</b></td>
<td>Reduces boilerplate code with annotations</td>
</tr>
<tr>
<td><b>Stripe Java SDK</b></td>
<td>Payment processing integration (v24.0.0)</td>
</tr>
</table>

</td>
</tr>
</table>

<br/>

---

## ▎Architecture

### System Design

<div align="center">

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              REST API LAYER                                 │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│   │   Customer   │  │    Order     │  │   Product    │  │   Payment    │   │
│   │  Controller  │  │  Controller  │  │  Controller  │  │  Controller  │   │
│   └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘   │
└──────────┼──────────────────┼──────────────────┼──────────────────┼──────────┘
           │                  │                  │                  │
           │                  │                  │                  │
┌──────────┼──────────────────┼──────────────────┼──────────────────┼──────────┐
│          ▼                  ▼                  ▼                  ▼          │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│   │   Customer   │  │    Order     │  │   Product    │  │   Payment    │   │
│   │   Service    │  │   Service    │  │   Service    │  │   Service    │   │
│   └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘   │
│          │  SERVICE LAYER   │                  │                  │          │
└──────────┼──────────────────┼──────────────────┼──────────────────┼──────────┘
           │                  │                  │                  │
           │                  │                  │                  │
┌──────────┼──────────────────┼──────────────────┼──────────────────┼──────────┐
│          ▼                  ▼                  ▼                  ▼          │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│   │   Customer   │  │    Order     │  │   Product    │  │   Payment    │   │
│   │  Repository  │  │  Repository  │  │  Repository  │  │  Repository  │   │
│   └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘   │
│          │  DATA ACCESS     │                  │                  │          │
└──────────┼──────────────────┼──────────────────┼──────────────────┼──────────┘
           │                  │                  │                  │
           └──────────────────┴──────────────────┴──────────────────┘
                                       │
                                       ▼
                          ┌────────────────────────┐
                          │   Oracle Database      │
                          └────────────────────────┘
```

</div>

<br/>

### Project Structure

```
E-Commerce-REST-API/
│
├── src/main/java/com/e_commerce/E_Commerce/REST/API/
│   │
│   ├── controller/                    ◁ REST API Endpoints
│   │   ├── CustomerController.java
│   │   ├── OrderController.java
│   │   ├── ProductController.java
│   │   └── PaymentController.java
│   │
│   ├── service/                       ◁ Business Logic Layer
│   │   ├── CustomerService.java
│   │   ├── OrderService.java
│   │   ├── ProductService.java
│   │   └── PaymentService.java        [In Progress]
│   │
│   ├── repository/                    ◁ Data Access Layer
│   │   ├── CustomerRepository.java
│   │   ├── OrderRepository.java
│   │   ├── ProductRepository.java
│   │   └── PaymentRepository.java
│   │
│   ├── model/                         ◁ Domain Entities
│   │   ├── Customer.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── Product.java
│   │   └── Payment.java
│   │
│   ├── dto/                           ◁ Data Transfer Objects
│   │   ├── request/
│   │   │   ├── CustomerRequest.java
│   │   │   ├── OrderRequest.java
│   │   │   └── ProductRequest.java
│   │   └── response/
│   │       ├── CustomerResponse.java
│   │       ├── OrderResponse.java
│   │       └── ProductResponse.java
│   │
│   ├── mapper/                        ◁ DTO Converters
│   │   ├── CustomerMapper.java
│   │   ├── OrderMapper.java
│   │   └── ProductMapper.java
│   │
│   ├── payment/                       ◁ Payment Strategies
│   │   ├── PaymentStrategy.java       [In Progress]
│   │   ├── CreditCardPayment.java
│   │   └── CashWalletPayment.java
│   │
│   ├── exception/                     ◁ Error Handling
│   │   ├── GlobalExceptionHandler.java 
│   │   ├── BusinessException.java      
│   │   └── ErrorCode.java             
│   │
│   └── ECommerceRestApiApplication.java
│
├── src/main/resources/
│   ├── application.yaml
│   └── application-prod.yaml
│
└── pom.xml
```

<br/>

---

## ▎Implementation Status

<br/>

<table>
<tr>
<td width="50%" valign="top">

### ✓ Production Ready

- **Customer Management**  
  Complete CRUD with email search
  
- **Product Catalog**  
  Full inventory management
  
- **Order Processing**  
  Order creation and status tracking
  
- **Order Items**  
  Multi-item order support
  
- **Database Layer**  
  Oracle integration with JPA
  
- **DTO Pattern**  
  MapStruct implementation
  
- **Input Validation**  
  Jakarta Validation

</td>
<td width="50%" valign="top">

### ⧗ In Development

- **Payment Service**  
  Stripe integration underway
  
- **Payment Strategies**  
  Credit card & wallet support
  
- **Exception Handling**  
  Global error management
  
- **Business Exceptions**  
  Custom error types
  
- **Error Codes**  
  Standardized error responses

</td>
</tr>
</table>

<br/>

---

## ▎Quick Start

<br/>

### Prerequisites

```
Java 17+          Oracle Database          Maven 3.6+          IDE (IntelliJ/Eclipse/VS Code)
```

<br/>

### Installation & Setup

<table>
<tr>
<td width="40"><b>1</b></td>
<td>

**Clone Repository**
```bash
git clone <repository-url>
cd E-Commerce-REST-API
```
</td>
</tr>
<tr>
<td><b>2</b></td>
<td>

**Configure Database**

Edit `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@//localhost:1521/orclpdb
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: oracle.jdbc.OracleDriver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

server:
  port: 8080
```
</td>
</tr>
<tr>
<td><b>3</b></td>
<td>

**Build Project**
```bash
mvn clean install
```
</td>
</tr>
<tr>
<td><b>4</b></td>
<td>

**Run Application**
```bash
mvn spring-boot:run
```

API available at: `http://localhost:8080/api`
</td>
</tr>
</table>

<br/>

> **💡 Production Deployment**  
> For production environments, use environment variables for sensitive configuration and set `ddl-auto` to `validate` to prevent schema modifications.

<br/>

---

## ▎API Documentation

### Base URL
```
http://localhost:8080/api
```

<br/>

### Customer Endpoints

<table>
<tr>
<th width="80">Method</th>
<th>Endpoint</th>
<th>Description</th>
<th width="80">Status</th>
</tr>
<tr>
<td align="center"><code>POST</code></td>
<td><code>/api/customers</code></td>
<td>Register new customer</td>
<td align="center">✓</td>
</tr>
<tr>
<td align="center"><code>GET</code></td>
<td><code>/api/customers</code></td>
<td>Retrieve all customers</td>
<td align="center">✓</td>
</tr>
<tr>
<td align="center"><code>GET</code></td>
<td><code>/api/customers/{id}</code></td>
<td>Retrieve customer by ID</td>
<td align="center">✓</td>
</tr>
<tr>
<td align="center"><code>GET</code></td>
<td><code>/api/customers/email/{email}</code></td>
<td>Retrieve customer by email</td>
<td align="center">✓</td>
</tr>
<tr>
<td align="center"><code>PUT</code></td>
<td><code>/api/customers/{id}</code></td>
<td>Update customer information</td>
<td align="center">✓</td>
</tr>
</table>

<br/>

### Order Endpoints

<table>
<tr>
<th width="80">Method</th>
<th>Endpoint</th>
<th>Description</th>
<th width="80">Status</th>
</tr>
<tr>
<td align="center"><code>POST</code></td>
<td><code>/api/orders</code></td>
<td>Create new order</td>
<td align="center">✓</td>
</tr>
<tr>
<td align="center"><code>GET</code></td>
<td><code>/api/orders</code></td>
<td>Retrieve all orders</td>
<td align="center">✓</td>
</tr>
<tr>
<td align="center"><code>GET</code></td>
<td><code>/api/orders/customer/{customerId}</code></td>
<td>Retrieve orders by customer</td>
<td align="center">✓</td>
</tr>
<tr>
<td align="center"><code>GET</code></td>
<td><code>/api/orders/status/{status}</code></td>
<td>Retrieve orders by status</td>
<td align="center">✓</td>
</tr>
<tr>
<td align="center"><code>PATCH</code></td>
<td><code>/api/orders/{id}/status</code></td>
<td>Update order status</td>
<td align="center">✓</td>
</tr>
</table>

<br/>

### Product Endpoints

<table>
<tr>
<th width="80">Method</th>
<th>Endpoint</th>
<th>Description</th>
<th width="80">Status</th>
</tr>
<tr>
<td align="center"><code>POST</code></td>
<td><code>/api/products</code></td>
<td>Add new product</td>
<td align="center">✓</td>
</tr>
<tr>
<td align="center"><code>GET</code></td>
<td><code>/api/products</code></td>
<td>Retrieve all products</td>
<td align="center">✓</td>
</tr>
<tr>
<td align="center"><code>GET</code></td>
<td><code>/api/products/{id}</code></td>
<td>Retrieve product by ID</td>
<td align="center">✓</td>
</tr>
<tr>
<td align="center"><code>PUT</code></td>
<td><code>/api/products/{id}</code></td>
<td>Update product information</td>
<td align="center">✓</td>
</tr>
</table>

<br/>

### Payment Endpoints

<table>
<tr>
<th width="80">Method</th>
<th>Endpoint</th>
<th>Description</th>
<th width="80">Status</th>
</tr>
<tr>
<td align="center"><code>POST</code></td>
<td><code>/api/payments</code></td>
<td>Process payment transaction</td>
<td align="center">⧗</td>
</tr>
<tr>
<td align="center"><code>GET</code></td>
<td><code>/api/payments/{id}</code></td>
<td>Retrieve payment by ID</td>
<td align="center">⧗</td>
</tr>
</table>

<br/>

<details>
<summary><b>📄 Request & Response Examples</b></summary>

<br/>

**Create Customer Request**
```http
POST /api/customers HTTP/1.1
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "address": {
    "street": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  }
}
```

**Create Order Request**
```http
POST /api/orders HTTP/1.1
Content-Type: application/json

{
  "customerId": 1,
  "orderItems": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 1
    }
  ]
}
```

</details>

<br/>

---

## ▎Roadmap

<br/>

<div align="center">

**Development Progress**

</div>

```
Phase 1: Core Features          ███████████████░░░░░  75%
Phase 2: Security & Quality     ████░░░░░░░░░░░░░░░░  20%
Phase 3: Performance            ░░░░░░░░░░░░░░░░░░░░   0%
Phase 4: Advanced Features      ░░░░░░░░░░░░░░░░░░░░   0%
```

<br/>

<details>
<summary><b>📋 Detailed Roadmap</b></summary>

<br/>

### Phase 1: Core Features (Q1 2024)

- [x] Customer Management System
- [x] Product Catalog Management
- [x] Order Processing Engine
- [x] Order Items Support
- [x] Database Schema Design
- [ ] Payment Processing Integration
- [ ] Global Exception Handling

<br/>

### Phase 2: Security & Quality (Q2 2024)

- [ ] JWT Authentication & Authorization
- [ ] Role-Based Access Control (RBAC)
- [ ] API Documentation (Swagger/OpenAPI)
- [ ] Unit Testing (JUnit 5, Mockito)
- [ ] Integration Testing
- [ ] Logging Framework (SLF4J/Logback)
- [ ] Security Headers

<br/>

### Phase 3: Performance & Scalability (Q3 2024)

- [ ] Pagination & Filtering
- [ ] Caching Layer (Redis)
- [ ] Rate Limiting
- [ ] Database Query Optimization
- [ ] Connection Pooling (HikariCP)
- [ ] Health Checks & Actuator
- [ ] Application Metrics

<br/>

### Phase 4: Advanced Features (Q4 2024)

- [ ] Email Notification Service
- [ ] Real-Time Order Tracking
- [ ] Inventory Management System
- [ ] Analytics & Reporting Dashboard
- [ ] Webhooks for External Integrations
- [ ] Multi-Currency Support
- [ ] Internationalization (i18n)

</details>

<br/>

---

## ▎Contributing

<br/>

We welcome contributions from the community. Please follow these guidelines:

<table>
<tr>
<td width="40"><b>1</b></td>
<td><b>Review Roadmap</b> — Familiarize yourself with planned features and current priorities</td>
</tr>
<tr>
<td><b>2</b></td>
<td><b>Address Known Issues</b> — Critical bugs take precedence over feature development</td>
</tr>
<tr>
<td><b>3</b></td>
<td><b>Follow Code Standards</b> — Maintain consistency with existing architecture patterns</td>
</tr>
<tr>
<td><b>4</b></td>
<td><b>Write Tests</b> — Include unit and integration tests for new functionality</td>
</tr>
<tr>
<td><b>5</b></td>
<td><b>Update Documentation</b> — Reflect changes in README and inline comments</td>
</tr>
</table>

<br/>

### Development Standards

- Use meaningful commit messages following conventional commits
- Create feature branches from `develop`
- Implement proper input validation for all endpoints
- Use DTOs for all request/response payloads
- Follow RESTful API design principles

<br/>

---

## ▎License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for full details.

<br/>

---

<br/>

<div align="center">

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

<br/>

<sub>

**E-Commerce REST API** · Enterprise Backend Solution

Developed by Alaa Mohamed
</sub>

<br/>

[![Version](https://img.shields.io/badge/Version-0.0.1--SNAPSHOT-2B2B2B?style=flat-square)](/)
[![Java](https://img.shields.io/badge/Java-17-2B2B2B?style=flat-square&logo=openjdk)](/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.7-2B2B2B?style=flat-square&logo=springboot)](/)
[![License](https://img.shields.io/badge/License-MIT-2B2B2B?style=flat-square)](/)

<br/>

<sub>Last Updated: 2024 · Documentation v1.0</sub>

</div>

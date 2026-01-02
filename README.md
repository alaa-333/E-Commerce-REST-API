<div align="center">

<picture>
  <source media="(max-width: 480px)" srcset="https://img.icons8.com/fluency/64/shopping-cart.png">
  <img src="https://img.icons8.com/fluency/96/shopping-cart.png" width="85" alt="E-Commerce API"/>
</picture>

<br/>

# E-Commerce REST API

### Enterprise-Grade Backend for Modern Commerce Platforms

<br/>

<div style="display: flex; flex-wrap: wrap; justify-content: center; gap: 8px;">

[![Build Status](https://img.shields.io/badge/Build-In_Development-F4B400?style=for-the-badge&labelColor=2B2B2B)](/)
[![Java](https://img.shields.io/badge/Java-17-E8E8E8?style=for-the-badge&logo=openjdk&logoColor=white&labelColor=ED8B00)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.7-E8E8E8?style=for-the-badge&logo=springboot&logoColor=white&labelColor=6DB33F)](https://spring.io/projects/spring-boot)
[![Oracle](https://img.shields.io/badge/Oracle-Database-E8E8E8?style=for-the-badge&logo=oracle&logoColor=white&labelColor=F80000)](https://www.oracle.com/database/)

</div>

<br/>

<details>
<summary>📑 <b>Table of Contents</b></summary>

- [Overview](#-overview)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [Implementation Status](#-implementation-status)
- [Quick Start](#-quick-start)
- [API Documentation](#-api-documentation)
- [Roadmap](#-roadmap)
- [Contributing](#-contributing)
- [License](#-license)

</details>

<br/>

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

<div>

*A production-ready RESTful API delivering comprehensive e-commerce functionality with customer management <br/> product catalog, order processing, and payment integration.*

</div>

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

</div>

<br/>

> **⚠️ Development Status**  
> This project is under active development. Core features are functional and tested, while advanced features are being incrementally rolled out. The API contract may evolve during this phase.

<br/>

---

## ▎Overview

This REST API serves as a comprehensive backend solution for e-commerce applications, implementing industry-standard patterns and best practices for scalability, maintainability, and security.

<br/>

### Core Features

<div align="center">

| Feature | Description |
|:--------|:------------|
| 👤 **Customer Management** | Complete CRUD operations with validation & search |
| 📦 **Product Catalog** | Inventory management with stock tracking |
| 🛍️ **Order Processing** | End-to-end order lifecycle with status tracking |
| 💳 **Payment Integration** | Stripe SDK with strategy pattern |
| 📊 **Pagination & Sorting** | Dynamic pagination & navigation with sorting|

</div>

<br/>

---

## ▎Technology Stack

<br/>

### Core Framework

| Component | Version | Purpose |
|:----------|:-------:|:--------|
| **Spring Boot** | 3.5.7 | Application framework |
| **Spring Data JPA** | — | Data persistence layer |
| **Hibernate** | — | ORM implementation |
| **Jakarta Validation** | — | Input validation |

<br/>

### Infrastructure

| Component | Version | Purpose |
|:----------|:-------:|:--------|
| **Java** | 17 | Runtime environment |
| **Maven** | 3.6+ | Build automation |
| **Oracle Database** | — | Data storage |
| **MapStruct** | 1.5.3 | Object mapping |

<br/>

### Additional Libraries

| Library | Purpose |
|:--------|:--------|
| **Lombok** | Reduces boilerplate code with annotations |
| **Stripe Java SDK** | Payment processing integration (v30.0.0) |
| **JJWT** | JWT authentication token handling (v0.13.0) |

<br/>

---

## ▎Architecture


<br/>

### Project Structure

<br/>

```
E-Commerce-REST-API/
│
├── src/main/java/com/e_commerce/E_Commerce/REST/API/
│   │
│   ├── 📂 config/                  # Configuration Layer
│   │   ├── SecurityConfig.java          # Security & Filter chain config
│   │   └── StripeConfig.java            # Stripe API key configuration
│   │
│   ├── 📂 controller/              # REST API Endpoints Layer
│   │   ├── AuthController.java          # Authentication endpoints (Login/Signup)
│   │   ├── CustomerController.java      # Customer CRUD operations
│   │   ├── OrderController.java         # Order management & status updates
│   │   ├── OrderItemController.java     # Order item operations
│   │   ├── PaymentController.java       # Payment operations
│   │   └── ProductController.java       # Product CRUD, search & filtering
│   │
│   ├── 📂 filter/                  # Security Filters
│   │   └── JwtAuthFilter.java           # JWT Authentication Filter
│   │
│   ├── 📂 service/                 # Business Logic Layer
│   │   ├── CustomUserDetailsService.java # User details service for Auth
│   │   ├── CustomerService.java         # Customer business rules
│   │   ├── OrderService.java            # Order processing logic
│   │   ├── OrderItemService.java        # Order item management
│   │   ├── OrderItemValidator.java      # Order item validation logic
│   │   ├── PaymentService.java          # Payment processing
│   │   ├── ProductService.java          # Product management & validation
│   │   └── UserService.java             # User management service
│   │
│   ├── 📂 repository/              # Data Access Layer
│   │   ├── CustomerRepository.java      # Customer data operations
│   │   ├── OrderRepository.java         # Order queries
│   │   ├── OrderItemRepository.java     # Order item queries
│   │   ├── PaymentRepository.java       # Payment data access
│   │   ├── ProductRepository.java       # Product queries & search
│   │   └── UserRepository.java          # User data access
│   │
│   ├── 📂 model/                   # Domain Entities (JPA)
│   │   ├── Customer.java                # Customer entity with address
│   │   ├── Order.java                   # Order entity with status
│   │   ├── OrderItem.java               # Order line items
│   │   ├── Payment.java                 # Payment entity
│   │   ├── Product.java                 # Product catalog entity
│   │   ├── User.java                    # User entity for application users
│   │   ├── Address.java                 # Embedded address value object
│   │   └── enums/
│   │       ├── OrderStatus.java          # Order status enumeration
│   │       |
│   │       ├── Role.java                 # User roles (ADMIN, USER)
│   │       └── WhiteList.java            # Allowed sort fields
│   │
│   ├── 📂 dto/                      # Data Transfer Objects
│   │   ├── request/                     # Input DTOs (API contracts)
│   │   │   ├── AddressRequestDTO.java
│   │   │   ├── CustomerCreateRequestDTO.java
│   │   │   ├── CustomerUpdateReqDTO.java
│   │   │   ├── LoginRequestDto.java
│   │   │   ├── OrderCreateRequestDTO.java
│   │   │   ├── OrderItemCreateRequestDTO.java
│   │   │   ├── OrderItemUpdateRequestDTO.java
│   │   │   ├── OrderUpdateRequestDTO.java
│   │   │   ├── PaginationRequestDto.java
│   │   │   ├── PaymentRequestDTO.java
│   │   │   ├── PaymentUpdateRequestDTO.java
│   │   │   ├── ProductCreateRequestDTO.java
│   │   │   ├── ProductUpdateRequestDTO.java
│   │   │   ├── SignupRequestDto.java
│   │   │   └── UserCreateRequestDto.java
│   │   │
│   │   └── response/                    # Output DTOs (API responses)
│   │       ├── AddressResponseDTO.java
│   │       ├── CustomerResponseDTO.java
│   │       ├── OrderItemResponseDTO.java
│   │       ├── OrderResponseDTO.java
│   │       ├── PaginationResponseDto.java
│   │       ├── PaymentResponseDTO.java
│   │       └── ProductResponseDTO.java
│   │
│   ├── 📂 mapper/                   # Entity-DTO Mapping (MapStruct)
│   │   ├── CustomerMapper.java          # Customer entity ↔ DTO mapping
│   │   ├── OrderMapper.java             # Order entity ↔ DTO mapping
│   │   ├── OrderItemMapper.java         # OrderItem entity ↔ DTO mapping
│   │   ├── PaymentMapper.java           # Payment entity ↔ DTO mapping
│   │   ├── ProductMapper.java           # Product entity ↔ DTO mapping
│   │   └── UserMapper.java              # User entity ↔ DTO mapping
│   │
│   ├── 📂 exception/                # Exception Handling
│   │   ├── GlobalExceptionHandling.java     # Centralized exception handler
│   │   ├── BusinessException.java           # Base business exception
│   │   ├── ResourceNotFoundException.java   # Generic resource not found
│   │   ├── DuplicateResourceException.java  # Duplicate resource handler
│   │   ├── ValidationException.java         # Validation error handler
│   │   ├── ErrorCode.java                   # Error code enumeration
│   │   ├── ErrorResponse.java               # Standardized error response
│   │   │
│   │   ├── customer/                        # Customer-specific exceptions
│   │   ├── order/                           # Order-specific exceptions
│   │   ├── orderItem/                       # OrderItem exceptions
│   │   ├── payment/                         # Payment exceptions
│   │   └── product/                         # Product-specific exceptions
│   │
│   ├── 📂 payment/                   # Payment Strategy Pattern
│   │   ├── PaymentStrategy.java            # Payment strategy interface with PaymentResult
│   │   ├── PaymentStrategyFactory.java     # Factory for selecting payment strategies
│   │   ├── StripePaymentStrategy.java      # Stripe PaymentIntent integration
│   │   ├── PaymentMethod.java              # Payment method enum (STRIPE, etc.)
│   │   └── PaymentStatus.java              # Payment status enum
│   │
│   ├── 📂 util/                      # Utilities
│   │   ├── JwtResponse.java             # JWT response container
│   │   └── JwtTokenUtil.java            # JWT generation & validation utils
│   │
│   └── ECommerceRestApiApplication.java    # Spring Boot main class
│
├── src/main/resources/
│   ├── application.yaml                     # Database & JPA configuration
│   ├── static/                              # Static resources
│   └── templates/                           # Template files
│
├── pom.xml                                  # Maven dependencies & build config
├── DB Diagram.png                           # Database schema diagram
└── README.md                                # Project documentation
```





<br/>

---

## ▎Implementation Status

<br/>

**✓ Production Ready**

- Customer Management — Complete CRUD with email search
- Product Catalog — Full inventory management
- Order Processing — Order creation and status tracking
- Order Items — Multi-item order support
- Database Layer — Oracle integration with JPA
- DTO Pattern — MapStruct implementation
- Input Validation — Jakarta Validation
- Exception Handling — Global error management
- Business Exceptions — Custom error types
- Error Codes — Standardized error responses
- **Payment Service** — Stripe PaymentIntent integration ✓
- **Payment Strategy Pattern** — Extensible payment architecture ✓
- **JWT Authentication** — Secure token-based auth ✓

<br/>

**⧗ In Development**

- Stripe Webhook — Payment confirmation via webhooks
- Additional Payment Methods — PayPal, Apple Pay strategies

<br/>

---

## ▎Quick Start

<br/>

### Prerequisites

```
Java 17+     Oracle Database     Maven 3.6+     IDE
```

<br/>

### Installation Steps

**1. Clone Repository**
```bash
git clone <repository-url>
cd E-Commerce-REST-API
```

<br/>

**2. Configure Database**

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

<br/>

**3. Build Project**
```bash
mvn clean install
```

<br/>

**4. Run Application**
```bash
mvn spring-boot:run
```

API available at: `http://localhost:8080/api`

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

| Method | Endpoint | Description | Status |
|:------:|:---------|:------------|:------:|
| `POST` | `/api/customers` | Register new customer | ✓ |
| `GET` | `/api/customers` | Retrieve all customers | ✓ |
| `GET` | `/api/customers/{id}` | Retrieve customer by ID | ✓ |
| `GET` | `/api/customers/email/{email}` | Retrieve customer by email | ✓ |
| `PUT` | `/api/customers/{id}` | Update customer information | ✓ |

<br/>

### Order Endpoints

| Method | Endpoint | Description | Status |
|:------:|:---------|:------------|:------:|
| `POST` | `/api/orders` | Create new order | ✓ |
| `GET` | `/api/orders` | Retrieve all orders | ✓ |
| `GET` | `/api/orders/customer/{customerId}` | Retrieve orders by customer | ✓ |
| `GET` | `/api/orders/status/{status}` | Retrieve orders by status | ✓ |
| `PATCH` | `/api/orders/{id}/status` | Update order status | ✓ |

<br/>

### Product Endpoints

| Method | Endpoint | Description | Status |
|:------:|:---------|:------------|:------:|
| `POST` | `/api/products` | Add new product | ✓ |
| `GET` | `/api/products` | Retrieve all products | ✓ |
| `GET` | `/api/products/{id}` | Retrieve product by ID | ✓ |
| `PUT` | `/api/products/{id}` | Update product information | ✓ |

<br/>

### Payment Endpoints

| Method | Endpoint | Description | Status |
|:------:|:---------|:------------|:------:|
| `POST` | `/api/v1/payments` | Create payment (returns Stripe clientSecret) | ✓ |
| `GET` | `/api/v1/payments/{id}` | Retrieve payment by ID | ✓ |

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

<br/>

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

<br/>

**Create Payment Request (Stripe)**
```http
POST /api/v1/payments HTTP/1.1
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "amount": 99.99,
  "paymentMethod": "STRIPE",
  "orderId": 1
}
```

<br/>

**Payment Response**
```json
{
  "id": 1,
  "paymentMethod": "STRIPE",
  "amount": 99.99,
  "paymentStatus": "PENDING",
  "transactionId": "pi_3ABC123...",
  "paymentGatewayResponse": "pi_3ABC123_secret_XYZ..."
}
```
> **Note**: The `paymentGatewayResponse` contains the Stripe `clientSecret` for frontend payment confirmation.

</details>

<br/>

<br/>

---

## ▎LLMs I Worked With

This project was developed with assistance from cutting-edge AI coding assistants:

<br/>

<div align="center">

| AI Assistant | Provider | Role |
|:-------------|:---------|:-----|
| 🤖 **Claude** | Anthropic | Code review, architecture guidance, documentation |
| 🚀 **Antigravity** | Google DeepMind | Pair programming, implementation, debugging |

</div>

<br/>

> These AI assistants helped accelerate development by providing code suggestions, explaining complex concepts, generating documentation.


---

## ▎Roadmap

<br/>

**Development Progress**

```
Phase 1: Core Features       ████████████████████  100%
Phase 2: Security & Quality  ████████░░░░░░░░░░░░  40%
Phase 3: Performance         ████░░░░░░░░░░░░░░░░  20%
Phase 4: Advanced Features   ░░░░░░░░░░░░░░░░░░░░   0%
```

<br/>

<details>
<summary><b>📋 Detailed Roadmap</b></summary>

<br/>

### Phase 1: Core Features ✓ Complete

- [x] Customer Management System
- [x] Product Catalog Management
- [x] Order Processing Engine
- [x] Order Items Support
- [x] Database Schema Design
- [x] Payment Processing Integration (Stripe)
- [x] Global Exception Handling

<br/>

### Phase 2: Security & Quality (In Progress)

- [x] JWT Authentication & Authorization
- [x] Role-Based Access Control (RBAC)
- [ ] API Documentation (Swagger/OpenAPI)
- [ ] Unit Testing (JUnit 5, Mockito)
- [ ] Integration Testing
- [x] Logging Framework (SLF4J/Logback)
- [ ] Security Headers

<br/>

### Phase 3: Performance & Scalability (In Progress)

- [x] Pagination & Filtering
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

We welcome contributions from the community. Please follow these guidelines:

1. **Review Roadmap** — Familiarize yourself with planned features
2. **Address Known Issues** — Critical bugs take precedence
3. **Follow Code Standards** — Maintain consistency
4. **Write Tests** — Include unit and integration tests
5. **Update Documentation** — Reflect changes in README

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

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

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

<sub>Last Updated: January 2026 · Documentation v1.1</sub>

</div>

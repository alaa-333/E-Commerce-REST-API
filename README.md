<div align="center">

# 🛒 E-Commerce REST API

**A modern RESTful API for e-commerce platforms**

[![Status](https://img.shields.io/badge/Status-Work%20In%20Progress-yellow?style=for-the-badge)](/)
[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Oracle](https://img.shields.io/badge/Oracle-Database-F80000?style=for-the-badge&logo=oracle&logoColor=white)](https://www.oracle.com/database/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

<br/>

[Features](#-features) • [Quick Start](#-quick-start) • [API Reference](#-api-reference) • [Roadmap](#-roadmap) • [Contributing](#-contributing)

<br/>

> ⚠️ **This project is under active development.** Some features are incomplete and the API may change.

</div>

---

## 📖 Overview

This REST API provides a complete backend solution for e-commerce platforms, featuring customer management, product catalog, order processing, and payment integration.

<br/>

## ✨ Features

<table>
<tr>
<td width="50%">

### ✅ Implemented
- 👤 Customer Management (CRUD)
- 📦 Product Management (CRUD)
- 🛍️ Order Management & Tracking
- 📋 Order Items Management
- 🗄️ Oracle Database Integration
- 🔄 DTO Pattern with MapStruct
- ✔️ Input Validation

</td>
<td width="50%">

### 🚧 In Progress
- 💳 Payment Service Implementation
- 🎯 Payment Strategy Pattern
- ⚠️ Global Exception Handling
- 🏷️ Custom Business Exceptions
- 📝 Error Code Management

</td>
</tr>
</table>

<br/>

## 🛠️ Tech Stack

<div align="center">

| Category | Technology |
|:--------:|:----------:|
| **Framework** | Spring Boot 3.5.7 |
| **Language** | Java 17 |
| **Database** | Oracle Database |
| **ORM** | Spring Data JPA / Hibernate |
| **Build Tool** | Maven |
| **Mapping** | MapStruct 1.5.3 |
| **Utilities** | Lombok |
| **Payments** | Stripe Java SDK 24.0.0 |
| **Validation** | Jakarta Validation |

</div>

<br/>

## 🚀 Quick Start

### Prerequisites

```
☑️ Java 17+     ☑️ Maven 3.6+     ☑️ Oracle Database     ☑️ Your favorite IDE
```

### Installation

```bash
# 1️⃣ Clone the repository
git clone <repository-url>
cd E-Commerce-REST-API

# 2️⃣ Configure database (edit src/main/resources/application.yaml)

# 3️⃣ Build the project
mvn clean install

# 4️⃣ Run the application
mvn spring-boot:run
```

### Configuration

```yaml
# src/main/resources/application.yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@//localhost:1521/orclpdb
    username: ${DB_USERNAME}      # Use environment variables
    password: ${DB_PASSWORD}      # for production!
    driver-class-name: oracle.jdbc.OracleDriver

  jpa:
    hibernate:
      ddl-auto: create            # Use 'update' or 'validate' in production
    show-sql: true

server:
  port: 8080
```

🌐 **API Base URL:** `http://localhost:8080/api`

<br/>

---

## 📡 API Reference

### 👤 Customers

| Method | Endpoint | Description | Status |
|:------:|:---------|:------------|:------:|
| `POST` | `/api/customers` | Create new customer | ✅ |
| `GET` | `/api/customers` | List all customers | ✅ |
| `GET` | `/api/customers/{id}` | Get customer by ID | ✅ |
| `GET` | `/api/customers/email/{email}` | Get customer by email | ✅ |
| `PUT` | `/api/customers/{id}` | Update customer | ✅ |

### 🛍️ Orders

| Method | Endpoint | Description | Status |
|:------:|:---------|:------------|:------:|
| `POST` | `/api/orders` | Create new order | ✅ |
| `GET` | `/api/orders` | List all orders | ✅ |
| `GET` | `/api/orders/customer/{customerId}` | Get orders by customer | ✅ |
| `GET` | `/api/orders/status/{status}` | Get orders by status | ✅ |
| `PATCH` | `/api/orders/{id}/status` | Update order status | ✅ |

### 📦 Products

| Method | Endpoint | Description | Status |
|:------:|:---------|:------------|:------:|
| `POST` | `/api/products` | Create new product | ✅ |
| `GET` | `/api/products` | List all products | ✅ |
| `GET` | `/api/products/{id}` | Get product by ID | ✅ |
| `PUT` | `/api/products/{id}` | Update product | ✅ |

### 💳 Payments

| Method | Endpoint | Description | Status |
|:------:|:---------|:------------|:------:|
| `POST` | `/api/payments` | Process payment | 🚧 |
| `GET` | `/api/payments/{id}` | Get payment by ID | 🚧 |

<br/>

<details>
<summary><b>📝 Request Examples</b></summary>

<br/>

**Create Customer**
```json
POST /api/customers
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  }
}
```

**Create Order**
```json
POST /api/orders
Content-Type: application/json

{
  "customerId": 1,
  "orderItems": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

</details>

<br/>

---

## 📁 Project Structure

```
📦 E-Commerce-REST-API
 ┣ 📂 src/main/java/com/e_commerce/...
 ┃ ┣ 📂 controller/        → REST Controllers
 ┃ ┣ 📂 service/           → Business Logic
 ┃ ┣ 📂 repository/        → Data Access Layer
 ┃ ┣ 📂 model/             → Entity Models
 ┃ ┣ 📂 dto/
 ┃ ┃ ┣ 📂 request/         → Request DTOs
 ┃ ┃ ┗ 📂 response/        → Response DTOs
 ┃ ┣ 📂 mapper/            → MapStruct Mappers
 ┃ ┣ 📂 payment/           → Payment Strategies 🚧
 ┃ ┣ 📂 exception/         → Exception Handling 🚧
 ┃ ┗ 📜 ECommerceRestApiApplication.java
 ┣ 📂 src/main/resources/
 ┃ ┗ 📜 application.yaml
 ┣ 📜 pom.xml
 ┗ 📜 README.md
```

<br/>

---

<br/>

---

## 🗺️ Roadmap

```
Phase 1: Core Features          ████████████░░░░░░░░  60%
Phase 2: Security & Quality     ░░░░░░░░░░░░░░░░░░░░   0%
Phase 3: Performance            ░░░░░░░░░░░░░░░░░░░░   0%
Phase 4: Advanced Features      ░░░░░░░░░░░░░░░░░░░░   0%
```

<details>
<summary><b>View Detailed Roadmap</b></summary>

### Phase 1: Core Features *(Current)*
- [x] Customer Management
- [x] Product Management
- [x] Order Management
- [ ] Payment Processing
- [ ] Exception Handling

### Phase 2: Security & Quality
- [ ] Authentication & Authorization (JWT)
- [ ] API Documentation (Swagger/OpenAPI)
- [ ] Comprehensive Testing
- [ ] Logging Framework (SLF4J/Logback)

### Phase 3: Performance & Scalability
- [ ] Pagination
- [ ] Caching
- [ ] Rate Limiting
- [ ] Database Optimization
- [ ] Monitoring & Health Checks

### Phase 4: Advanced Features
- [ ] Email Notifications
- [ ] Order Tracking
- [ ] Inventory Management
- [ ] Analytics & Reporting

</details>

<br/>

---

## 🤝 Contributing

We welcome contributions! Here's how you can help:

1. 🔍 **Check the Roadmap** — See what's planned
2. 🐛 **Fix Known Issues** — Help fix critical bugs
3. 📏 **Follow Code Style** — Maintain consistency
4. 🧪 **Test Changes** — Ensure nothing breaks
5. 📝 **Update Docs** — Keep documentation current

<br/>

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

<br/>

---

<div align="center">

**Made with ❤️ by Eng:Alaa Mohamed**

<br/>

![Version](https://img.shields.io/badge/Version-0.0.1--SNAPSHOT-informational?style=flat-square)
![Last Updated](https://img.shields.io/badge/Last%20Updated-2024-informational?style=flat-square)

</div>

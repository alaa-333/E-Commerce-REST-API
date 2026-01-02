
## 🗄️ Database Schema

### Entity Relationships

```mermaid
erDiagram
    USER ||--|| CUSTOMER : "associated with"
    USER {
        Long id PK
        String email UK
        String password
        Set~Role~ roles
        Boolean enabled
        Boolean accountNonLocked
        LocalDateTime createdAt
        LocalDateTime updatedAt
    }

    CUSTOMER ||--o{ ORDER : places
    CUSTOMER {
        Long id PK
        Long userId FK
        String firstName
        String lastName
        String phone
        String city
        String street
        String postalCode
        String country
        LocalDateTime createdAt
    }
    
    ORDER ||--|{ ORDER_ITEM : contains
    ORDER ||--|| PAYMENT : has
    ORDER {
        Long id PK
        String orderNumber UK
        Long customerId FK
        LocalDateTime orderDate
        BigDecimal totalAmount
        OrderStatus status
    }
    
    ORDER_ITEM }o--|| PRODUCT : references
    ORDER_ITEM {
        Long id PK
        Long orderId FK
        Long productId FK
        Integer quantity
        BigDecimal unitPrice
    }
    
    PRODUCT {
        Long id PK
        String name
        String description
        BigDecimal price
        Integer stockQuantity
        String category
        String imgUrl
        Boolean active
        LocalDateTime createdAt
    }
    
    PAYMENT {
        Long id PK
        Long orderId FK
        BigDecimal amount
        PaymentMethod paymentMethod
        PaymentStatus paymentStatus
        LocalDateTime paymentDate
        String transactionId
        String paymentGatewayResponse
    }
```


## 📊 Data Flow Diagrams

### Order Creation Flow

```mermaid
sequenceDiagram
    participant Client
    participant OrderController
    participant OrderService
    participant CustomerService
    participant ProductService
    participant OrderRepository
    participant PaymentService
    participant PaymentStrategyFactory
    participant PaymentStrategy
    
    Client->>OrderController: POST /api/orders
    OrderController->>OrderController: Validate DTO
    OrderController->>OrderService: createOrder(dto)
    
    OrderService->>OrderService: validateOrder(dto)
    OrderService->>CustomerService: getCustomerById()
    CustomerService-->>OrderService: Customer
    
    loop For each OrderItem
        OrderService->>ProductService: getById(productId)
        ProductService-->>OrderService: Product
        OrderService->>OrderService: Validate stock
        OrderService->>ProductService: Update stock quantity
    end
    
    OrderService->>OrderService: generateOrderNumber()
    OrderService->>OrderService: calculateTotal()
    OrderService->>OrderRepository: save(order)
    OrderRepository-->>OrderService: Saved Order
    
    OrderService->>PaymentService: processPayment(order)
    PaymentService->>PaymentStrategyFactory: getStrategy(paymentMethod)
    PaymentStrategyFactory-->>PaymentService: PaymentStrategy (e.g., Stripe)
    PaymentService->>PaymentStrategy: processPayment(amount)
    PaymentStrategy-->>PaymentService: PaymentResult
    
    OrderService->>OrderService: Map to DTO
    OrderService-->>OrderController: OrderResponseDTO
    OrderController-->>Client: 201 Created
```

### Exception Handling Flow

```mermaid
sequenceDiagram
    participant Client
    participant Controller
    participant Service
    participant Repository
    participant GlobalExceptionHandling
    
    Client->>Controller: HTTP Request
    Controller->>Service: Business operation
    Service->>Repository: Data access
    Repository-->>Service: Throw ProductNotFoundException
    Service-->>Controller: Propagate exception
    Controller-->>GlobalExceptionHandling: Exception caught
    GlobalExceptionHandling->>GlobalExceptionHandling: Build ErrorResponse
    GlobalExceptionHandling->>GlobalExceptionHandling: Set HTTP Status
    GlobalExceptionHandling-->>Client: Error Response (404)
```

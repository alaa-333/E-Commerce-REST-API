# Stripe Payment Work Flow Guide

## 📁 Project Structure Overview

Your payment implementation follows the **Strategy Design Pattern**, which makes it easy to add new payment methods (PayPal, Apple Pay, etc.) in the future.

```
src/main/java/com/e_commerce/E_Commerce/REST/API/
├── config/
│   └── StripeConfig.java           # Stripe API key configuration
├── controller/
│   └── PaymentController.java      # REST endpoints for payments
├── dto/
│   ├── request/
│   │   └── PaymentRequestDTO.java  # Request body for creating payments
│   └── response/
│       └── PaymentResponseDTO.java # Response containing payment details
├── model/
│   ├── Payment.java                # JPA entity for storing payments
| 
├── payment/                        # 🔑 Strategy Pattern Package
│   ├── PaymentStrategy.java        # Interface all payment methods implement
│   ├── PaymentStrategyFactory.java # Factory to select the right strategy
|   ├── PaymentMethod.java          # Enum constant values (STRIPE , PAYPAL)
|   ├── PaymentStatus.java          # Enum constant values (PENDING, REFAUNDED, CONFERMID)
│   └── StripePaymentStrategy.java  # Stripe implementation
├── repository/
│   └── PaymentRepository.java      # JPA repository for Payment entity
└── service/
    └── PaymentService.java         # Business logic orchestrator
```

---

## 5. How They Work Together

### Sequence Diagram

```
Client                Controller           Service              Factory              Strategy            Stripe API
  │                       │                   │                    │                    │                    │
  │ POST /payments        │                   │                    │                    │                    │
  │──────────────────────>│                   │                    │                    │                    │
  │                       │ createPayment()   │                    │                    │                    │
  │                       │──────────────────>│                    │                    │                    │
  │                       │                   │ getStrategy("STRIPE")                   │                    │
  │                       │                   │───────────────────>│                    │                    │
  │                       │                   │                    │ return StripePaymentStrategy            │
  │                       │                   │<───────────────────│                    │                    │
  │                       │                   │                    │                    │                    │
  │                       │                   │ processPayment(100.00)                  │                    │
  │                       │                   │───────────────────────────────────────>│                    │
  │                       │                   │                    │                    │ PaymentIntent.create()
  │                       │                   │                    │                    │───────────────────>│
  │                       │                   │                    │                    │   PaymentIntent    │
  │                       │                   │                    │                    │<───────────────────│
  │                       │                   │           PaymentResult.success()       │                    │
  │                       │                   │<───────────────────────────────────────│                    │
  │                       │                   │                    │                    │                    │
  │                       │                   │ save(Payment)      │                    │                    │
  │                       │                   │──────────> DB      │                    │                    │
  │                       │                   │                    │                    │                    │
  │                       │ PaymentResponseDTO│                    │                    │                    │
  │                       │<──────────────────│                    │                    │                    │
  │   JSON Response       │                   │                    │                    │                    │
  │<──────────────────────│                   │                    │                    │                    │
```

### Class Relationships

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              PaymentController                              │
│                                    │                                        │
│                                    │ uses                                   │
│                                    ▼                                        │
│  ┌──────────────────────────PaymentService──────────────────────────────┐  │
│  │                                 │                                     │  │
│  │              ┌──────────────────┼──────────────────┐                 │  │
│  │              │                  │                  │                 │  │
│  │              ▼                  ▼                  ▼                 │  │
│  │    OrderRepository    PaymentRepository    PaymentStrategyFactory   │  │
│  │                                                    │                 │  │
│  │                                                    │ manages         │  │
│  │                                                    ▼                 │  │
│  │                                        ┌───────────────────┐        │  │
│  │                                        │  PaymentStrategy  │        │  │
│  │                                        │    (interface)    │        │  │
│  │                                        └─────────┬─────────┘        │  │
│  │                                                  │ implements       │  │
│  │                                    ┌─────────────┴─────────────┐    │  │
│  │                                    │                           │    │  │
│  │                                    ▼                           ▼    │  │
│  │                        StripePaymentStrategy       PayPalPaymentStrategy   │
│  │                                    │                      (future)  │  │
│  │                                    │                                │  │
│  │                                    ▼                                │  │
│  │                              Stripe API                             │  │
│  └─────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Summary Table

| Class | Type | Responsibility | Design Pattern |
|-------|------|----------------|----------------|
| `PaymentStrategy` | Interface | Define contract for payment methods | Strategy |
| `StripePaymentStrategy` | Class | Implement Stripe-specific logic | Strategy (ConcreteStrategy) |
| `PaymentStrategyFactory` | Class | Select appropriate strategy | Factory |
| `PaymentService` | Class | Orchestrate payment flow | Service Layer / Facade |

---

## 📄 File-by-File Explanation

### 1. `StripeConfig.java` - Configuration
**Purpose**: Initializes Stripe SDK with your secret API key on application startup.

```java
@Configuration
public class StripeConfig {
    @Value("${stripe.api.key}")
    private String secretKey;

    @PostConstruct
    public void setUp() {
        Stripe.apiKey = secretKey;  // Sets Stripe's static API key
    }
}
```

**Key Concept**: Uses `@PostConstruct` to run once when the bean is created.

---

### 2. `PaymentStrategy.java` - Strategy Interface
**Purpose**: Defines the contract that ALL payment methods must follow.

```java
public interface PaymentStrategy {
    PaymentResult processPayment(BigDecimal amount);
    String getPaymentMethodType();

    record PaymentResult(
        boolean success,
        String transactionId,
        String clientSecret,
        String errorMessage
    ) { /* factory methods */ }
}
```

---

### 3. `StripePaymentStrategy.java` - Stripe Implementation
**Purpose**: Contains all Stripe-specific logic.

```java
@Component  // Makes it a Spring Bean (auto-discovered)
public class StripePaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentResult processPayment(BigDecimal amount) {
        // 1. Convert to cents (Stripe requirement)
        long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

        // 2. Build PaymentIntent params
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount(amountInCents)
            .setCurrency("usd")
            .setAutomaticPaymentMethods(...)
            .build();

        // 3. Call Stripe API
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // 4. Return result
        return PaymentResult.success(
            paymentIntent.getId(),
            paymentIntent.getClientSecret()
        );
    }

    @Override
    public String getPaymentMethodType() {
        return "STRIPE";
    }
}
```

---

### 4. `PaymentStrategyFactory.java` - Factory
**Purpose**: Selects the correct strategy based on payment method name.

```java
@Component
public class PaymentStrategyFactory {
    private final Map<String, PaymentStrategy> strategyMap;

    // Spring auto-injects ALL beans implementing PaymentStrategy
    public PaymentStrategyFactory(List<PaymentStrategy> strategies) {
        this.strategyMap = strategies.stream()
            .collect(Collectors.toMap(
                PaymentStrategy::getPaymentMethodType,
                Function.identity()
            ));
    }

    public PaymentStrategy getStrategy(String paymentMethod) {
        return strategyMap.get(paymentMethod.toUpperCase());
    }
}
```

---

### 5. `PaymentService.java` - Business Logic
**Purpose**: Orchestrates the payment flow.

```java
@Service
public class PaymentService {
    private final PaymentStrategyFactory paymentStrategyFactory;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentResponseDTO createPayment(PaymentRequestDTO requestDTO) {
        // 1. Validate request
        // 2. Verify order exists
        // 3. Get appropriate strategy from factory
        PaymentStrategy strategy = paymentStrategyFactory.getStrategy(paymentMethod);

        // 4. Process payment
        PaymentResult result = strategy.processPayment(amount);

        // 5. Save to database
        Payment payment = Payment.builder()...build();
        paymentRepository.save(payment);

        // 6. Return response
        return new PaymentResponseDTO(...);
    }
}
```

---

### 6. `PaymentController.java` - REST API
**Purpose**: Exposes HTTP endpoints.

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/payments` | Create a new payment |
| GET | `/api/v1/payments/{id}` | Get payment by ID |

---

### Step 2: Request
```bash
curl -X POST http://localhost:8080/api/v1/payments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "amount": 99.99,
    "paymentMethod": "STRIPE",
    "orderId": 1
  }'
```

### Step 3: Expected Response
```json
{
  "id": 1,
  "paymentMethod": "STRIPE",
  "amount": 99.99,
  "paymentStatus": "PENDING",
  "transactionId": "pi_3ABC123...",
  "paymentGatewayResponse": "pi_3ABC123_secret_XYZ789..."
}
```
---
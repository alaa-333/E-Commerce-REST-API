package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.dto.request.PaymentRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaymentResponseDTO;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.exception.order.OrderNotFoundException;
import com.e_commerce.E_Commerce.REST.API.exception.payment.PaymentAmountMismatchException;
import com.e_commerce.E_Commerce.REST.API.mapper.PaymentMapper;
import com.e_commerce.E_Commerce.REST.API.model.Order;
import com.e_commerce.E_Commerce.REST.API.model.Payment;
import com.e_commerce.E_Commerce.REST.API.payment.PaymentMethod;
import com.e_commerce.E_Commerce.REST.API.payment.PaymentStatus;
import com.e_commerce.E_Commerce.REST.API.payment.PaymentStrategy;
import com.e_commerce.E_Commerce.REST.API.payment.PaymentStrategyFactory;
import com.e_commerce.E_Commerce.REST.API.repository.OrderRepository;
import com.e_commerce.E_Commerce.REST.API.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentStrategyFactory paymentStrategyFactory;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentMapper mapper;

    @Mock
    private PaymentStrategy paymentStrategy;

    @InjectMocks
    private PaymentService paymentService;

    private Order testOrder;
    private Payment testPayment;
    private PaymentRequestDTO paymentRequestDTO;
    private PaymentResponseDTO paymentResponseDTO;
    private PaymentStrategy.PaymentResult successResult;
    private PaymentStrategy.PaymentResult failureResult;

    @BeforeEach
    void setUp() {
        // Setup test order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNumber("ORD-123456");
        testOrder.setTotalAmount(BigDecimal.valueOf(100.00));

        // Setup payment request DTO
        paymentRequestDTO = new PaymentRequestDTO();
        paymentRequestDTO.setOrderId(1L);
        paymentRequestDTO.setAmount(BigDecimal.valueOf(100.00));
        paymentRequestDTO.setPaymentMethod("STRIPE");

        // Setup test payment
        testPayment = Payment.builder()
                .id(1L)
                .paymentMethod(PaymentMethod.STRIPE)
                .amount(BigDecimal.valueOf(100.00))
                .PaymentDate(LocalDateTime.now())
                .paymentStatus(PaymentStatus.PENDING)
                .transactionId("txn_123456")
                .paymentGatewayResponse("client_secret_123")
                .order(testOrder)
                .build();

        // Setup payment response DTO
        paymentResponseDTO = new PaymentResponseDTO();
        paymentResponseDTO.setId(1L);
        paymentResponseDTO.setTransactionId("txn_123456");
        paymentResponseDTO.setPaymentStatus("PENDING");

        // Setup payment results
        successResult = PaymentStrategy.PaymentResult.success("txn_123456", "client_secret_123");
        failureResult = PaymentStrategy.PaymentResult.failure("Payment failed");
    }

    @Test
    void createPayment_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(paymentStrategyFactory.isSupported("STRIPE")).thenReturn(true);
        when(paymentStrategyFactory.getStrategy("STRIPE")).thenReturn(paymentStrategy);
        when(paymentStrategy.processPayment(any(BigDecimal.class))).thenReturn(successResult);
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
        when(mapper.toResponseDTO(any(Payment.class))).thenReturn(paymentResponseDTO);

        // Act
        PaymentResponseDTO result = paymentService.createPayment(paymentRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("txn_123456", result.getTransactionId());
        verify(orderRepository).findById(1L);
        verify(paymentStrategyFactory).isSupported("STRIPE");
        verify(paymentStrategy).processPayment(BigDecimal.valueOf(100.00));
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void createPayment_OrderNotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> paymentService.createPayment(paymentRequestDTO));
        verify(orderRepository).findById(1L);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void createPayment_AmountMismatch_ThrowsException() {
        // Arrange
        paymentRequestDTO.setAmount(BigDecimal.valueOf(200.00)); // Different from order total
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        assertThrows(PaymentAmountMismatchException.class, () -> paymentService.createPayment(paymentRequestDTO));
        verify(orderRepository).findById(1L);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void createPayment_UnsupportedPaymentMethod_ThrowsException() {
        // Arrange
        paymentRequestDTO.setPaymentMethod("UNSUPPORTED");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(paymentStrategyFactory.isSupported("UNSUPPORTED")).thenReturn(false);

        // Act & Assert
        assertThrows(ValidationException.class, () -> paymentService.createPayment(paymentRequestDTO));
        verify(orderRepository).findById(1L);
        verify(paymentStrategyFactory).isSupported("UNSUPPORTED");
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void createPayment_PaymentProcessingFails_ThrowsException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(paymentStrategyFactory.isSupported("STRIPE")).thenReturn(true);
        when(paymentStrategyFactory.getStrategy("STRIPE")).thenReturn(paymentStrategy);
        when(paymentStrategy.processPayment(any(BigDecimal.class))).thenReturn(failureResult);
        
        Payment failedPayment = Payment.builder()
                .id(1L)
                .paymentStatus(PaymentStatus.FAILED)
                .build();
        when(paymentRepository.save(any(Payment.class))).thenReturn(failedPayment);

        // Act & Assert
        assertThrows(ValidationException.class, () -> paymentService.createPayment(paymentRequestDTO));
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void getPaymentById_Success() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(mapper.toResponseDTO(testPayment)).thenReturn(paymentResponseDTO);

        // Act
        PaymentResponseDTO result = paymentService.getPaymentById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(paymentRepository).findById(1L);
        verify(mapper).toResponseDTO(testPayment);
    }

    @Test
    void getPaymentById_NotFound_ThrowsException() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidationException.class, () -> paymentService.getPaymentById(1L));
        verify(paymentRepository).findById(1L);
    }

    @Test
    void updatePaymentStatus_Success() {
        // Arrange
        String transactionId = "txn_123456";
        PaymentStatus newStatus = PaymentStatus.COMPLETED;
        
        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // Act
        paymentService.updatePaymentStatus(transactionId, newStatus);

        // Assert
        verify(paymentRepository).findByTransactionId(transactionId);
        verify(paymentRepository).save(testPayment);
        assertEquals(newStatus, testPayment.getPaymentStatus());
    }

    @Test
    void updatePaymentStatus_PaymentNotFound_ThrowsException() {
        // Arrange
        String transactionId = "txn_nonexistent";
        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidationException.class, 
            () -> paymentService.updatePaymentStatus(transactionId, PaymentStatus.COMPLETED));
        verify(paymentRepository).findByTransactionId(transactionId);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void createPayment_WithZeroAmount_ThrowsException() {
        // Arrange
        paymentRequestDTO.setAmount(BigDecimal.ZERO);

        // Act & Assert
        assertThrows(Exception.class, () -> paymentService.createPayment(paymentRequestDTO));
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void createPayment_WithNegativeAmount_ThrowsException() {
        // Arrange
        paymentRequestDTO.setAmount(BigDecimal.valueOf(-100.00));

        // Act & Assert
        assertThrows(Exception.class, () -> paymentService.createPayment(paymentRequestDTO));
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void createPayment_WithNullPaymentMethod_ThrowsException() {
        // Arrange
        paymentRequestDTO.setPaymentMethod(null);

        // Act & Assert
        assertThrows(Exception.class, () -> paymentService.createPayment(paymentRequestDTO));
        verify(paymentRepository, never()).save(any());
    }
}

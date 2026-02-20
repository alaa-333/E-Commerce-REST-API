package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.dto.request.OrderCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.OrderItemCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.OrderUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.PaginationRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.response.OrderItemResponseDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.OrderResponseDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaginationResponseDto;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.exception.customer.CustomerNotFoundException;
import com.e_commerce.E_Commerce.REST.API.exception.order.OrderNotFoundException;
import com.e_commerce.E_Commerce.REST.API.mapper.OrderMapper;
import com.e_commerce.E_Commerce.REST.API.model.Customer;
import com.e_commerce.E_Commerce.REST.API.model.Order;
import com.e_commerce.E_Commerce.REST.API.model.OrderItem;
import com.e_commerce.E_Commerce.REST.API.model.Product;
import com.e_commerce.E_Commerce.REST.API.model.enums.OrderStatus;
import com.e_commerce.E_Commerce.REST.API.repository.CustomerRepository;
import com.e_commerce.E_Commerce.REST.API.repository.OrderRepository;
import com.e_commerce.E_Commerce.REST.API.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemService orderItemService;

    @InjectMocks
    private OrderService orderService;

    private Customer testCustomer;
    private Order testOrder;
    private OrderCreateRequestDTO createRequestDTO;
    private OrderResponseDTO responseDTO;
    private Product testProduct;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        // Setup test customer
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");

        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(100));
        testProduct.setStockQuantity(10);

        // Setup test order item
        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setProduct(testProduct);
        testOrderItem.setQuantity(2);
        testOrderItem.setUnitPrice(BigDecimal.valueOf(100));

        // Setup test order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNumber("ORD-123456");
        testOrder.setCustomer(testCustomer);
        testOrder.setOrderStatus(OrderStatus.PENDING);
        testOrder.setTotalAmount(BigDecimal.valueOf(200));
        testOrder.setOrderItems(new ArrayList<>(List.of(testOrderItem)));

        // Setup create request DTO
        OrderItemCreateRequestDTO itemDTO = OrderItemCreateRequestDTO.builder()
                .productId(1L)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(100))
                .build();

        createRequestDTO = OrderCreateRequestDTO.builder()
                .customerId(1L)
                .orderItems(List.of(itemDTO))
                .build();

        // Setup response DTO
        responseDTO = new OrderResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setOrderNumber("ORD-123456");
    }

    @Test
    void createOrder_Success() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(orderMapper.createNewOrder(any(), anyString())).thenReturn(testOrder);
        when(orderItemService.addOrderItem(any(), any())).thenReturn(new OrderItemResponseDTO());
        when(productRepository.findAllById(anySet())).thenReturn(List.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderMapper.toResponseDTO(any(Order.class))).thenReturn(responseDTO);

        // Act
        OrderResponseDTO result = orderService.createOrder(createRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("ORD-123456", result.getOrderNumber());
        verify(customerRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).toResponseDTO(any(Order.class));
    }

    @Test
    void createOrder_CustomerNotFound_ThrowsException() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> orderService.createOrder(createRequestDTO));
        verify(customerRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_EmptyOrderItems_ThrowsException() {
        // Arrange
        OrderCreateRequestDTO emptyItemsDTO = OrderCreateRequestDTO.builder()
                .customerId(1L)
                .orderItems(List.of())
                .build();

        // Act & Assert
        assertThrows(Exception.class, () -> orderService.createOrder(emptyItemsDTO));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrderById_Success() {
        // Arrange
        when(orderRepository.findByIdWithCustomerAndItems(1L)).thenReturn(Optional.of(testOrder));
        when(orderMapper.toResponseDTO(testOrder)).thenReturn(responseDTO);

        // Act
        OrderResponseDTO result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository).findByIdWithCustomerAndItems(1L);
        verify(orderMapper).toResponseDTO(testOrder);
    }

    @Test
    void getOrderById_NotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findByIdWithCustomerAndItems(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(1L));
        verify(orderRepository).findByIdWithCustomerAndItems(1L);
    }

    @Test
    void updateOrderStatus_Success() {
        // Arrange
        OrderUpdateRequestDTO updateDTO = new OrderUpdateRequestDTO();
        updateDTO.setOrderStatus("SHIPPED");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderMapper.toResponseDTO(any(Order.class))).thenReturn(responseDTO);

        // Act
        OrderResponseDTO result = orderService.updateOrderStatus(1L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(orderRepository).findById(1L);
        verify(orderMapper).updateEntityFromDTO(updateDTO, testOrder);
        verify(orderRepository).save(testOrder);
    }

    @Test
    void updateOrderStatus_OrderNotFound_ThrowsException() {
        // Arrange
        OrderUpdateRequestDTO updateDTO = new OrderUpdateRequestDTO();
        updateDTO.setOrderStatus("SHIPPED");

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrderStatus(1L, updateDTO));
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getAll_Success() {
        // Arrange
        PaginationRequestDto requestDto = new PaginationRequestDto();
        requestDto.setPage(0);
        requestDto.setSize(10);

        Page<Order> orderPage = new PageImpl<>(List.of(testOrder));
        when(orderRepository.findAllWithCustomer(any(Pageable.class))).thenReturn(orderPage);
        when(orderMapper.toResponseDTO(any(Order.class))).thenReturn(responseDTO);

        // Act
        PaginationResponseDto<OrderResponseDTO> result = orderService.getAll(requestDto);

        // Assert
        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        verify(orderRepository).findAllWithCustomer(any(Pageable.class));
    }

    @Test
    void getOrdersByCustomerId_Success() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(orderRepository.findByCustomerId(1L)).thenReturn(List.of(testOrder));
        when(orderMapper.toResponseDTO(any(Order.class))).thenReturn(responseDTO);

        // Act
        List<OrderResponseDTO> result = orderService.getOrdersByCustomerId(1L);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(customerRepository).findById(1L);
        verify(orderRepository).findByCustomerId(1L);
    }

    @Test
    void getOrdersByCustomerId_CustomerNotFound_ThrowsException() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> orderService.getOrdersByCustomerId(1L));
        verify(customerRepository).findById(1L);
        verify(orderRepository, never()).findByCustomerId(any());
    }

    @Test
    void getOrderByStatus_Success() {
        // Arrange
        PaginationRequestDto requestDto = new PaginationRequestDto();
        requestDto.setPage(0);
        requestDto.setSize(10);

        Page<Order> orderPage = new PageImpl<>(List.of(testOrder));
        when(orderRepository.findByOrderStatus(eq(OrderStatus.PENDING), any(Pageable.class))).thenReturn(orderPage);
        when(orderMapper.toResponseDTO(any(Order.class))).thenReturn(responseDTO);

        // Act
        PaginationResponseDto<OrderResponseDTO> result = orderService.getOrderByStatus("PENDING", requestDto);

        // Assert
        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        verify(orderRepository).findByOrderStatus(eq(OrderStatus.PENDING), any(Pageable.class));
    }

    @Test
    void getOrderByStatus_InvalidStatus_ThrowsException() {
        // Arrange
        PaginationRequestDto requestDto = new PaginationRequestDto();
        requestDto.setPage(0);
        requestDto.setSize(10);

        // Act & Assert
        assertThrows(ValidationException.class, () -> orderService.getOrderByStatus("INVALID_STATUS", requestDto));
        verify(orderRepository, never()).findByOrderStatus(any(), any());
    }
}

package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.dto.request.OrderItemCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.OrderItemUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.OrderItemResponseDTO;
import com.e_commerce.E_Commerce.REST.API.exception.DuplicateResourceException;
import com.e_commerce.E_Commerce.REST.API.exception.ResourceNotFoundException;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.exception.order.OrderNotFoundException;
import com.e_commerce.E_Commerce.REST.API.exception.orderItem.OrderItemsEmptyException;
import com.e_commerce.E_Commerce.REST.API.exception.product.ProductNotFoundException;
import com.e_commerce.E_Commerce.REST.API.mapper.OrderItemMapper;
import com.e_commerce.E_Commerce.REST.API.model.Order;
import com.e_commerce.E_Commerce.REST.API.model.OrderItem;
import com.e_commerce.E_Commerce.REST.API.model.Product;
import com.e_commerce.E_Commerce.REST.API.model.enums.OrderStatus;
import com.e_commerce.E_Commerce.REST.API.repository.OrderItemRepository;
import com.e_commerce.E_Commerce.REST.API.repository.OrderRepository;
import com.e_commerce.E_Commerce.REST.API.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemValidator orderItemValidator;

    @InjectMocks
    private OrderItemService orderItemService;

    private Order testOrder;
    private Product testProduct;
    private OrderItem testOrderItem;
    private OrderItemCreateRequestDTO createRequestDTO;
    private OrderItemUpdateRequestDTO updateRequestDTO;
    private OrderItemResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(100.00));
        testProduct.setStockQuantity(10);
        testProduct.setActive(true);

        // Setup test order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNumber("ORD-123456");
        testOrder.setOrderStatus(OrderStatus.PENDING);
        testOrder.setTotalAmount(BigDecimal.valueOf(200.00));
        testOrder.setOrderItems(new ArrayList<>());

        // Setup test order item
        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setProduct(testProduct);
        testOrderItem.setOrder(testOrder);
        testOrderItem.setQuantity(2);
        testOrderItem.setUnitPrice(BigDecimal.valueOf(100.00));

        testOrder.getOrderItems().add(testOrderItem);

        // Setup create request DTO
        createRequestDTO = OrderItemCreateRequestDTO.builder()
                .productId(1L)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(100.00))
                .build();

        // Setup update request DTO
        updateRequestDTO = new OrderItemUpdateRequestDTO();
        updateRequestDTO.setOrderQuantity(3);

        // Setup response DTO
        responseDTO = new OrderItemResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setProductId(1L);
        responseDTO.setProductName("Test Product");
        responseDTO.setQuantity(2);
        responseDTO.setUnitPrice(BigDecimal.valueOf(100.00));
    }

    @Test
    void getOrderItemById_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderItemRepository.findByIdAndOrderId(1L, 1L)).thenReturn(Optional.of(testOrderItem));
        when(orderItemMapper.toResponseDTO(testOrderItem)).thenReturn(responseDTO);
        doNothing().when(orderItemValidator).validateOrderStatusForItemOperations(testOrder);
        doNothing().when(orderItemValidator).validateOrderItemBelongsToOrder(testOrderItem, 1L);

        // Act
        OrderItemResponseDTO result = orderItemService.getOrderItemById(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository).findById(1L);
        verify(orderItemRepository).findByIdAndOrderId(1L, 1L);
    }

    @Test
    void getOrderItemById_OrderNotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        doThrow(new ValidationException(null)).when(orderItemValidator).validateOrderStatusForItemOperations(null);

        // Act & Assert
        assertThrows(Exception.class, () -> orderItemService.getOrderItemById(1L, 1L));
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderItemById_OrderItemNotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderItemRepository.findByIdAndOrderId(1L, 1L)).thenReturn(Optional.empty());
        doNothing().when(orderItemValidator).validateOrderStatusForItemOperations(testOrder);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderItemService.getOrderItemById(1L, 1L));
        verify(orderItemRepository).findByIdAndOrderId(1L, 1L);
    }

    @Test
    void addOrderItem_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderItemRepository.findByOrderIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
        when(orderItemMapper.createOrderItem(createRequestDTO, testProduct, testOrder)).thenReturn(testOrderItem);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);
        when(orderItemMapper.toResponseDTO(testOrderItem)).thenReturn(responseDTO);
        when(orderItemMapper.calculateItemTotal(testOrderItem)).thenReturn(BigDecimal.valueOf(200.00));
        when(orderItemValidator.hasSufficientStock(createRequestDTO, testProduct)).thenReturn(true);
        
        doNothing().when(orderItemValidator).validateOrderItemRequest(createRequestDTO);
        doNothing().when(orderItemValidator).validateOrderStatusForItemOperations(testOrder);
        doNothing().when(orderItemValidator).validateProductAndQuantity(testProduct, 2);

        // Act
        OrderItemResponseDTO result = orderItemService.addOrderItem(1L, createRequestDTO);

        // Assert
        assertNotNull(result);
        verify(orderRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(orderItemRepository).save(any(OrderItem.class));
    }

    @Test
    void addOrderItem_OrderNotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        doThrow(new ValidationException(null)).when(orderItemValidator).validateOrderStatusForItemOperations(null);

        // Act & Assert
        assertThrows(Exception.class, () -> orderItemService.addOrderItem(1L, createRequestDTO));
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    void addOrderItem_ProductNotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        doNothing().when(orderItemValidator).validateOrderItemRequest(createRequestDTO);
        doNothing().when(orderItemValidator).validateOrderStatusForItemOperations(testOrder);

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> orderItemService.addOrderItem(1L, createRequestDTO));
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    void addOrderItem_DuplicateProduct_ThrowsException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderItemRepository.findByOrderIdAndProductId(1L, 1L)).thenReturn(Optional.of(testOrderItem));
        when(orderItemValidator.hasSufficientStock(createRequestDTO, testProduct)).thenReturn(true);
        
        doNothing().when(orderItemValidator).validateOrderItemRequest(createRequestDTO);
        doNothing().when(orderItemValidator).validateOrderStatusForItemOperations(testOrder);
        doNothing().when(orderItemValidator).validateProductAndQuantity(testProduct, 2);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> orderItemService.addOrderItem(1L, createRequestDTO));
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    void addOrderItem_InsufficientStock_ThrowsException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderItemRepository.findByOrderIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
        when(orderItemValidator.hasSufficientStock(createRequestDTO, testProduct)).thenReturn(false);
        
        doNothing().when(orderItemValidator).validateOrderItemRequest(createRequestDTO);
        doNothing().when(orderItemValidator).validateOrderStatusForItemOperations(testOrder);
        doNothing().when(orderItemValidator).validateProductAndQuantity(testProduct, 2);

        // Act & Assert
        assertThrows(ValidationException.class, () -> orderItemService.addOrderItem(1L, createRequestDTO));
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    void updateOrderItemQuantity_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));
        when(productRepository.reduceStock(1L, 1)).thenReturn(1); // Increasing quantity by 1
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemMapper.toResponseDTO(testOrderItem)).thenReturn(responseDTO);
        
        doNothing().when(orderItemValidator).validateOrderStatusForItemOperations(testOrder);
        doNothing().when(orderItemValidator).validateOrderItemBelongsToOrder(testOrderItem, 1L);

        // Act
        OrderItemResponseDTO result = orderItemService.updateOrderItemQuantity(1L, 1L, updateRequestDTO);

        // Assert
        assertNotNull(result);
        verify(orderItemRepository).save(testOrderItem);
        verify(orderRepository).save(testOrder);
    }

    @Test
    void updateOrderItemQuantity_InsufficientStock_ThrowsException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));
        when(productRepository.reduceStock(1L, 1)).thenReturn(0); // Stock update failed
        
        doNothing().when(orderItemValidator).validateOrderStatusForItemOperations(testOrder);
        doNothing().when(orderItemValidator).validateOrderItemBelongsToOrder(testOrderItem, 1L);

        // Act & Assert
        assertThrows(ValidationException.class, 
            () -> orderItemService.updateOrderItemQuantity(1L, 1L, updateRequestDTO));
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    void getOrderItemsByOrder_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderItemMapper.toResponseDTOs(anyList())).thenReturn(List.of(responseDTO));
        doNothing().when(orderItemValidator).validateOrderItems(anyList());

        // Act
        List<OrderItemResponseDTO> result = orderItemService.getOrderItemsByOrder(1L);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderItemsByOrder_EmptyItems_ThrowsException() {
        // Arrange
        testOrder.setOrderItems(new ArrayList<>());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        assertThrows(OrderItemsEmptyException.class, () -> orderItemService.getOrderItemsByOrder(1L));
        verify(orderRepository).findById(1L);
    }

    @Test
    void getItemsByOrderAndProduct_Success() {
        // Arrange
        when(orderItemRepository.getItemsByOrderIdAndProductId(1L, 1L)).thenReturn(List.of(testOrderItem));
        when(orderItemMapper.toResponseDTOs(anyList())).thenReturn(List.of(responseDTO));
        doNothing().when(orderItemValidator).validateOrderItems(anyList());

        // Act
        List<OrderItemResponseDTO> result = orderItemService.getItemsByOrderAndProduct(1L, 1L);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(orderItemRepository).getItemsByOrderIdAndProductId(1L, 1L);
    }

    @Test
    void getOrderItemsByProduct_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderItemRepository.findByProductId(1L)).thenReturn(List.of(testOrderItem));
        when(orderItemMapper.toResponseDTOs(anyList())).thenReturn(List.of(responseDTO));

        // Act
        List<OrderItemResponseDTO> result = orderItemService.getOrderItemsByProduct(1L);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(productRepository).findById(1L);
        verify(orderItemRepository).findByProductId(1L);
    }

    @Test
    void getOrderItemsByProduct_ProductNotFound_ThrowsException() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> orderItemService.getOrderItemsByProduct(1L));
        verify(productRepository).findById(1L);
    }

    @Test
    void deleteOrderItem_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderItemMapper.calculateItemTotal(testOrderItem)).thenReturn(BigDecimal.valueOf(200.00));
        doNothing().when(orderItemValidator).validateOrderStatusForItemOperations(testOrder);
        doNothing().when(orderItemRepository).deleteById(1L);

        // Act
        orderItemService.deleteOrderItem(1L, 1L);

        // Assert
        verify(orderRepository).findById(1L);
        verify(orderItemRepository).deleteById(1L);
        verify(productRepository).save(testProduct);
    }

    @Test
    void deleteOrderItem_OrderItemNotFound_ThrowsException() {
        // Arrange
        testOrder.setOrderItems(new ArrayList<>());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        doNothing().when(orderItemValidator).validateOrderStatusForItemOperations(testOrder);

        // Act & Assert
        assertThrows(ValidationException.class, () -> orderItemService.deleteOrderItem(1L, 1L));
        verify(orderItemRepository, never()).deleteById(any());
    }
}

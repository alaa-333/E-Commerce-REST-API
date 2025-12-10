package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.dto.request.OrderItemCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.OrderItemUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.OrderItemResponseDTO;
import com.e_commerce.E_Commerce.REST.API.exception.DuplicateResourceException;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ResourceNotFoundException;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.exception.order.OrderNotFoundException;
import com.e_commerce.E_Commerce.REST.API.exception.orderItem.OrderItemsEmptyException;
import com.e_commerce.E_Commerce.REST.API.exception.product.ProductNotFoundException;
import com.e_commerce.E_Commerce.REST.API.mapper.OrderItemMapper;
import com.e_commerce.E_Commerce.REST.API.model.Order;
import com.e_commerce.E_Commerce.REST.API.model.OrderItem;
import com.e_commerce.E_Commerce.REST.API.model.Product;
import com.e_commerce.E_Commerce.REST.API.repository.OrderItemRepository;
import com.e_commerce.E_Commerce.REST.API.repository.OrderRepository;
import com.e_commerce.E_Commerce.REST.API.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemValidator orderItemValidator;

    @Transactional(readOnly = true)
    public OrderItemResponseDTO getOrderItemById(Long orderId, Long orderItemId) {
        Order order = getAndValidateOrder(orderId);

        OrderItem orderItem = orderItemRepository.findByIdAndOrderId(orderItemId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.ORDER_ITEM_NOT_FOUND,
                        ErrorCode.ORDER_ITEM_NOT_FOUND.getHttpStatus(),
                        ErrorCode.ORDER_ITEM_NOT_FOUND.getMessage()
                ));

        orderItemValidator.validateOrderItemBelongsToOrder(orderItem, orderId);

        return orderItemMapper.toResponseDTO(orderItem);
    }

    public OrderItemResponseDTO addOrderItem(Long orderId, OrderItemCreateRequestDTO requestDTO) {
        // Validate request
        orderItemValidator.validateOrderItemRequest(requestDTO);

        // Get and validate order
        Order order = getAndValidateOrder(orderId);
        orderItemValidator.validateOrderStatusForItemOperations(order);

        // Get and validate product
        Product product = getAndValidateProduct(requestDTO.getProductId(), requestDTO.getQuantity());

        // Validate no duplicate product in order
        validateNoDuplicateProductInOrder(orderId, product.getId());

        // Create and save order item
        OrderItem orderItem = orderItemMapper.createOrderItem(requestDTO, product, order);
        OrderItem saved = orderItemRepository.save(orderItem);

        // Update product stock
        updateProductStock(product, requestDTO.getQuantity());

        // Update order total
        updateOrderTotal(order, orderItemMapper.calculateItemTotal(saved));

        return orderItemMapper.toResponseDTO(saved);
    }

    public OrderItemResponseDTO updateOrderItemQuantity(Long orderId, Long orderItemId,
                                                        OrderItemUpdateRequestDTO updateRequestDTO) {
        // Validate update request
        orderItemValidator.validateOrderItemUpdateRequest(updateRequestDTO);

        // Get and validate order
        Order order = getAndValidateOrder(orderId);
        orderItemValidator.validateOrderStatusForItemOperations(order);

        // Get and validate order item
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ValidationException(ErrorCode.ORDER_ITEM_NOT_FOUND));

        orderItemValidator.validateOrderItemBelongsToOrder(orderItem, orderId);

        Integer newQuantity = updateRequestDTO.getOrderQuantity();

        // Validate stock for quantity update
        orderItemValidator.validateStockForQuantityUpdate(orderItem, newQuantity);

        // Calculate price differences
        BigDecimal oldTotal = orderItemMapper.calculateItemTotal(orderItem);
        orderItem.setQuantity(newQuantity);
        BigDecimal newTotal = orderItemMapper.calculateItemTotal(orderItem);
        BigDecimal difference = newTotal.subtract(oldTotal);

        // Update product stock
        int quantityDifference = newQuantity - orderItem.getQuantity();
        updateProductStock(orderItem.getProduct(), -quantityDifference);

        // Update order
        order.setTotalAmount(order.getTotalAmount().add(difference));

        OrderItem savedItem = orderItemRepository.save(orderItem);
        orderRepository.save(order);

        return orderItemMapper.toResponseDTO(savedItem);
    }

    @Transactional(readOnly = true)
    public List<OrderItemResponseDTO> getOrderItemsByOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        List<OrderItem> orderItems = order.getOrderItems();

        if (orderItems.isEmpty()) {
            throw new OrderItemsEmptyException();
        }

        orderItemValidator.validateOrderItems(orderItems);

        return orderItemMapper.toResponseDTOs(orderItems);
    }

    @Transactional(readOnly = true)
    public List<OrderItemResponseDTO> getItemsByOrderAndProduct(Long orderId, Long productId) {
        List<OrderItem> orderItems = orderItemRepository.getItemsByOrderIdAndProductId(orderId, productId);

        orderItemValidator.validateOrderItems(orderItems);

        return orderItemMapper.toResponseDTOs(orderItems);
    }

    @Transactional(readOnly = true)
    public List<OrderItemResponseDTO> getOrderItemsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        List<OrderItem> orderItems = orderItemRepository.findByProductId(productId);

        return orderItemMapper.toResponseDTOs(orderItems);
    }

    public void deleteOrderItem(Long orderId, Long itemId) {
        Order order = getAndValidateOrder(orderId);
        orderItemValidator.validateOrderStatusForItemOperations(order);

        OrderItem orderItem = order.getOrderItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ValidationException(ErrorCode.ORDER_ITEM_NOT_FOUND));

        // Restore product stock
        restoreProductStock(orderItem);

        // Update order total
        updateOrderTotal(order, orderItemMapper.calculateItemTotal(orderItem).negate());

        orderItemRepository.deleteById(itemId);
    }

    // ============= PRIVATE HELPER METHODS =============

    private Order getAndValidateOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        orderItemValidator.validateOrderStatusForItemOperations(order);

        return order;
    }

    private Product getAndValidateProduct(Long productId, Integer requestedQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        orderItemValidator.validateProductAndQuantity(product, requestedQuantity);

        // Validate stock
        if (!orderItemValidator.hasSufficientStock(
                OrderItemCreateRequestDTO.builder()
                        .productId(productId)
                        .quantity(requestedQuantity)
                        .unitPrice(product.getPrice())
                        .build(),
                product
        )) {
            throw new ValidationException(ErrorCode.PRODUCT_INSUFFICIENT_STOCK);
        }

        return product;
    }

    private void validateNoDuplicateProductInOrder(Long orderId, Long productId) {
        Optional<OrderItem> existingItem = orderItemRepository.findByOrderIdAndProductId(orderId, productId);

        if (existingItem.isPresent()) {
            throw new DuplicateResourceException("orderItem", existingItem.get().getId());
        }
    }

    private void updateProductStock(Product product, Integer quantityChange) {
        int newStock = product.getStockQuantity() - quantityChange;
        if (newStock < 0) {
            throw new ValidationException(ErrorCode.PRODUCT_INSUFFICIENT_STOCK);
        }
        product.setStockQuantity(newStock);
        productRepository.save(product);
    }

    private void restoreProductStock(OrderItem orderItem) {
        if (orderItem.getProduct() != null) {
            Product product = orderItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
            productRepository.save(product);
        }
    }

    private void updateOrderTotal(Order order, BigDecimal amountChange) {
        BigDecimal newTotal = order.getTotalAmount().add(amountChange);
        if (newTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException(ErrorCode.ORDER_TOTAL_INVALID);
        }
        order.setTotalAmount(newTotal);
        orderRepository.save(order);
    }
}
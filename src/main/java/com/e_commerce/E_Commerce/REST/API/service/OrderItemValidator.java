package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.dto.request.OrderItemCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.OrderItemUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.model.Order;
import com.e_commerce.E_Commerce.REST.API.model.OrderItem;
import com.e_commerce.E_Commerce.REST.API.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderItemValidator {

    // ============= VALIDATION METHODS =============

    /**
     * Validates if order item has sufficient stock
     */
    public boolean hasSufficientStock(OrderItem orderItem) {
        return Optional.ofNullable(orderItem)
                .filter(item -> item.getProduct() != null)
                .filter(item -> item.getQuantity() != null)
                .map(item -> item.getQuantity() <= item.getProduct().getStockQuantity())
                .orElse(false);
    }
    public boolean hasSufficientStock(OrderItemCreateRequestDTO requestDTO, Product product) {
        return Optional.ofNullable(requestDTO)
                .filter(dto -> dto.getQuantity() != null)
                .filter(dto -> product != null)
                .map(dto -> dto.getQuantity() <= product.getStockQuantity())
                .orElse(false);
    }


    /**
     * Validates product and quantity for order item creation
     */
    public void validateProductAndQuantity(Product product, Integer quantity) {
        if (product == null) {
            throw new ValidationException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        if (quantity == null || quantity <= 0) {
            throw new ValidationException(ErrorCode.INVALID_QUANTITY);
        }
        if (!product.isActive()) {
            throw new ValidationException(ErrorCode.PRODUCT_DISABLED);
        }
    }

    /**
     * Validates order item creation request
     */
    public void validateOrderItemRequest(OrderItemCreateRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new ValidationException(ErrorCode.ORDER_ITEMS_EMPTY);
        }

        if (requestDTO.getProductId() == null) {
            throw new ValidationException(ErrorCode.REQUIRED_FIELD_MISSING,"product id is null");
        }

        validateQuantityAndPrice(requestDTO.getQuantity(), requestDTO.getUnitPrice(), requestDTO);
    }

    /**
     * Validates order item update request
     */
    public void validateOrderItemUpdateRequest(OrderItemUpdateRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new ValidationException(ErrorCode.ORDER_ITEMS_EMPTY);
        }

        if (requestDTO.getOrderQuantity() == null || requestDTO.getOrderQuantity() <= 0) {
            throw new ValidationException(ErrorCode.INVALID_QUANTITY);
        }
    }

    /**
     * Validates order item belongs to order
     */
    public void validateOrderItemBelongsToOrder(OrderItem orderItem, Long orderId) {
        if (orderItem == null) {
            throw new ValidationException(ErrorCode.ORDER_ITEM_NOT_FOUND);
        }

        if (!orderItem.getOrder().getId().equals(orderId)) {
            throw new ValidationException(ErrorCode.ORDER_ITEM_NOT_FOUND);
        }
    }

    /**
     * Validates order status for item operations
     */
    public void validateOrderStatusForItemOperations(Order order) {
        if (order == null) {
            throw new ValidationException(ErrorCode.ORDER_NOT_FOUND);
        }

        if (!order.getOrderStatus().canModifyItems()) {
            throw new ValidationException(ErrorCode.ORDER_ALREADY_PROCESSED);
        }
    }

    /**
     * Validates stock availability for quantity update
     */
    public void validateStockForQuantityUpdate(OrderItem orderItem, Integer newQuantity) {
        Product product = orderItem.getProduct();
        int currentOrderQuantity = orderItem.getQuantity();
        int availableStock = product.getStockQuantity() + currentOrderQuantity;

        if (newQuantity > availableStock) {
            throw new ValidationException(ErrorCode.PRODUCT_INSUFFICIENT_STOCK);
        }
    }

    /**
     * Validates no duplicate products in order
     */
    public void validateNoDuplicateProduct(List<OrderItem> existingItems, Long productId) {
        boolean duplicateExists = existingItems.stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId));

        if (duplicateExists) {
            throw new ValidationException(ErrorCode.DUPLICATE_ENTRY);
        }
    }

    /**
     * Validates collection of order items
     */
    public void validateOrderItems(Collection<OrderItem> orderItems) {
        if (CollectionUtils.isEmpty(orderItems)) {
            throw new ValidationException(ErrorCode.ORDER_ITEMS_EMPTY);
        }

        orderItems.forEach(this::validateOrderItem);
    }

    /**
     * Validates individual order item
     */
    public void validateOrderItem(OrderItem orderItem) {
        if (orderItem == null) {
            throw new ValidationException(ErrorCode.ORDER_ITEMS_EMPTY);
        }

        validateQuantityAndPrice(orderItem.getQuantity(), orderItem.getUnitPrice(), orderItem);

        if (orderItem.getProduct() == null) {
            throw new ValidationException(ErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    public void validateQuantityAndPrice(Integer quantity, BigDecimal unitPrice, Object orderItem) {
        if (quantity == null || quantity <= 0) {
            throw new ValidationException(ErrorCode.INVALID_QUANTITY);
        }

        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(ErrorCode.INVALID_PRICE);
        }
    }
}
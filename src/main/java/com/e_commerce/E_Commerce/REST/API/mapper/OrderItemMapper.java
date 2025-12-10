package com.e_commerce.E_Commerce.REST.API.mapper;

import com.e_commerce.E_Commerce.REST.API.dto.request.OrderItemCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.OrderItemUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.OrderItemResponseDTO;
import com.e_commerce.E_Commerce.REST.API.model.Order;
import com.e_commerce.E_Commerce.REST.API.model.OrderItem;
import com.e_commerce.E_Commerce.REST.API.model.Product;
import org.mapstruct.*;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderItemMapper {

    // ============= BASIC MAPPINGS =============

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemCreateRequestDTO requestDTO);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.imgUrl", target = "imageUrl")
    @Mapping(target = "itemTotal", expression = "java(calculateItemTotal(orderItem))")
    OrderItemResponseDTO toResponseDTO(OrderItem orderItem);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "order", ignore = true)
    void updateFromDTO(OrderItemUpdateRequestDTO requestDTO, @MappingTarget OrderItem orderItem);

    // ============= COLLECTION MAPPINGS =============

    default List<OrderItemResponseDTO> toResponseDTOs(Collection<OrderItem> orderItems) {
        return Optional.ofNullable(orderItems)
                .stream()
                .flatMap(Collection::stream)
                .map(this::toResponseDTO)
                .toList();
    }

    default List<OrderItem> toEntities(Collection<OrderItemCreateRequestDTO> requestDTOs) {
        return Optional.ofNullable(requestDTOs)
                .stream()
                .flatMap(Collection::stream)
                .map(this::toEntity)
                .toList();
    }

    // ============= FACTORY/CREATION METHODS =============

    /**
     * Creates OrderItem entity with all relationships set
     */
    default OrderItem createOrderItem(OrderItemCreateRequestDTO requestDTO, Product product, Order order) {
        if (requestDTO == null || product == null || order == null) {
            throw new IllegalArgumentException("RequestDTO, Product, and Order must not be null");
        }

        return OrderItem.builder()
                .quantity(requestDTO.getQuantity())
                .unitPrice(requestDTO.getUnitPrice())
                .product(product)
                .order(order)
                .build();
    }

    /**
     * Creates OrderItem from product directly (uses current product price)
     */
    default OrderItem createFromProduct(Product product, Integer quantity, Order order) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive integer");
        }

        return OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .unitPrice(product.getPrice())
                .order(order)
                .build();
    }

    // ============= CALCULATION METHODS =============

    default BigDecimal calculateItemTotal(OrderItem orderItem) {
        return Optional.ofNullable(orderItem)
                .filter(item -> item.getQuantity() != null && item.getUnitPrice() != null)
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .orElse(BigDecimal.ZERO);
    }

    default BigDecimal calculateOrderTotal(Collection<OrderItem> orderItems) {
        return Optional.ofNullable(orderItems)
                .stream()
                .flatMap(Collection::stream)
                .map(this::calculateItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ============= UTILITY METHODS =============

    /**
     * Extracts product IDs from order item DTOs
     */
    default List<Long> extractProductIds(Collection<OrderItemCreateRequestDTO> requestDTOs) {
        return Optional.ofNullable(requestDTOs)
                .stream()
                .flatMap(Collection::stream)
                .map(OrderItemCreateRequestDTO::getProductId)
                .toList();
    }

    /**
     * Updates multiple order items from DTOs
     */
    default void updateOrderItemsFromDTOs(
            Collection<OrderItemUpdateRequestDTO> updateDTOs,
            Collection<OrderItem> orderItems
    ) {
        if (CollectionUtils.isEmpty(updateDTOs) || CollectionUtils.isEmpty(orderItems)) {
            return;
        }

    }
}
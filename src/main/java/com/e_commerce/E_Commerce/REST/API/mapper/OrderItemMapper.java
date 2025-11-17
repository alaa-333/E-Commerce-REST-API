package com.e_commerce.E_Commerce.REST.API.mapper;

import com.e_commerce.E_Commerce.REST.API.dto.request.OrderCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.OrderItemCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.OrderItemResponseDTO;
import com.e_commerce.E_Commerce.REST.API.model.Order;
import com.e_commerce.E_Commerce.REST.API.model.OrderItem;
import com.e_commerce.E_Commerce.REST.API.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    // ==== REQUEST -> ENTITY

    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "product" , ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderCreateRequestDTO requestDTO);

    // =====   response  <- entity

    @Mapping(source = "product.getId()" , target = "productId")
    @Mapping(source = "product.getName" , target = "productName")
    @Mapping(source = "product.gerUrl()" , target = "imageUrl")
    @Mapping(target ="itemTotal" , expression = "java(calculateItemTotal(orderItem))")
    OrderItemResponseDTO toResponseDTO(OrderItem orderItem);

    // create orderItem enttiy and link both references product_id , order_id
    default OrderItem createOrderItemEntity(OrderCreateRequestDTO requestDTO, Product product, Order order)
    {
        OrderItem orderItem = toEntity(requestDTO);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        return orderItem;
    }


    /**
     * Calculates item total (quantity * unit price)
     */
    default BigDecimal calculateItemTotal(OrderItem orderItem) {
        if (orderItem == null || orderItem.getQuantity() == null || orderItem.getUnitPrice() == null) {
            return BigDecimal.ZERO;
        }
        return orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
    }

    /**
     * Maps list of OrderItem entities to ResponseDTOs
     */
    default List<OrderItemResponseDTO> toResponseDTOs(List<OrderItem> orderItems) {
        if (orderItems == null) {
            return List.of();
        }
        return orderItems.stream()
                .map(this::toResponseDTO)
                .toList();
    }



    /**
     * Maps list of RequestDTOs to OrderItem entities
     */
    default List<OrderItem> toEntities(List<OrderItemCreateRequestDTO> requestDTOs,
                                       com.e_commerce.E_Commerce.REST.API.model.Order order) {
        if (requestDTOs == null) {
            return List.of();
        }

        return requestDTOs.stream()
                .map(dto -> {
                    OrderItem orderItem = toEntity(dto);
                    orderItem.setOrder(order);
                    // Note: Product would be set by service layer after validation
                    return orderItem;
                })
                .toList();
    }

    /**
     * Updates OrderItem entity from RequestDTO (for partial updates)
     */
    default void updateOrderItemFromDTO(OrderItemCreateRequestDTO requestDTO, OrderItem orderItem) {
        if (requestDTO == null || orderItem == null) {
            return;
        }

        // Update only non-null fields from DTO
        if (requestDTO.getQuantity() != null) {
            orderItem.setQuantity(requestDTO.getQuantity());
        }
        if (requestDTO.getUnitPrice() != null) {
            orderItem.setUnitPrice(requestDTO.getUnitPrice());
        }
    }

    /**
     * Validates if OrderItem has sufficient stock
     */
    default boolean hasSufficientStock(OrderItem orderItem) {
        if (orderItem == null || orderItem.getProduct() == null) {
            return false;
        }
        return orderItem.getQuantity() <= orderItem.getProduct().getStockQuantity();
    }

    /**
     * Calculates total for a list of order items
     */
    default BigDecimal calculateOrderItemsTotal(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return orderItems.stream()
                .map(this::calculateItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Creates OrderItem from product and quantity (convenience method)
     */
    default OrderItem createOrderItemFromProduct(com.e_commerce.E_Commerce.REST.API.model.Product product,
                                                 Integer quantity,
                                                 com.e_commerce.E_Commerce.REST.API.model.Order order) {
        if (product == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Product and valid quantity are required");
        }

        return OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .unitPrice(product.getPrice()) // Use current product price
                .order(order)
                .build();
    }


}

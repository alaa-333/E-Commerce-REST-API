package com.e_commerce.E_Commerce.REST.API.mapper;

import com.e_commerce.E_Commerce.REST.API.dto.request.OrderCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.OrderUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.OrderItemResponseDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.OrderResponseDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaymentResponseDTO;
import com.e_commerce.E_Commerce.REST.API.model.Order;
import com.e_commerce.E_Commerce.REST.API.model.OrderItem;
import com.e_commerce.E_Commerce.REST.API.model.Payment;
import com.e_commerce.E_Commerce.REST.API.model.enums.OrderStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // ======== REQUEST TO ENTITY ======== /

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "orderStatus", ignore = true)
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    Order toEntity(OrderCreateRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    void updateEntityFromDTO(OrderUpdateRequestDTO requestDTO, @MappingTarget Order order);


    @Mapping(source = "orderItems", target = "orderItems")
    @Mapping(source = "payment", target = "payment")
    OrderResponseDTO toResponseDTO(Order order);


    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.imgUrl", target = "imageUrl")
    OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem);

    PaymentResponseDTO toPaymentResponseDTO(Payment payment);


    default Order createNewOrder(OrderCreateRequestDTO requestDTO, String orderNumber) {
        Order order = toEntity(requestDTO);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderNumber(orderNumber);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setTotalAmount(requestDTO.calculateTotalAmount());
        return order;
    }

    default void updateOrderFromDTO(OrderUpdateRequestDTO requestDTO, Order order) {
        if (requestDTO == null || order == null) {
            return;
        }

        // Update only provided fields
        if (requestDTO.hasStatus()) {
            try {
                OrderStatus status = OrderStatus.valueOf(requestDTO.getOrderStatus().toUpperCase());
                order.setOrderStatus(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid order status: " + requestDTO.getOrderStatus());
            }
        }
    }
}

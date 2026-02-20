package com.e_commerce.E_Commerce.REST.API.mapper;

import com.e_commerce.E_Commerce.REST.API.dto.request.OrderCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.OrderItemCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.OrderUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.OrderItemResponseDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.OrderResponseDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaymentResponseDTO;
import com.e_commerce.E_Commerce.REST.API.model.Order;
import com.e_commerce.E_Commerce.REST.API.model.OrderItem;
import com.e_commerce.E_Commerce.REST.API.model.Payment;
import com.e_commerce.E_Commerce.REST.API.model.enums.OrderStatus;
import com.e_commerce.E_Commerce.REST.API.util.ValidationUtility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring",
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

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

    OrderResponseDTO toResponseDTO(Order order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.imgUrl", target = "imageUrl")
    OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem);

    PaymentResponseDTO toPaymentResponseDTO(Payment payment);

    default Order createNewOrder(OrderCreateRequestDTO requestDTO, String orderNumber) {
        Order order = toEntity(requestDTO);
        order.setOrderNumber(orderNumber);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setTotalAmount(ValidationUtility.calculateOrderTotal(requestDTO.getOrderItems()));
        return order;
    }

    default OrderItem createOrderItem(OrderItemCreateRequestDTO requestDTO, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(requestDTO.getQuantity());
        orderItem.setUnitPrice(requestDTO.getUnitPrice());
        orderItem.setOrder(order);
        return orderItem;
    }

    default List<OrderItem> toOrderItemsEntities(List<OrderItemCreateRequestDTO> requestDTOS, Order order) {
        if (requestDTOS == null) {
            return List.of();
        }
        return requestDTOS.stream().map(dto -> createOrderItem(dto, order)).toList();
    }
}

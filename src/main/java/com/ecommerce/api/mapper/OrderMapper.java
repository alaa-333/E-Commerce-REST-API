package com.ecommerce.api.mapper;

import com.ecommerce.api.dto.response.OrderItemResponse;
import com.ecommerce.api.dto.response.OrderResponse;
import com.ecommerce.api.dto.response.ShippingAddressResponse;
import com.ecommerce.api.entity.Order;
import com.ecommerce.api.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "customerId", expression = "java(order.getCustomer() != null ? order.getCustomer().getId() : null)")
    @Mapping(target = "status", expression = "java(order.getOrderStatus().name())")
    @Mapping(target = "items", source = "order.items")
    @Mapping(target = "shippingAddress", expression = "java(new com.ecommerce.api.dto.response.ShippingAddressResponse(order.getShippingCity(), order.getShippingStreet(), order.getShippingPostalCode(), order.getShippingCountry()))")
    OrderResponse toOrderResponse(Order order);

    @Mapping(target = "productId", expression = "java(item.getProduct()!=null ? item.getProduct().getId() : null)")
    @Mapping(target = "productName", expression = "java(item.getProduct()!=null ? item.getProduct().getName() : null)")
    @Mapping(target = "subtotal", expression = "java(item.getUnitPrice().multiply(new java.math.BigDecimal(item.getQuantity())))")
    OrderItemResponse toOrderItemResponse(OrderItem item);

    List<OrderItemResponse> toOrderItemResponses(List<OrderItem> items);
}


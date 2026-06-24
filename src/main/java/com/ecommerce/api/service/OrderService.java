package com.ecommerce.api.service;

import com.ecommerce.api.dto.request.order.CreateOrderRequest;
import com.ecommerce.api.dto.response.OrderResponse;
import com.ecommerce.api.entity.*;
import com.ecommerce.api.entity.enums.OrderStatus;
import com.ecommerce.api.exception.EcommerceAppException;
import com.ecommerce.api.exception.ErrorCode;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.mapper.OrderMapper;
import com.ecommerce.api.repository.OrderRepository;
import com.ecommerce.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public OrderResponse createOrder(CreateOrderRequest request) {
        var customer = getCurrentCustomer();

        var cart = customer.getCart();
        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new EcommerceAppException(ErrorCode.CART_EMPTY);
        }

        // validate stock
        cart.getCartItems().forEach(item -> {
            var product = item.getProduct();
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new EcommerceAppException(ErrorCode.PRODUCT_INSUFFICIENT_STOCK);
            }
        });

        var order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderNumber(generateOrderNumber());
        if (request.getShippingAddress() != null) {
            var sa = request.getShippingAddress();
            order.setShippingCity(sa.getCity());
            order.setShippingStreet(sa.getStreet());
            order.setShippingPostalCode(sa.getPostalCode());
            order.setShippingCountry(sa.getCountry());
        }
        order.setNotes(request.getNotes());

        BigDecimal total = BigDecimal.ZERO;

        for (var cartItem : cart.getCartItems()) {
            var product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "product not found with id " + cartItem.getProduct().getId()));

            // snapshot price
            var item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(product.getPrice());
            item.setAddedAt(LocalDateTime.now());

            // reduce stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            order.addItem(item);

            total = total.add(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        order.setTotalAmount(total);

        var saved = orderRepository.save(order);

        // clear cart
        cartService.clearCart();

        return orderMapper.toOrderResponse(saved);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Page<OrderResponse> getAll(int page, int size, String status) {
        var pageRequest = PageRequest.of(page, size);
        Page<Order> pageResult;
        if (status == null || status.isBlank()) {
            pageResult = orderRepository.findAll(pageRequest);
        } else {
            var s = OrderStatus.valueOf(status);
            pageResult = orderRepository.findByOrderStatus(s, pageRequest);
        }

        return pageResult.map(orderMapper::toOrderResponse);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<OrderResponse> getMyOrders(int page, int size) {
        var customer = getCurrentCustomer();
        var pageRequest = PageRequest.of(page, size);
        return orderRepository.findByCustomer(customer, pageRequest).map(orderMapper::toOrderResponse);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public OrderResponse getOrderById(Long id) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND, "order not found with id " + id));

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return orderMapper.toOrderResponse(order);
        }

        var customer = getCurrentCustomer();
        if (order.getCustomer() == null || !order.getCustomer().getId().equals(customer.getId())) {
            throw new EcommerceAppException(ErrorCode.ACCESS_DENIED);
        }

        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse updateStatus(Long id, String status) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND, "order not found with id " + id));

        var newStatus = OrderStatus.valueOf(status);
        var current = order.getOrderStatus();
        if (!isValidTransition(current, newStatus)) {
            throw new EcommerceAppException(ErrorCode.ORDER_INVALID_STATUS_TRANSITION);
        }

        order.setOrderStatus(newStatus);

        // if cancel restore stock
        if (newStatus == OrderStatus.CANCELLED) {
            restoreStockForOrder(order);
        }

        var saved = orderRepository.save(order);
        return orderMapper.toOrderResponse(saved);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN') or hasRole('USER')")
    public OrderResponse cancelOrder(Long id) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND, "order not found with id " + id));

        var customer = getCurrentCustomer();
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var isAdmin = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // allow owner or admin
        if (!isAdmin) {
            if (order.getCustomer() == null || !order.getCustomer().getId().equals(customer.getId())) {
                throw new EcommerceAppException(ErrorCode.ACCESS_DENIED);
            }
            // user can cancel only if PENDING or CONFIRMED
            if (!(order.getOrderStatus() == OrderStatus.PENDING || order.getOrderStatus() == OrderStatus.CONFIRMED)) {
                throw new EcommerceAppException(ErrorCode.ORDER_CANCEL_NOT_ALLOWED);
            }
        }

        // perform cancel
        order.setOrderStatus(OrderStatus.CANCELLED);
        restoreStockForOrder(order);

        var saved = orderRepository.save(order);
        return orderMapper.toOrderResponse(saved);
    }

    private void restoreStockForOrder(Order order) {
        order.getItems().forEach(item -> {
            var product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "product not found with id " + item.getProduct().getId()));
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        });
    }

    private boolean isValidTransition(OrderStatus from, OrderStatus to) {
        if (from == to) return true;
        return switch (from) {
            case PENDING -> to == OrderStatus.CONFIRMED || to == OrderStatus.CANCELLED;
            case CONFIRMED -> to == OrderStatus.PROCESSING || to == OrderStatus.CANCELLED;
            case PROCESSING -> to == OrderStatus.SHIPPED;
            case SHIPPED -> to == OrderStatus.DELIVERED;
            case DELIVERED -> false;
            case CANCELLED -> false;
        };
    }

    private String generateOrderNumber() {
        var today = LocalDate.now();
        var start = today.atStartOfDay();
        var end = today.atTime(LocalTime.MAX);
        long count = orderRepository.countByOrderDateBetween(start, end);
        long seq = count + 1;
        return String.format("ORD-%s-%05d", today.toString().replace("-", ""), seq);
    }

    private Customer getCurrentCustomer() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResourceNotFoundException(ErrorCode.ACCESS_DENIED, "user not authenticated");
        }
        var principal = auth.getPrincipal();
        if (!(principal instanceof User user) || user.getCustomer() == null) {
            throw new ResourceNotFoundException(ErrorCode.ACCESS_DENIED, "authenticated principal has no customer");
        }
        return user.getCustomer();
    }
}


package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.dto.request.OrderCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.OrderItemCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.OrderUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.PaginationRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.response.OrderItemResponseDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.OrderResponseDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaginationResponseDto;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.exception.customer.CustomerNotFoundException;
import com.e_commerce.E_Commerce.REST.API.exception.order.OrderNotFoundException;
import com.e_commerce.E_Commerce.REST.API.exception.order.OrderTotalInvalidException;
import com.e_commerce.E_Commerce.REST.API.exception.orderItem.OrderItemsEmptyException;
import com.e_commerce.E_Commerce.REST.API.exception.product.ProductNotFoundException;
import com.e_commerce.E_Commerce.REST.API.mapper.CustomerMapper;
import com.e_commerce.E_Commerce.REST.API.mapper.OrderItemMapper;
import com.e_commerce.E_Commerce.REST.API.mapper.OrderMapper;
import com.e_commerce.E_Commerce.REST.API.model.Customer;
import com.e_commerce.E_Commerce.REST.API.model.Order;
import com.e_commerce.E_Commerce.REST.API.model.OrderItem;
import com.e_commerce.E_Commerce.REST.API.model.Product;
import com.e_commerce.E_Commerce.REST.API.model.enums.OrderStatus;
import com.e_commerce.E_Commerce.REST.API.repository.CustomerRepository;
import com.e_commerce.E_Commerce.REST.API.repository.OrderRepository;
import com.e_commerce.E_Commerce.REST.API.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;
    private final OrderItemService orderItemService;

    // Create new order
    public OrderResponseDTO createOrder(OrderCreateRequestDTO requestDTO)
    {
        // validate total amount > 0 && contain at least one order item
        validateOrder(requestDTO.getOrderItems());
     

        // make sure quantity for each order is positive
        validateQuantity(requestDTO.getOrderItems());

        // ensure customer exist first -> and then you can make order
        Customer customer = customerRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() ->  new CustomerNotFoundException(requestDTO.getCustomerId()));

        // create order number
        String orderNumber = generateOrderNumber();

        Order order = orderMapper.createNewOrder(requestDTO,orderNumber);
        order.setCustomer(customer);


         List<OrderItemResponseDTO> orderItemResponseDTOS = requestDTO.getOrderItems().stream()
                 .map(item -> orderItemService.addOrderItem(order.getId(), item))
                 .toList();

         List<OrderItem> orderItemList = order.getOrderItems();



        // fetch products and set in orders items
        for (int i = 0 ; i < orderItemList.size() ; i++)
        {
            OrderItemCreateRequestDTO itemDto = requestDTO.getOrderItems().get(i);
            OrderItem orderItem = orderItemList.get(i);
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(itemDto.getProductId()));

            orderItem.setProduct(product);
        }
        order.setOrderItems(orderItemList);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponseDTO(savedOrder);


    }

    public OrderResponseDTO getOrderById(Long id)
    {
        Order order = orderRepository.findByIdWithCustomerAndItems(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return orderMapper.toResponseDTO(order);
    }

    public OrderResponseDTO updateOrderStatus(Long id, OrderUpdateRequestDTO requestDTO)
    {
        Order order = orderRepository.findById(id)
                .orElseThrow( () -> new OrderNotFoundException(id) );

        requestDTO.validateStatus();
        orderMapper.updateEntityFromDTO(requestDTO,order);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponseDTO(savedOrder);

    }

    public List<OrderResponseDTO> getAll ()
    {
        return orderRepository.findAllWithCustomer()
                .stream().map(orderMapper::toResponseDTO).toList();
    }

    public List<OrderResponseDTO> getOrdersByCustomerId(Long customerId)
    {
        // Validate customer exist first
        customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(orderMapper::toResponseDTO)
                .toList();
    }


    public PaginationResponseDto<OrderResponseDTO> getOrderByStatus(String status , PaginationRequestDto requestDto)
    {
        PaginationRequestDto.validate(requestDto);
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        Page<Order> orderPage =  orderRepository.findByOrderStatus(orderStatus , requestDto.toPageable());
        return PaginationResponseDto.PaginationMetadata.of(
                orderPage.map(orderMapper::toResponseDTO)
        );

    }

    private String generateOrderNumber()
    {
        return "ORD-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    // Custom validation methods
    private void validateOrder(List<OrderItemCreateRequestDTO> orderItems) {

        if (orderItems == null || orderItems.isEmpty() )
        {
            throw new OrderItemsEmptyException();
        }
        if ( calculateTotalAmount(orderItems).compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new OrderTotalInvalidException();
        }
    }

    public static BigDecimal calculateTotalAmount(List<OrderItemCreateRequestDTO> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return orderItems.stream()
                .map(OrderItemCreateRequestDTO::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateQuantity(List<OrderItemCreateRequestDTO> orderItems) {
        if (orderItems != null) {
            for (OrderItemCreateRequestDTO item : orderItems) {
                if (item.getQuantity() <= 0) {
                    throw new ValidationException(ErrorCode.INVALID_QUANTITY);
                }
            }
        }
    }



}

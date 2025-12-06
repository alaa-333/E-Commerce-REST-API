package com.e_commerce.E_Commerce.REST.API.controller;

import com.e_commerce.E_Commerce.REST.API.dto.request.OrderItemCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.OrderItemUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.OrderUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.OrderItemResponseDTO;
import com.e_commerce.E_Commerce.REST.API.model.OrderItem;
import com.e_commerce.E_Commerce.REST.API.repository.OrderItemRepository;
import com.e_commerce.E_Commerce.REST.API.service.OrderItemService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/orders/{orderId}/items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    // ========= GET ENDPOINT ======= //

    @GetMapping
    public ResponseEntity< List<OrderItemResponseDTO>> getAll(

            @PathVariable  @Positive @NotNull  Long ordreId,
            @RequestParam(value = "productId" , required = false) Long productId

    )
    {
        List<OrderItemResponseDTO> orderItems;
        if (productId != null)
        {
            orderItems = orderItemService.getItemsByOrderAndProduct(ordreId,productId);

        } else  {
            orderItems = orderItemService.getOrderItemsByOrder(ordreId);
        }

        if (orderItems.isEmpty())
        {
            return ResponseEntity.noContent().build();
        }
        return  ResponseEntity.ok(orderItems);
    }





    @GetMapping("/{itemId}")
    public  ResponseEntity<OrderItemResponseDTO> getOrderItem(
            @PathVariable("orderId") @Positive @NotNull Long orderId,
            @PathVariable("itemId") @Positive @NotNull Long itemId
    )
    {
        OrderItemResponseDTO responseDTO = orderItemService.getOrderItemById(orderId,itemId);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<OrderItemResponseDTO> addItem(
            @PathVariable("orderId") @Positive @NotNull Long orderId,
            @Valid @RequestBody OrderItemCreateRequestDTO requestDTO
    )
    {
        OrderItemResponseDTO responseDTO = orderItemService.addOrderItem(orderId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<OrderItemResponseDTO> updateQuantity(
            @PathVariable("orderId") @Positive @NotNull Long orderId,
            @PathVariable("itemId") @Positive @NotNull Long itemId,
            @Valid @RequestBody OrderItemUpdateRequestDTO requestDTO
            )
    {
        OrderItemResponseDTO responseDTO = orderItemService.updateOrderItemQuantity(
                orderId, itemId, requestDTO
        );

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable @Positive @NotNull Long orderId,
            @PathVariable @Positive @NotNull Long itemId
    )
    {
      // delete
        orderItemService.deleteOrderItem(orderId, itemId);
        return ResponseEntity.noContent().build();
    }







}

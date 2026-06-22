package com.ecommerce.api.controller;

import com.ecommerce.api.dto.request.cart.AddToCartRequest;
import com.ecommerce.api.dto.request.cart.UpdateCartItemRequest;
import com.ecommerce.api.dto.response.ApiResponse;
import com.ecommerce.api.dto.response.CartResponse;
import com.ecommerce.api.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {
        var response = cartService.getCart();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse<String>> updateCart(
            @PathVariable @Positive @NotNull Long id,
            @RequestBody @Valid UpdateCartItemRequest request
    ) {
        var response = cartService.updateCart(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("cart updated successfully")
        );
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<String>> addToCart(
            @RequestBody @Valid AddToCartRequest request
    ) {
        cartService.addToCart(request);
        return ResponseEntity.ok(ApiResponse.success("item added to cart successfully"));
    }

    @DeleteMapping("items/{id}")
    public ResponseEntity<ApiResponse<String>> removeFromCart(
            @PathVariable @Positive @NotNull Long id
    ) {
        cartService.removeFromCart(id);
        return ResponseEntity.ok(ApiResponse.success("item removed from cart successfully"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> clearCart(
    ) {
        cartService.clearCart();
        return ResponseEntity.ok(ApiResponse.success("cart cleared successfully"));
    }
}



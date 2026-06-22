package com.ecommerce.api.controller;

import com.ecommerce.api.dto.request.wishlist.AddToWishlistRequest;
import com.ecommerce.api.dto.response.ApiResponse;
import com.ecommerce.api.dto.response.CartResponse;
import com.ecommerce.api.dto.response.WishlistResponse;
import com.ecommerce.api.service.WishlistService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<ApiResponse<WishlistResponse>> getWishlist() {
        var response = wishlistService.getWishlist();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(
            @RequestBody @Valid AddToWishlistRequest request
    ) {
        var response = wishlistService.addToWishlist(request);
        return ResponseEntity.ok(ApiResponse.success("item added to wishlist successfully", response));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<WishlistResponse>> removeFromWishlist(
            @PathVariable @Positive @NotNull Long itemId
    ) {
        var response = wishlistService.removeFromWishlist(itemId);
        return ResponseEntity.ok(ApiResponse.success("item removed from wishlist successfully", response));
    }

    @PostMapping("/items/{itemId}/move-to-cart")
    public ResponseEntity<ApiResponse<CartResponse>> moveToCart(
            @PathVariable @Positive @NotNull Long itemId
    ) {
        var response = wishlistService.moveToCart(itemId);
        return ResponseEntity.ok(ApiResponse.success("item moved to cart successfully", response));
    }
}


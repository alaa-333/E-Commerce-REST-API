package com.ecommerce.api.service;

import com.ecommerce.api.dto.request.wishlist.AddToWishlistRequest;
import com.ecommerce.api.dto.response.CartResponse;
import com.ecommerce.api.dto.response.WishlistResponse;
import com.ecommerce.api.entity.Cart;
import com.ecommerce.api.entity.CartItem;
import com.ecommerce.api.entity.Customer;
import com.ecommerce.api.entity.User;
import com.ecommerce.api.entity.Wishlist;
import com.ecommerce.api.entity.WishlistItem;
import com.ecommerce.api.exception.EcommerceAppException;
import com.ecommerce.api.exception.ErrorCode;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.mapper.CartMapper;
import com.ecommerce.api.mapper.WishlistMapper;
import com.ecommerce.api.repository.CartRepository;
import com.ecommerce.api.repository.ProductRepository;
import com.ecommerce.api.repository.WishlistItemRepository;
import com.ecommerce.api.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final WishlistMapper wishlistMapper;
    private final CartMapper cartMapper;

    @PreAuthorize("hasRole('USER')")
    public WishlistResponse getWishlist() {
        var customer = getCurrentCustomer();

        var wishlist = customer.getWishlist();
        if (wishlist == null) {
            throw new ResourceNotFoundException(ErrorCode.WISHLIST_NOT_FOUND, "Wishlist not found for customer with id: " + customer.getId());
        }

        return wishlistMapper.toWishlistResponse(wishlist);
    }

    @PreAuthorize("hasRole('USER')")
    public WishlistResponse addToWishlist(AddToWishlistRequest request) {
        var customer = getCurrentCustomer();
        var wishlist = getOrCreateWishlist(customer);

        var product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WISHLIST_ITEM_PRODUCT_NOT_FOUND,
                        "product not found with id: " + request.getProductId()));

        if (!product.isActive()) {
            throw new EcommerceAppException(
                    ErrorCode.WISHLIST_ITEM_PRODUCT_DISABLED,
                    "product disabled with id " + request.getProductId());
        }

        var existingItem = wishlistItemRepository.findByWishlistIdAndProductId(wishlist.getId(), product.getId());
        if (existingItem.isPresent()) {
            throw new EcommerceAppException(
                    ErrorCode.WISHLIST_ITEM_ALREADY_EXISTS,
                    "Product already exists in wishlist with id " + product.getId());
        }

        var item = new WishlistItem();
        item.setProduct(product);
        item.setWishlist(wishlist);
        wishlist.addWishlistItem(item);

        wishlistRepository.save(wishlist);

        return wishlistMapper.toWishlistResponse(wishlist);
    }

    @PreAuthorize("hasRole('USER')")
    public WishlistResponse removeFromWishlist(Long itemId) {
        var customer = getCurrentCustomer();

        var wishlist = customer.getWishlist();
        if (wishlist == null) {
            throw new ResourceNotFoundException(ErrorCode.WISHLIST_NOT_FOUND, "Wishlist not found for the authenticated user");
        }

        var item = wishlist.getItems()
                .stream()
                .filter(wishlistItem -> wishlistItem.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WISHLIST_ITEM_NOT_FOUND, "wishlist item not found with id " + itemId));

        wishlist.getItems().remove(item);
        wishlistRepository.save(wishlist);

        return wishlistMapper.toWishlistResponse(wishlist);
    }

    @PreAuthorize("hasRole('USER')")
    public CartResponse moveToCart(Long itemId) {
        var customer = getCurrentCustomer();

        var wishlist = customer.getWishlist();
        if (wishlist == null) {
            throw new ResourceNotFoundException(ErrorCode.WISHLIST_NOT_FOUND, "Wishlist not found for the authenticated user");
        }

        var wishlistItem = wishlist.getItems()
                .stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WISHLIST_ITEM_NOT_FOUND, "wishlist item not found with id " + itemId));

        var product = wishlistItem.getProduct();

        // Check stock availability
        if (product.getStockQuantity() <= 0) {
            throw new EcommerceAppException(
                    ErrorCode.PRODUCT_INSUFFICIENT_STOCK,
                    "insufficient stock for product with id " + product.getId());
        }

        // Get or create cart
        var cart = customer.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setCustomer(customer);
            customer.setCart(cart);
        }

        // Check if product already exists in cart
        var existingCartItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingCartItem.isPresent()) {
            existingCartItem.get().setQuantity(existingCartItem.get().getQuantity() + 1);
        } else {
            var cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartItem.setCart(cart);
            cart.addCartItem(cartItem);
        }

        // Remove from wishlist
        wishlist.getItems().remove(wishlistItem);

        // Save all changes
        cartRepository.save(cart);
        wishlistRepository.save(wishlist);

        return cartMapper.toCartResponse(cart);
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

    private Wishlist getOrCreateWishlist(Customer customer) {
        var wishlist = customer.getWishlist();
        if (wishlist == null) {
            wishlist = new Wishlist();
            wishlist.setCustomer(customer);
            customer.setWishlist(wishlist);
        }
        return wishlist;
    }
}


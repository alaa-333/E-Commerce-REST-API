package com.ecommerce.api.service;

import com.ecommerce.api.dto.request.cart.AddToCartRequest;
import com.ecommerce.api.dto.request.cart.UpdateCartItemRequest;
import com.ecommerce.api.dto.response.CartResponse;
import com.ecommerce.api.entity.Cart;
import com.ecommerce.api.entity.CartItem;
import com.ecommerce.api.entity.Customer;
import com.ecommerce.api.entity.User;
import com.ecommerce.api.exception.EcommerceAppException;
import com.ecommerce.api.exception.ErrorCode;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.mapper.CartMapper;
import com.ecommerce.api.repository.CartRepository;
import com.ecommerce.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CustomerService customerService;
    private final CartMapper cartMapper;



    @Transactional
    @PreAuthorize("hasRole('USER')")
    public CartResponse updateCart(Long id,UpdateCartItemRequest request) {
      var customer = getCurrentCustomer();

        var cart = customer.getCart();
        if (cart == null) {
            throw new ResourceNotFoundException(ErrorCode.CART_NOT_FOUND, "Cart not found for the authenticated user");
        }
            var item = cart.getCartItems()
                    .stream()
                    .filter(cartItem -> cartItem.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CART_ITEM_NOT_FOUND , "card item not found with id "+ id));


        var productStock = item.getProduct().getStockQuantity();
        if (request.getQuantity() > productStock) {
            throw new EcommerceAppException(ErrorCode.CART_ITEM_INSUFFICIENT_STOCK);
        }
        item.setQuantity(request.getQuantity());
        cartRepository.save(cart);


        return cartMapper.toCartResponse(cart);

    }

    @PreAuthorize("hasRole('USER')")
    public CartResponse getCart() {


      var customer = getCurrentCustomer();

        var cart = customer.getCart();
        if (cart == null) {

            throw new ResourceNotFoundException(ErrorCode.CART_NOT_FOUND, "cart not found for customer with id: " + customer.getId());
        }

        return cartMapper.toCartResponse(cart);
    }

    @PreAuthorize("hasRole('USER')")
    public void addToCart(AddToCartRequest request) {
        var customer = getCurrentCustomer();
        var cart = getOrCreateCart(customer);

        var product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CART_ITEM_PRODUCT_NOT_FOUND,
                        "product not found with id: " + request.getProductId()));

        if (!product.isActive()) {
            throw new EcommerceAppException(
                    ErrorCode.CART_ITEM_PRODUCT_DISABLED,
                    "product disabled with id " + request.getProductId());
        }

        var existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.getProductId()))
                .findFirst();

        if (request.getQuantity() == 0) {
            existingItem.ifPresent(item -> cart.getCartItems().remove(item));
            cartRepository.save(cart);
            return;
        }

        int newQuantity = existingItem
                .map(item -> item.getQuantity() + request.getQuantity())
                .orElse(request.getQuantity());

        if (newQuantity > product.getStockQuantity()) {
            throw new EcommerceAppException(
                    ErrorCode.CART_ITEM_INSUFFICIENT_STOCK,
                    "insufficient stock for product with id " + request.getProductId());
        }

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(newQuantity);
        } else {
            var item = new CartItem();
            item.setProduct(product);
            item.setQuantity(request.getQuantity());
            item.setCart(cart);
            cart.addCartItem(item);
        }

        cartRepository.save(cart);
    }

    @PreAuthorize("hasRole('USER')")
    public void removeFromCart(Long id) {
        var customer = getCurrentCustomer();

        var cart = customer.getCart();
        if (cart == null) {
            throw new ResourceNotFoundException(ErrorCode.CART_NOT_FOUND, "Cart not found for the authenticated user");
        }
        var item = cart.getCartItems()
                .stream()
                .filter(cartItem -> cartItem.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CART_ITEM_NOT_FOUND , "card item not found with id "+ id));

        cart.getCartItems().remove(item);
        cartRepository.save(cart);


    }

    @PreAuthorize("hasRole('USER')")
    public void clearCart() {
      var customer = getCurrentCustomer();

        var cart = customer.getCart();
        if (cart == null) {
            throw new ResourceNotFoundException(ErrorCode.CART_NOT_FOUND, "Cart not found for the authenticated user");
        }
        cart.getCartItems().clear();
        cartRepository.save(cart);
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

    private Cart getOrCreateCart(Customer customer) {
        var cart = customer.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setCustomer(customer);
            customer.setCart(cart);
        }
        return cart;
    }
}

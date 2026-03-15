package com.ecom.order.services;

import com.ecom.order.clients.ProductServiceClient;
import com.ecom.order.clients.UserServiceClient;
import com.ecom.order.dtos.CartItemRequest;
import com.ecom.order.dtos.ProductResponse;
import com.ecom.order.dtos.UserResponse;
import com.ecom.order.models.CartItem;
import com.ecom.order.repositories.CartItemRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;
    private int attempt =0;

//    @CircuitBreaker(name = "productService", fallbackMethod = "addProductToCartFallback")
    @Retry(name = "productService", fallbackMethod = "addProductToCartFallback")
    public boolean addProductToCart(String userId, CartItemRequest cartItemRequest) {
        System.out.println("Attempting to add product to cart: " + attempt+ " Attempt");
        attempt++;
        ProductResponse product = productServiceClient.fetchProductById(Long.valueOf(cartItemRequest.getProductId()));
        if (product == null) {
            return false;
        }
        if(product.getStockQuantity() < cartItemRequest.getQuantity()) {
            return false;
        }
        UserResponse user = userServiceClient.fetchUserById(userId);
        System.out.println(user);
        if(user == null) {
            return false;
        }
        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(userId, cartItemRequest.getProductId());
        if(existingCartItem != null) {
            existingCartItem.setProductId(String.valueOf(cartItemRequest.getProductId()));
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemRequest.getQuantity());
            existingCartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
            cartItemRepository.save(existingCartItem);
            return true;
        }else{
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(String.valueOf(cartItemRequest.getProductId()));
            cartItem.setQuantity(cartItemRequest.getQuantity());
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItemRequest.getQuantity())));
            cartItemRepository.save(cartItem);
            return true;
        }
    }

    private boolean addProductToCartFallback(String userId, CartItemRequest cartItemRequest, Exception e) {
        System.out.println("Falling back to add product to cart");
        return false;
    }

    public boolean deleteProductFromCart(String productId, String userId) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, String.valueOf(productId));
        if(cartItem != null) {
            cartItemRepository.deleteByUserIdAndProductId(userId, productId);
            return true;
        }
        return false;
    }

    public List<CartItem> getCart(String userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}

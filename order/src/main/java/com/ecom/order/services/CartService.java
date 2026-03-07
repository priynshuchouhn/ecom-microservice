package com.ecom.order.services;

import com.ecom.order.clients.ProductServiceClient;
import com.ecom.order.clients.UserServiceClient;
import com.ecom.order.dtos.CartItemRequest;
import com.ecom.order.dtos.ProductResponse;
import com.ecom.order.dtos.UserResponse;
import com.ecom.order.models.CartItem;
import com.ecom.order.repositories.CartItemRepository;
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


    public boolean addProductToCart(String userId, CartItemRequest cartItemRequest) {
        ProductResponse product = productServiceClient.fetchProductById(Long.valueOf(cartItemRequest.getProductId()));
        if (product == null) {
            return false;
        }
        System.out.println("Product exist");
        if(product.getStockQuantity() < cartItemRequest.getQuantity()) {
            return false;
        }
        System.out.println("Product stocks exist");
        UserResponse user = userServiceClient.fetchUserById(userId);
        System.out.println(user);
        if(user == null) {
            return false;
        }
        System.out.println("User exist");
//        User user = userOps.get();
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

package com.ecom.order.controllers;

import com.ecom.order.dtos.CartItemRequest;
import com.ecom.order.models.CartItem;
import com.ecom.order.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<String> addProductToCart(@RequestHeader("X-USER-ID") String userId, @RequestBody CartItemRequest cartItem) {
        if(!cartService.addProductToCart(userId, cartItem)){
            return new ResponseEntity<>("Product is out of stock or User or Product not found",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable String productId, @RequestHeader("X-USER-ID") String userId) {
        boolean isDeleted = cartService.deleteProductFromCart(productId, userId);
        return isDeleted ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCartItems(@RequestHeader("X-USER-ID") String userId) {
        return new ResponseEntity<>(cartService.getCart(userId), HttpStatus.OK);
    }
}

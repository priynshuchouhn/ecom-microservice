package com.ecom.order.services;

import com.ecom.order.clients.UserServiceClient;
import com.ecom.order.dtos.OrderItem;
import com.ecom.order.dtos.OrderItemDTO;
import com.ecom.order.dtos.OrderResponse;
import com.ecom.order.dtos.UserResponse;
import com.ecom.order.models.*;
import com.ecom.order.repositories.OrderRepository;
import com.ecom.order.models.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;

    public Optional<OrderResponse> createOrder(String userId) {
        //Validate for cart
        List<CartItem> cartItems = cartService.getCart(userId);
        if(cartItems.isEmpty()){
            return Optional.empty();
        }

        //Validate user
        UserResponse user = userServiceClient.fetchUserById(userId);
        if(user == null) return Optional.empty();
//        User user = userOptional.get();

        // calculate total price
        BigDecimal totalPrice = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);
        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> new OrderItem(
                        null,
                        item.getProductId(),
                        item.getPrice(),
                        item.getQuantity(),
                        order
                )).toList();
        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        //Clear cart

        cartService.clearCart(userId);
        return Optional.of(mapToOrderResponse(savedOrder));
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getItems().stream()
                        .map(item -> new OrderItemDTO(
                                item.getId(),
                                item.getProductId(),
                                item.getQuantity(),
                                item.getPrice()
                        )).toList(),
                order.getCreatedAt()
        );
    }
}

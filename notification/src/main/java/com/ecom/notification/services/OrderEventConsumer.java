package com.ecom.notification.services;

import com.ecom.notification.dtos.OrderCreatedEvent;
import com.ecom.notification.dtos.OrderStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderEventConsumer {
    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void handleOrderEvent(OrderCreatedEvent orderEvent) {
        System.out.println("Order event received: " + orderEvent);

        long orderId = orderEvent.getOrderId();
        OrderStatus orderStatus = orderEvent.getStatus();
        String userId =  orderEvent.getUserId();

        System.out.println("Order Id: " + orderId + " Order Status: " + orderStatus + " UserId: " + userId);
    }
}

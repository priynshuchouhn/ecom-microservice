package com.ecom.order.dtos;

import com.ecom.order.models.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "mst_order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId;
    private BigDecimal price;
    private Integer quantity;
    @ManyToOne()
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}

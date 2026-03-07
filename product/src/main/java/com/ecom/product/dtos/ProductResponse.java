package com.ecom.product.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private String category;
    private Integer stockQuantity;
    private Boolean isActive;
}

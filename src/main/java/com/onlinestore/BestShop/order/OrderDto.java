package com.onlinestore.BestShop.order;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDto {
    private String id;
    private String currency;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private Integer totalPrice;
}

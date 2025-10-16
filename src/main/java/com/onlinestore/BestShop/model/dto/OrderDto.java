package com.onlinestore.BestShop.model.dto;

import com.onlinestore.BestShop.model.OrderStatus;
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

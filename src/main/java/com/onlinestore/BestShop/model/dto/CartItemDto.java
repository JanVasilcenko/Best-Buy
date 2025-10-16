package com.onlinestore.BestShop.model.dto;

import lombok.Data;

@Data
public class CartItemDto {
    private ProductDto product;
    private int quantity;
    private Long totalPrice;
}

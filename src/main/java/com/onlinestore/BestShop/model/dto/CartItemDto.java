package com.onlinestore.BestShop.model.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class CartItemDto {
    private ProductDto product;
    private int quantity;
    private Long totalPrice;
}

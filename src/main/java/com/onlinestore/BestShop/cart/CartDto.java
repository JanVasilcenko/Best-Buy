package com.onlinestore.BestShop.cart;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CartDto {
    private String id;
    private List<CartItemDto> items = new ArrayList<>();
    private long totalPrice;
}

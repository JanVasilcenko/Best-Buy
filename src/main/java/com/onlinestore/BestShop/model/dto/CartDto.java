package com.onlinestore.BestShop.model.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class CartDto {
    private String id;
    private List<CartItemDto> items = new ArrayList<>();
    private long totalPrice;
}

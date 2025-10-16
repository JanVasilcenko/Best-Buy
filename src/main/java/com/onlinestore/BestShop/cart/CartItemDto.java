package com.onlinestore.BestShop.cart;

import com.onlinestore.BestShop.product.ProductDto;
import lombok.Data;

@Data
public class CartItemDto {
    private ProductDto product;
    private int quantity;
    private Long totalPrice;
}

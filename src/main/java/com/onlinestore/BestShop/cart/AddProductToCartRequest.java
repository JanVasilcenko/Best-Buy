package com.onlinestore.BestShop.cart;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class AddProductToCartRequest {
    private String productId;
    @PositiveOrZero(message = "Quantity of a product must be bigger than zero")
    private Integer quantity;
}

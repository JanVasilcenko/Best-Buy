package com.onlinestore.BestShop.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotBlank(message = "Cart id must be provided")
    private String cartId;
}

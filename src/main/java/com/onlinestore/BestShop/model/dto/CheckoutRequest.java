package com.onlinestore.BestShop.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CheckoutRequest {
    @NotBlank(message = "Cart id must be provided")
    private String cartId;
}

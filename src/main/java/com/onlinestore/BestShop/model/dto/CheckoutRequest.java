package com.onlinestore.BestShop.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CheckoutRequest {
    @JsonProperty("id")
    @JsonAlias({"cartId", "cart_id"})
    private String id;
}

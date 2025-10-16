package com.onlinestore.BestShop.model.dto;

import lombok.Data;

@Data
public class CheckoutResponse {
    private String orderId;
    private String paymentIntentClientSecret;

    public CheckoutResponse(String orderId, String paymentIntentClientSecret){
        this.orderId = orderId;
        this.paymentIntentClientSecret = paymentIntentClientSecret;
    }
}

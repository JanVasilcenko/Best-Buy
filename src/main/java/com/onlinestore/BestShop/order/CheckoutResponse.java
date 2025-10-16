package com.onlinestore.BestShop.order;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CheckoutResponse {
    private String orderId;
    private String paymentIntentClientSecret;

    public CheckoutResponse(String orderId, String paymentIntentClientSecret){
        this.orderId = orderId;
        this.paymentIntentClientSecret = paymentIntentClientSecret;
    }
}

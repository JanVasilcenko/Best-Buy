package com.onlinestore.BestShop.order;

public interface PaymentGateway {
    CheckoutSession createCheckoutSession(Order order);
}

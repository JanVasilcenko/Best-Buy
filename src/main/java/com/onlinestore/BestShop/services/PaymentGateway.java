package com.onlinestore.BestShop.services;

import com.onlinestore.BestShop.model.CheckoutSession;
import com.onlinestore.BestShop.model.Order;

public interface PaymentGateway {
    CheckoutSession createCheckoutSession(Order order);
}

package com.onlinestore.BestShop.services;

import com.onlinestore.BestShop.exceptions.PaymentException;
import com.onlinestore.BestShop.model.CheckoutSession;
import com.onlinestore.BestShop.model.Order;
import com.onlinestore.BestShop.model.OrderItem;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripePaymentGateway implements PaymentGateway {

    @Value("${websiteUrl}")
    private String websiteUrl;

    @Override
    public CheckoutSession createCheckoutSession(Order order) {
        try {
            SessionCreateParams.Builder builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "/checkout-success?orderId" + order.getId())
                    .setCancelUrl(websiteUrl + "/checkout-cancel")
                    .putMetadata("order_id", order.getId());

            order.getOrderItems().forEach(item -> {
                SessionCreateParams.LineItem lineItem = createLineItem(item);
                builder.addLineItem(lineItem);
            });

            Session session = Session.create(builder.build());
            return new CheckoutSession(session.getUrl());
        } catch (StripeException e) {
            e.printStackTrace();
            throw new PaymentException();
        }
    }

    private SessionCreateParams.LineItem createLineItem(OrderItem item) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(Long.valueOf(item.getQuantity()))
                .setPriceData(createPriceData(item)
                ).build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("czk")
                .setUnitAmount(Long.valueOf(item.getUnitPrice() * 100))
                .setProductData(createProductData(item)
                ).build();
    }

    private SessionCreateParams.LineItem.PriceData.ProductData createProductData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(item.getProduct().getName())
                .setDescription(item.getProduct().getDescription())
                .build();
    }
}

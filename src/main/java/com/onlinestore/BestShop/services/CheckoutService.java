package com.onlinestore.BestShop.services;

import com.onlinestore.BestShop.exceptions.NotFoundException;
import com.onlinestore.BestShop.exceptions.PaymentException;
import com.onlinestore.BestShop.model.*;
import com.onlinestore.BestShop.model.dto.CheckoutRequest;
import com.onlinestore.BestShop.model.dto.CheckoutResponse;
import com.onlinestore.BestShop.persistence.CartRepository;
import com.onlinestore.BestShop.persistence.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final PaymentGateway paymentGateway;
    private final CartService cartService;
    private final ProductService productService;

    @Value("${websiteUrl")
    private String websiteUrl;

    @Transactional(rollbackFor = PaymentException.class)
    public CheckoutResponse checkout(String id) throws PaymentException {
        System.out.println(id);
        Cart cart = cartRepository.findById(id).orElseThrow(() -> new NotFoundException("Cart with specified id does not exist"));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(authService.getCurrentUser());
        order.setStatus(OrderStatus.NEW);
        order.setCurrency("CZK");
        order.setTotalPrice(Math.toIntExact(cart.getTotalPrice()));

        cart.getCartItems().forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setCurrency("CZK");
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProduct(cartItem.getProduct());
            order.getOrderItems().add(orderItem);
        });

        orderRepository.save(order);

        CheckoutSession checkoutSession = paymentGateway.createCheckoutSession(order);

        cartService.clearTheCart();

        return new CheckoutResponse(order.getId(), checkoutSession.getCheckoutUrl());
    }
}

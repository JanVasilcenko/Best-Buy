package com.onlinestore.BestShop.order;

import com.onlinestore.BestShop.auth.AuthService;
import com.onlinestore.BestShop.cart.Cart;
import com.onlinestore.BestShop.cart.CartRepository;
import com.onlinestore.BestShop.cart.CartService;
import com.onlinestore.BestShop.exceptions.NotFoundException;
import com.onlinestore.BestShop.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
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
    private final OrderMapper orderMapper;

    @Value("${websiteUrl")
    private String websiteUrl;

    @Transactional(rollbackFor = PaymentException.class)
    public CheckoutResponse checkout(String cartId) throws PaymentException {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new NotFoundException("Cart with specified id does not exist"));

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

    public OrderDto getOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order with specified id does not exist"));
        OrderDto orderDto = new OrderDto();

        orderMapper.updateFromOrder(order, orderDto);

        return orderDto;
    }

    public void cancelOrder(String orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order with specified id does not exist"));

        if (order.getStatus() == OrderStatus.PAID){
            throw new IllegalStateException("Cannot cancel paid order");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public PagedModel<Order> getOrders(int pageNumber, int size){
        PageRequest pageRequest = PageRequest.of(pageNumber, size);
        Page<Order> page = orderRepository.findByUser_IdOrderByStatusAsc(authService.getCurrentUser().getId(), pageRequest);
        return new PagedModel<>(page);
    }
}

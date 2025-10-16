package com.onlinestore.BestShop.services;

import com.onlinestore.BestShop.exceptions.NotFoundException;
import com.onlinestore.BestShop.model.Order;
import com.onlinestore.BestShop.model.OrderStatus;
import com.onlinestore.BestShop.persistence.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private OrderRepository orderRepository;

    public void setStatusOfOrder(String orderId, OrderStatus status){
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new NotFoundException("Order with speicified id not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }
}

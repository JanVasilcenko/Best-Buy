package com.onlinestore.BestShop.integration;

import com.onlinestore.BestShop.exceptions.NotFoundException;
import com.onlinestore.BestShop.order.Order;
import com.onlinestore.BestShop.order.OrderStatus;
import com.onlinestore.BestShop.user.Role;
import com.onlinestore.BestShop.user.User;
import com.onlinestore.BestShop.order.OrderRepository;
import com.onlinestore.BestShop.user.UserRepository;
import com.onlinestore.BestShop.order.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class PaymentServiceTest {

    @Autowired private OrderRepository orderRepository;
    @Autowired private PaymentService paymentService;
    @Autowired private UserRepository userRepository;

    @Test
    void setStatusOrder(){
        //Arrange
        User user = new User();
        user.setEmail("example@example.com");
        user.setPasswordHash("x");
        user.setRole(Role.USER);
        user = userRepository.save(user);

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.NEW);
        order.setCurrency("CZK");
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalPrice(100);
        order = orderRepository.save(order);

        //Act
        paymentService.setStatusOfOrder(order.getId(), OrderStatus.PAID);

        //Assert
        Order refreshed = orderRepository.findById(order.getId()).orElseThrow();
        assertEquals(OrderStatus.PAID, refreshed.getStatus());
    }

    @Test
    void setStatusOrder_missing(){
        assertThrows(NotFoundException.class, ()->paymentService.setStatusOfOrder("NONEXISTENT", OrderStatus.FAILED));
    }
}

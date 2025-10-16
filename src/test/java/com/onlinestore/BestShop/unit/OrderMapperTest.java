package com.onlinestore.BestShop.unit;

import com.onlinestore.BestShop.order.Order;
import com.onlinestore.BestShop.order.OrderMapper;
import com.onlinestore.BestShop.order.OrderStatus;
import com.onlinestore.BestShop.order.OrderDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
public class OrderMapperTest {
    @Autowired
    private OrderMapper orderMapper;

    @Test
    void mapsAllFieldsOrderToOrderDto(){
        //Arrange
        Order order = new Order();
        order.setId("A");
        order.setCurrency("CZK");
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW);
        order.setTotalPrice(100);

        OrderDto orderDto = new OrderDto();

        //Act
        orderMapper.updateFromOrder(order, orderDto);

        //Assert
        assertEquals(orderDto.getId(), order.getId());
        assertEquals(orderDto.getCurrency(), order.getCurrency());
        assertEquals(orderDto.getCreatedAt(), order.getCreatedAt());
        assertEquals(orderDto.getStatus(), order.getStatus());
        assertEquals(orderDto.getTotalPrice(), order.getTotalPrice());
    }
}

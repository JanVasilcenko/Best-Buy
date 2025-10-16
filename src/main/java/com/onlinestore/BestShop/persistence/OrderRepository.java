package com.onlinestore.BestShop.persistence;

import com.onlinestore.BestShop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}

package com.onlinestore.BestShop.persistence;

import com.onlinestore.BestShop.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findByUser_IdOrderByStatusAsc(@NonNull String id, Pageable pageable);
}

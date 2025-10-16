package com.onlinestore.BestShop.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, String> {
    Optional<CartItem> findByCart_IdAndProduct_Id(String id, String id1);
}

package com.onlinestore.BestShop.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByUser_EmailIgnoreCase(String email);
}

package com.onlinestore.BestShop.persistence;

import com.onlinestore.BestShop.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByUser_EmailIgnoreCase(String email);
}

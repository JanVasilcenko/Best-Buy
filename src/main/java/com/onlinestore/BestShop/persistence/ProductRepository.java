package com.onlinestore.BestShop.persistence;


import com.onlinestore.BestShop.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
    Page<Product> findByNameLikeIgnoreCase(String name, Pageable pageable);
}

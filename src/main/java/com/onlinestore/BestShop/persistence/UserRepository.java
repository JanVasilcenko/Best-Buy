package com.onlinestore.BestShop.persistence;

import com.onlinestore.BestShop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}

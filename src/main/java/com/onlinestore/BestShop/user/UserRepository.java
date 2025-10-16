package com.onlinestore.BestShop.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, String> {
    @Query("select (count(u) > 0) from User u where upper(u.email) = upper(?1)")
    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmail(String email);
}

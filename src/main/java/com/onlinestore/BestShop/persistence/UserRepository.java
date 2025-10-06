package com.onlinestore.BestShop.persistence;

import com.onlinestore.BestShop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserRepository extends JpaRepository<User, String> {
    @Query("select (count(u) > 0) from User u where upper(u.email) = upper(?1)")
    boolean existsByEmailIgnoreCase(String email);

}

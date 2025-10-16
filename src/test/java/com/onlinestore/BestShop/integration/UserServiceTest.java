package com.onlinestore.BestShop.integration;

import com.onlinestore.BestShop.user.DuplicateUserException;
import com.onlinestore.BestShop.user.RegisterUserRequest;
import com.onlinestore.BestShop.user.Role;
import com.onlinestore.BestShop.user.User;
import com.onlinestore.BestShop.user.UserRepository;
import com.onlinestore.BestShop.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {
    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @AfterEach
    void cleanup(){
        userRepository.deleteAll();
    }

    @Test
    void registerUser(){
        //Arrange
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setEmail("example@example.com");
        registerUserRequest.setPassword("new");

        //Act
        userService.registerUser(registerUserRequest);

        //Assert
        User saved = userRepository.findByEmail("example@example.com").orElseThrow();
        assertEquals("example@example.com", saved.getEmail());
        assertEquals(Role.USER, saved.getRole());

        assertNotEquals("new", saved.getPasswordHash());
        assertTrue(passwordEncoder.matches("new", saved.getPasswordHash()));
    }

    @Test
    void registerUser_duplicateEmail(){
        //Arrange
        User existing = new User();
        existing.setEmail("example@example.com");
        existing.setPasswordHash(passwordEncoder.encode("password"));
        existing.setRole(Role.USER);
        userRepository.save(existing);

        RegisterUserRequest duplicate = new RegisterUserRequest();
        duplicate.setEmail("example@example.com");
        duplicate.setPassword("password");

        //Act + Assert
        assertThrows(DuplicateUserException.class, () -> userService.registerUser(duplicate));

        assertEquals(1, userRepository.count());
    }
}

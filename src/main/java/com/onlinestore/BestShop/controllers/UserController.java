package com.onlinestore.BestShop.controllers;

import com.onlinestore.BestShop.exceptions.DuplicateUserException;
import com.onlinestore.BestShop.model.RegisterUserRequest;
import com.onlinestore.BestShop.model.User;
import com.onlinestore.BestShop.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<?> GetUserById(@RequestParam(name = "userID") String userID){
        User user = userService.getUserByID(userID);

        return user == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegisterUserRequest registerUserRequest){
        userService.registerUser(registerUserRequest);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<Map<String,String>> handleDuplicateUser(){
        return ResponseEntity.badRequest().body(
                Map.of("Error", "Email is already registered")
        );
    }
}

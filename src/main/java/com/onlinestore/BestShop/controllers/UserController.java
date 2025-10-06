package com.onlinestore.BestShop.controllers;

import com.onlinestore.BestShop.model.User;
import com.onlinestore.BestShop.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}

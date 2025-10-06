package com.onlinestore.BestShop.controllers;

import com.onlinestore.BestShop.model.User;
import com.onlinestore.BestShop.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<?> GetUserById(@RequestParam(name = "userID") String userID){
        User user = userService.getUserByID(userID);

        return user == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(user);
    }
}

package com.onlinestore.BestShop.controllers;

import com.onlinestore.BestShop.model.LoginRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.ok().build();
    }
}

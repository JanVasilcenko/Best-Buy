package com.onlinestore.BestShop.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vi/cart")
public class CartController {

    @GetMapping
    public ResponseEntity getCart(){
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public void deleteCart(){

    }

    @PostMapping
    public void addProduct(){

    }

    @PatchMapping
    public void changeQuantity(){

    }
}

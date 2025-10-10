package com.onlinestore.BestShop.controllers;

import com.onlinestore.BestShop.exceptions.RequestExceedingStockException;
import com.onlinestore.BestShop.model.Cart;
import com.onlinestore.BestShop.model.dto.AddProductToCartRequest;
import com.onlinestore.BestShop.services.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Carts")
@RestController
@RequestMapping("/api/vi/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity getCart(){
        return ResponseEntity.ok(cartService.getCart());
    }

    @DeleteMapping("/items")
    public ResponseEntity deleteCart(){
        cartService.clearTheCart();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("items")
    public ResponseEntity addProductToCart(@RequestBody AddProductToCartRequest addProductToCartRequest) {
        Cart cart = cartService.addProductToCart(addProductToCartRequest);
        return ResponseEntity.ok(cart);
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity changeQuantityOfProductInCart(@PathVariable(name = "itemId") String itemId, @RequestBody Integer quantity) {
        cartService.changeQuantityOfProduct(itemId, quantity);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(RequestExceedingStockException.class)
    public ResponseEntity handleRequestExceedingStockException(){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("Error", "Requested quantity exceeds stock"));
    }
}

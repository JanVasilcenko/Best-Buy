package com.onlinestore.BestShop.cart;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Carts")
@RestController
@RequestMapping("/api/v1/cart")
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
    public ResponseEntity<CartDto> addProductToCart(@RequestBody AddProductToCartRequest addProductToCartRequest) {
        return ResponseEntity.ok(cartService.addProductToCart(addProductToCartRequest));
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

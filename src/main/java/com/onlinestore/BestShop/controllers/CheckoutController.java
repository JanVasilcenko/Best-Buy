package com.onlinestore.BestShop.controllers;

import com.onlinestore.BestShop.exceptions.PaymentException;
import com.onlinestore.BestShop.model.dto.CheckoutRequest;
import com.onlinestore.BestShop.model.dto.CheckoutResponse;
import com.onlinestore.BestShop.services.CheckoutService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Checkout")
@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody @Valid CheckoutRequest checkoutRequest){
        return ResponseEntity.ok(checkoutService.checkout(checkoutRequest));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String,String>> handleIllegalStateException(IllegalStateException e){
        return ResponseEntity.badRequest().body(Map.of("Error", e.getMessage()));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<Map<String, String>> handlePaymentException(){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("Error","Error creating checkout session"));
    }
}

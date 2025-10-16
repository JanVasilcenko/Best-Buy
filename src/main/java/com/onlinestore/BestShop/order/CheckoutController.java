package com.onlinestore.BestShop.order;

import com.onlinestore.BestShop.order.PaymentException;
import com.onlinestore.BestShop.order.Order;
import com.onlinestore.BestShop.order.CheckoutResponse;
import com.onlinestore.BestShop.order.OrderDto;
import com.onlinestore.BestShop.order.CheckoutService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Checkout")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@RequestParam("id") String id){
        return ResponseEntity.ok(checkoutService.checkout(id));
    }

    @GetMapping("/orders")
    public ResponseEntity<PagedModel<Order>> getOrders(@RequestParam(value = "page") Integer page, @RequestParam(value = "size") Integer size) {
        return ResponseEntity.ok(checkoutService.getOrders(page, size));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable("id") String id){
        return ResponseEntity.ok(checkoutService.getOrder(id));
    }

    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity cancelOrder(@PathVariable("id") String id){
        checkoutService.cancelOrder(id);
        return ResponseEntity.ok().build();
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

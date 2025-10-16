package com.onlinestore.BestShop.order;

import com.onlinestore.BestShop.order.OrderStatus;
import com.onlinestore.BestShop.order.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payments")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentController {

    @Value("${stripe.webhookSecretKey}")
    private String webhookSecretKey;

    private final PaymentService paymentService;

    @PostMapping("/webhooks/fake")
    public ResponseEntity<Void> handleWebhook(@RequestHeader("Stripe-Signature") String signature, @RequestBody String payload){
        try {
            Event event = Webhook.constructEvent(payload, signature, webhookSecretKey);

            StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);
            PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
            String orderId = paymentIntent.getMetadata().get("order_id");

            switch (event.getType()) {
                case "payment_intent.succeeded" -> paymentService.setStatusOfOrder(orderId, OrderStatus.PAID);
                case "payment_intent.failed" -> paymentService.setStatusOfOrder(orderId, OrderStatus.FAILED);
            }
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
}

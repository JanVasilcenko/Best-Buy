package com.onlinestore.BestShop;

import com.onlinestore.BestShop.model.*;
import com.onlinestore.BestShop.model.dto.CheckoutRequest;
import com.onlinestore.BestShop.model.dto.CheckoutResponse;
import com.onlinestore.BestShop.persistence.CartRepository;
import com.onlinestore.BestShop.persistence.OrderRepository;
import com.onlinestore.BestShop.persistence.ProductRepository;
import com.onlinestore.BestShop.persistence.UserRepository;
import com.onlinestore.BestShop.services.AuthService;
import com.onlinestore.BestShop.services.CartService;
import com.onlinestore.BestShop.services.CheckoutService;
import com.onlinestore.BestShop.services.PaymentGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class CheckoutServiceTest {

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderRepository orderRepository;

    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private PaymentGateway paymentGateway;
    @MockitoBean
    private CartService cartService;

    private static final String CHECKOUT_SESSION_URL = "https://pay.exmaple/checkout/session";

    @Test
    @Transactional
    void checkout_sunny_scenario() throws Exception {
        //Arrange
        User user = saveUser("a@a.cz");
        when(authService.getCurrentUser()).thenReturn(user);

        Product product = saveProduct("SKU-1", "Name", 100, "CZK", 10);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.addItem(product);

        cart = cartRepository.saveAndFlush(cart);

        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setCartId(cart.getId());

        when(paymentGateway.createCheckoutSession(any(Order.class)))
                .thenReturn(new CheckoutSession(CHECKOUT_SESSION_URL));

        //Act
        CheckoutResponse checkoutResponse = checkoutService.checkout(checkoutRequest);

        //Assert
        assertNotNull(checkoutResponse);
        assertNotNull(checkoutResponse.getOrderId());
        assertEquals(CHECKOUT_SESSION_URL, checkoutResponse.getPaymentIntentClientSecret());

        Order order = orderRepository.findById(checkoutResponse.getOrderId()).orElseThrow();
        assertEquals(OrderStatus.NEW, order.getStatus());
        assertEquals("CZK", order.getCurrency());
        assertEquals(user.getId(), order.getUser().getId());
        assertEquals(1, order.getOrderItems().size());

        OrderItem orderItem = order.getOrderItems().stream().findFirst().get();
        assertEquals(100, orderItem.getUnitPrice());
        assertEquals(1, orderItem.getQuantity());
        assertEquals("CZK", orderItem.getCurrency());

        verify(paymentGateway, times(1)).createCheckoutSession(any(Order.class));
        verify(cartService, times(1)).clearTheCart();
    }

    private User saveUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("a");
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    private Product saveProduct(String sku, String name, int price, String currency, int quantity) {
        Product product = new Product();
        product.setSku(sku);
        product.setName(name);
        product.setDescription("b");
        product.setPrice(price);
        product.setCurrency(currency);
        product.setQuantity(quantity);
        return productRepository.save(product);
    }
}

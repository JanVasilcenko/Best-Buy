package com.onlinestore.BestShop.integration;

import com.onlinestore.BestShop.auth.AuthService;
import com.onlinestore.BestShop.cart.Cart;
import com.onlinestore.BestShop.cart.CartRepository;
import com.onlinestore.BestShop.cart.CartService;
import com.onlinestore.BestShop.exceptions.NotFoundException;
import com.onlinestore.BestShop.order.*;
import com.onlinestore.BestShop.product.Product;
import com.onlinestore.BestShop.product.ProductRepository;
import com.onlinestore.BestShop.user.Role;
import com.onlinestore.BestShop.user.User;
import com.onlinestore.BestShop.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PagedModel;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
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
    @MockitoBean
    private OrderMapper orderMapper;

    private static final String CHECKOUT_SESSION_URL = "https://pay.exmaple/checkout/session";

    @Test
    @Transactional
    void checkout_sunny_scenario() {
        //Arrange
        User user = saveUser("a@a.cz");
        when(authService.getCurrentUser()).thenReturn(user);

        Product product = saveProduct("SKU-1", "Name", 100, "CZK", 10);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.addItem(product);

        cart = cartRepository.saveAndFlush(cart);

        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setId(cart.getId());

        when(paymentGateway.createCheckoutSession(any(Order.class)))
                .thenReturn(new CheckoutSession(CHECKOUT_SESSION_URL));

        //Act
        CheckoutResponse checkoutResponse = checkoutService.checkout(checkoutRequest.getId());

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

    @Test
    void checkout_emptyCart() {
        //Arrange
        User user = saveUser("empty@empty.cz");
        when(authService.getCurrentUser()).thenReturn(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cart = cartRepository.saveAndFlush(cart);

        //Act
        Cart finalCart = cart;
        assertThrows(IllegalStateException.class, () -> checkoutService.checkout(finalCart.getId()));

        //Assert
        verifyNoInteractions(paymentGateway);
        verify(cartService, never()).clearTheCart();
        assertEquals(0, orderRepository.count());
    }

    @Test
    void checkout_cartNotFound(){
        assertThrows(NotFoundException.class, ()-> checkoutService.checkout("NONEXISTENT"));
        verifyNoInteractions(paymentGateway, cartService);
    }

    @Test
    void checkout_paymentGatewayFail_rollback(){
        //Arrange
        User user = saveUser("failed@failed.cz");
        when(authService.getCurrentUser()).thenReturn(user);
        Product product = saveProduct("Something", "Product", 999, "CZK", 3);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.addItem(product);
        cart = cartRepository.saveAndFlush(cart);

        when(paymentGateway.createCheckoutSession(any(Order.class))).thenThrow(new PaymentException());

        //Act + Assert
        Cart finalCart = cart;
        assertThrows(PaymentException.class, () -> checkoutService.checkout(finalCart.getId()));
    }

    @Test
    void cancelOrder_notPaid(){
        //Arrange
        User user = saveUser("user@user.cz");

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.NEW);
        order.setCurrency("CZK");
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalPrice(999);
        order = orderRepository.saveAndFlush(order);

        //Act
        checkoutService.cancelOrder(order.getId());

        //Assert
        Order after = orderRepository.findById(order.getId()).orElseThrow();
        assertEquals(OrderStatus.CANCELLED, after.getStatus());
    }

    @Test
    void cancelOrder_paid(){
        //Arrange
        User user = saveUser("user@user.cz");
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PAID);
        order.setCurrency("CZK");
        order.setTotalPrice(999);
        order.setCreatedAt(LocalDateTime.now());
        order = orderRepository.saveAndFlush(order);

        //Act + Assert
        Order finalOrder = order;
        assertThrows(IllegalStateException.class, ()-> checkoutService.cancelOrder(finalOrder.getId()));
    }

    @Test
    void getOrders(){
        //Arrange
        User user = saveUser("user@user.cz");
        when(authService.getCurrentUser()).thenReturn(user);

        orderRepository.saveAll(List.of(
                order(user, OrderStatus.NEW),
                order(user, OrderStatus.CANCELLED),
                order(user, OrderStatus.PAID)
        ));

        //Act
        PagedModel<Order> pageZero = checkoutService.getOrders(0,10);
        List<OrderStatus> statuses =pageZero.getContent().stream().map(Order::getStatus).toList();

        //Assert
        assertEquals(List.of(OrderStatus.CANCELLED, OrderStatus.NEW, OrderStatus.PAID), statuses);
        assertEquals(3, pageZero.getContent().size());
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

    private Order order(User u, OrderStatus s) {
        Order o = new Order();
        o.setUser(u);
        o.setStatus(s);
        o.setCurrency("CZK");
        o.setTotalPrice(999);
        o.setCreatedAt(LocalDateTime.now());
        return o;
    }
}

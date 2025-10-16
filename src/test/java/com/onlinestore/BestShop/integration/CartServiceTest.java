package com.onlinestore.BestShop.integration;

import com.onlinestore.BestShop.auth.AuthService;
import com.onlinestore.BestShop.cart.*;
import com.onlinestore.BestShop.exceptions.NotFoundException;
import com.onlinestore.BestShop.product.Product;
import com.onlinestore.BestShop.product.ProductRepository;
import com.onlinestore.BestShop.product.ProductService;
import com.onlinestore.BestShop.user.Role;
import com.onlinestore.BestShop.user.User;
import com.onlinestore.BestShop.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CartServiceTest {
    @Autowired
    CartService cartService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    ProductRepository productRepository;

    @MockitoBean
    AuthService authService;
    @MockitoBean
    ProductService productService;
    @MockitoBean
    CartMapper cartMapper;

    @Test
    @Transactional
    void addProduct_createsCart_andAddsItem_firstTime() {
        // Arrange
        User user = saveUser("example@example.cz");
        userRepository.save(user);

        Product product = product("SKU", "name", "CZK", 999, 9999);
        product = productRepository.saveAndFlush(product);

        when(productService.getProductByID(product.getId())).thenReturn(product);

        when(authService.getCurrentUser()).thenReturn(user);

        AddProductToCartRequest request = new AddProductToCartRequest();
        request.setProductId(product.getId());
        request.setQuantity(2);

        // Act + Assert
        assertDoesNotThrow(() -> cartService.addProductToCart(request));
    }

    @Test
    @Transactional
    void addProduct_increaseQuantity() {
        //Arrange
        User user = saveUser("example@example.cz");
        when(authService.getCurrentUser()).thenReturn(user);

        Product product = product("SKU", "name", "CZK", 999, 9999);
        product = productRepository.save(product);
        when(productService.getProductByID(anyString()))
                .thenAnswer(inv -> productRepository.findById(inv.getArgument(0)).orElse(null));

        doAnswer(inv -> null).when(cartMapper).updateFromCart(any(), any());

        AddProductToCartRequest request = new AddProductToCartRequest();
        request.setProductId(product.getId());
        request.setQuantity(1);

        //Act
        cartService.addProductToCart(request);
        cartService.addProductToCart(request);

        //Assert
        Cart persisted = cartRepository.findByUser_EmailIgnoreCase(user.getEmail()).orElseThrow();
        assertEquals(1, persisted.getCartItems().size());
        CartItem cartItem = persisted.getCartItems().iterator().next();
        assertEquals(2, cartItem.getQuantity());
    }

    @Test
    void addProduct_exceedingStock() {
        //Arrange
        User user = saveUser("example@example.cz");
        when(authService.getCurrentUser()).thenReturn(user);

        Product product = product("SKU", "name", "CZK", 999, 1);
        when(productService.getProductByID(product.getId())).thenReturn(product);

        AddProductToCartRequest req = new AddProductToCartRequest();
        req.setProductId(product.getId());
        req.setQuantity(5);

        //Act + Assert
        assertThrows(RequestExceedingStockException.class, () -> cartService.addProductToCart(req));
        assertTrue(cartRepository.findByUser_EmailIgnoreCase(user.getEmail()).isEmpty());
    }

    @Test
    void addProduct_missingProduct() {
        //Arrange
        User user = saveUser("example@example.cz");
        when(authService.getCurrentUser()).thenReturn(user);
        when(productService.getProductByID("NONEXISTENT")).thenReturn(null);

        AddProductToCartRequest req = new AddProductToCartRequest();
        req.setProductId("NONEXISTENT");
        req.setQuantity(1);

        //Act + Assert
        assertThrows(NotFoundException.class, () -> cartService.addProductToCart(req));
    }

    @Test
    void getCart_noCart() {
        //Arrange
        User user = saveUser("example@example.cz");
        when(authService.getCurrentUser()).thenReturn(user);

        //Act + Assert
        assertThrows(NotFoundException.class, () -> cartService.getCart());
    }

    @Test
    void getCart_noUser() {
        when(authService.getCurrentUser()).thenReturn(null);
        assertThrows(NotFoundException.class, () -> cartService.getCart());
    }

    @Test
    @Transactional
    void clearTheCart() {
        //Arrange
        User user = saveUser("example@example.cz");
        when(authService.getCurrentUser()).thenReturn(user);

        Cart cart = new Cart();
        cart.setUser(user);
        Product product = product("SKU", "name", "CZK", 999, 1);
        product = productRepository.save(product);

        when(productService.getProductByID(anyString()))
                .thenAnswer(inv -> productRepository.findById(inv.getArgument(0)).orElse(null));

        cart.addItem(product, 2);
        cart = cartRepository.save(cart);

        //Act
        cartService.clearTheCart();

        //Assert
        Cart after = cartRepository.findById(cart.getId()).orElseThrow();
        assertEquals(0, after.getCartItems().size());
    }

    @Test
    void clearTheCart_noUser() {
        when(authService.getCurrentUser()).thenReturn(null);
        assertThrows(NotFoundException.class, () -> cartService.clearTheCart());
    }

    @Test
    void changeQuantity() {
        //Arrange
        User user = saveUser("example@example.cz");
        when(authService.getCurrentUser()).thenReturn(user);

        Product product = product("SKU", "name", "CZK", 999, 10);
        product = productRepository.save(product);

        when(productService.getProductByID(anyString()))
                .thenAnswer(inv -> productRepository.findById(inv.getArgument(0)).orElse(null));

        Cart cart = new Cart();
        cart.setUser(user);
        cart.addItem(product, 2);
        cart = cartRepository.save(cart);
        String cartItemId = cart.getCartItems().iterator().next().getId();

        //Act
        cartService.changeQuantityOfProduct(cartItemId, 5);

        //Assert
        CartItem updated = cartItemRepository.findById(cartItemId).orElseThrow();
        assertEquals(5, updated.getQuantity());
    }

    private User saveUser(String email) {
        User u = new User();
        u.setEmail(email);
        u.setCreatedAt(Instant.now());
        u.setPasswordHash("x");
        u.setRole(Role.USER);
        return userRepository.save(u);
    }

    private Product product(String sku, String name, String currency, int price, int stockQty) {
        Product p = new Product();
        //p.setId(java.util.UUID.randomUUID().toString());
        p.setSku(sku);
        p.setName(name);
        p.setDescription("D");
        p.setPrice(price);
        p.setCurrency(currency);
        p.setQuantity(stockQty);
        return p;
    }
}


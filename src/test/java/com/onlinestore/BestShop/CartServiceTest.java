package com.onlinestore.BestShop;

import com.onlinestore.BestShop.model.Product;
import com.onlinestore.BestShop.model.Role;
import com.onlinestore.BestShop.model.User;
import com.onlinestore.BestShop.model.dto.AddProductToCartRequest;
import com.onlinestore.BestShop.persistence.ProductRepository;
import com.onlinestore.BestShop.persistence.UserRepository;
import com.onlinestore.BestShop.services.AuthService;
import com.onlinestore.BestShop.services.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class CartServiceTest {
    @Autowired
    CartService cartService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;

    @MockitoBean AuthService authService;

    @Test
    @Transactional
    void addProduct_createsCart_andAddsItem_firstTime() {
        // Arrange
        User user = new User();
        user.setEmail("u@u.cz");
        user.setPasswordHash("x");
        user.setRole(Role.USER);
        userRepository.save(user);

        Product product = new Product();
        product.setSku("SKU-1");
        product.setName("N1");
        product.setDescription("D");
        product.setPrice(100);
        product.setCurrency("CZK");
        product.setQuantity(10);
        productRepository.save(product);

        when(authService.getCurrentUser()).thenReturn(user);

        AddProductToCartRequest request = new AddProductToCartRequest();
        request.setProductId(product.getId());
        request.setQuantity(2);

        // Act + Assert
        assertDoesNotThrow(() -> cartService.addProductToCart(request));
    }
}


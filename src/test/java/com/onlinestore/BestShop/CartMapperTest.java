package com.onlinestore.BestShop;

import com.onlinestore.BestShop.model.*;
import com.onlinestore.BestShop.model.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
public class CartMapperTest {
    @Autowired
    private CartMapper cartMapper;

    @Test
    void mapsAllFieldsCartToCartDto(){
        //Arrange
        Product product = new Product();
        product.setId("A");
        product.setQuantity(2);
        product.setPrice(10);
        product.setDescription("D");
        product.setName("name");

        Cart cart = new Cart();
        cart.setId("A");
        cart.addItem(product);
        cart.addItem(product);

        CartDto cartDto = new CartDto();

        //Act
        cartMapper.updateFromCart(cart, cartDto);

        //Assert
        assertEquals("A", cartDto.getId());
        assertEquals(20, cartDto.getTotalPrice());
        assertEquals(1, cartDto.getItems().size());
    }

    @Test
    void mapsAllFieldsCartItemToCartItemDto() {

        Product product = new Product();
        product.setId("A");

        //Arrange
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(10);
        cartItem.setUnitPrice(20);

        CartItemDto cartItemDto = new CartItemDto();

        //Act
        cartMapper.updateFromCartItem(cartItem, cartItemDto);

        //Assert
        assertEquals(10, cartItemDto.getQuantity());
        assertEquals(200, cartItemDto.getTotalPrice());
        assertEquals(product, cartItemDto.getProduct());
    }

}

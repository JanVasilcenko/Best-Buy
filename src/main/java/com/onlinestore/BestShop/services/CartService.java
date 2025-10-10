package com.onlinestore.BestShop.services;

import com.onlinestore.BestShop.exceptions.NotFoundException;
import com.onlinestore.BestShop.exceptions.RequestExceedingStockException;
import com.onlinestore.BestShop.model.Cart;
import com.onlinestore.BestShop.model.CartItem;
import com.onlinestore.BestShop.model.Product;
import com.onlinestore.BestShop.model.User;
import com.onlinestore.BestShop.model.dto.AddProductToCartRequest;
import com.onlinestore.BestShop.model.dto.ProductPatchRequest;
import com.onlinestore.BestShop.persistence.CartItemRepository;
import com.onlinestore.BestShop.persistence.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final AuthService authService;

    public Cart getCart(){
        User currentUser = authService.getCurrentUser();

        if (currentUser == null)
            throw new NotFoundException("Current user does not exist");

        return cartRepository.findByUser_EmailIgnoreCase(currentUser.getEmail()).orElseThrow(() -> new NotFoundException("Current user does not have any cart"));
    }

    @Transactional
    public Cart addProductToCart(AddProductToCartRequest addProductToCartRequest) {
        User currentUser = authService.getCurrentUser();

        if (currentUser == null)
            throw new NotFoundException("Current user does not exist");

        Product product = productService.getProductByID(addProductToCartRequest.getProductId());
        if (product == null)
            throw new NotFoundException("Selected product does not exist");

        if (product.getQuantity() < addProductToCartRequest.getQuantity())
            throw new RequestExceedingStockException();

        Cart cart = cartRepository.findByUser_EmailIgnoreCase(currentUser.getEmail()).orElseGet(()->{
           Cart c = new Cart();
           c.setUser(currentUser);
           c.setCreatedAt(Instant.now());
           return c;
        });

        CartItem cartItem = cartItemRepository.findByCart_IdAndProduct_Id(cart.getId(), product.getId()).orElseGet(()->{
           CartItem i = new CartItem();
           i.setCart(cart);
           i.setProduct(product);
           i.setQuantity(addProductToCartRequest.getQuantity());
           i.setUnitPrice(product.getPrice());
           i.setCreatedAt(Instant.now());
           cart.addItem(i);
           return i;
        });

        cartRepository.save(cart);
        return cart;
    }

    @Transactional
    public void clearTheCart(){
        User currentUser = authService.getCurrentUser();

        if (currentUser == null)
            throw new NotFoundException("Current user does not exist");

        Cart cart = cartRepository.findByUser_EmailIgnoreCase(currentUser.getEmail()).orElseThrow(() -> new NotFoundException("Cart not found"));

        cart.getCartItems().clear();
    }

    @Transactional
    public void changeQuantityOfProduct(String cartItemId, Integer quantity){
        User currentUser = authService.getCurrentUser();
        if (currentUser == null)
            throw new NotFoundException("Current user does not exist");

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() ->{
            throw new NotFoundException("Selected product does not exist");
        });

        Product product = productService.getProductByID(cartItem.getProduct().getId());
        if (product == null)
            throw new NotFoundException("Product with cartItem does not exist");

        if (product.getQuantity() < quantity)
            throw new RequestExceedingStockException();

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }
}

package com.onlinestore.BestShop.cart;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @BeanMapping(resultType = CartDto.class)
    @Mapping(target = "items", source = "cartItems")
    @Mapping(target = "totalPrice", expression = "java(src.getTotalPrice())")
    void updateFromCart(Cart src, @MappingTarget CartDto dest);

    @BeanMapping(resultType = CartItemDto.class)
    @Mapping(target = "totalPrice", expression = "java(src.getTotalPrice())")
    void updateFromCartItem(CartItem src, @MappingTarget CartItemDto dest);
}

package com.onlinestore.BestShop.order;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    void updateFromOrder(Order src, @MappingTarget OrderDto dest);
}

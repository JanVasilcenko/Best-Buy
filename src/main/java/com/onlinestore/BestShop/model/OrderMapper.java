package com.onlinestore.BestShop.model;

import com.onlinestore.BestShop.model.dto.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    void updateFromOrder(Order src, @MappingTarget OrderDto dest);
}

package com.onlinestore.BestShop.product;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void productPatchRequestToProduct(ProductPatchRequest source, @MappingTarget Product target);

    @BeanMapping(resultType = Product.class)
    void updateFromCreateDto(ProductCreateRequest src, @MappingTarget Product dest);

    @BeanMapping(resultType = ProductDto.class)
    void updateFromProductProductDto(Product src, @MappingTarget ProductDto dest);
}

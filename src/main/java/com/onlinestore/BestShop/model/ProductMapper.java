package com.onlinestore.BestShop.model;

import com.onlinestore.BestShop.model.dto.ProductCreateRequest;
import com.onlinestore.BestShop.model.dto.ProductPatchRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void productPatchRequestToProduct(ProductPatchRequest source, @MappingTarget Product target);

    @BeanMapping(resultType = Product.class)
    void updateFromCreateDto(ProductCreateRequest src, @MappingTarget Product dest);
}

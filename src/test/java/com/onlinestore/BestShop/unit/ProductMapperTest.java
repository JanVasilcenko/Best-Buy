package com.onlinestore.BestShop.unit;

import com.onlinestore.BestShop.product.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
public class ProductMapperTest {
    @Autowired
    private ProductMapper productMapper;

    @Test
    void mapsAllFieldsProductCreateRequestToProduct(){
        //Arrange
        ProductCreateRequest request = new ProductCreateRequest();
        request.setSku("RandomSku");
        request.setName("RandomName");
        request.setCurrency("RandomCurrency");
        request.setPrice(100);
        request.setDescription("RandomDescription");
        Product product = new Product();

        //Act
        productMapper.updateFromCreateDto(request, product);

        //Assert
        assertEquals("RandomSku", product.getSku());
        assertEquals("RandomName", product.getName());
        assertEquals(100, product.getPrice());
        assertEquals("RandomCurrency", product.getCurrency());
        assertEquals("RandomDescription", product.getDescription());
    }

    @Test
    void mapsAllFieldsProductToProductDto(){
        //Arrange
        Product product = new Product();
        product.setSku("RandomSku");
        product.setName("RandomName");
        product.setCurrency("RandomCurrency");
        product.setPrice(100);
        product.setDescription("RandomDescription");
        ProductDto productDto = new ProductDto();

        //Act
        productMapper.updateFromProductProductDto(product, productDto);

        //Assert
        assertEquals("RandomSku", productDto.getSku());
        assertEquals("RandomName", productDto.getName());
        assertEquals(100, productDto.getPrice());
        assertEquals("RandomCurrency", productDto.getCurrency());
        assertEquals("RandomDescription", productDto.getDescription());
    }

    @Test
    void mapsAllFieldsProductPatchRequestToProduct(){
        //Arrange
        ProductPatchRequest productPatchRequest = new ProductPatchRequest();
        productPatchRequest.setId("RandomId");
        productPatchRequest.setSku("RandomSku");
        productPatchRequest.setName("RandomName");
        productPatchRequest.setPrice(100);
        productPatchRequest.setDescription("RandomDescription");
        productPatchRequest.setCurrency("RandomCurrency");
        productPatchRequest.setQuantity(1);
        Product product = new Product();

        //Act
        productMapper.productPatchRequestToProduct(productPatchRequest, product);

        //Assert
        assertEquals("RandomId", product.getId());
        assertEquals("RandomSku", product.getSku());
        assertEquals("RandomName", product.getName());
        assertEquals(100, product.getPrice());
        assertEquals("RandomCurrency", product.getCurrency());
        assertEquals("RandomDescription", product.getDescription());
        assertEquals(1, product.getQuantity());
    }
}

package com.onlinestore.BestShop;

import com.onlinestore.BestShop.model.Product;
import com.onlinestore.BestShop.model.ProductMapper;
import com.onlinestore.BestShop.model.dto.ProductCreateRequest;
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
    void mapsAllFields(){
        ProductCreateRequest request = new ProductCreateRequest();
        request.setSku("RandomSku");
        request.setName("RandomName");
        request.setCurrency("RandomCurrency");
        request.setPrice(100);
        request.setDescription("RandomDescription");
        Product product = new Product();
        productMapper.updateFromCreateDto(request, product);
        assertEquals("RandomSku", product.getSku());
        assertEquals("RandomName", product.getName());
        assertEquals(100, product.getPrice());
        assertEquals("RandomCurrency", product.getCurrency());
        assertEquals("RandomDescription", product.getDescription());
    }
}

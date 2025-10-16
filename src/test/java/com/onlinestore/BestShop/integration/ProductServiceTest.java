package com.onlinestore.BestShop.integration;

import com.onlinestore.BestShop.exceptions.NotFoundException;
import com.onlinestore.BestShop.product.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PagedModel;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProductServiceTest {
    @Autowired private ProductService productService;
    @Autowired private ProductRepository productRepository;

    @Test
    void createProduct(){
        //Arrange
        ProductCreateRequest productCreateRequest = new ProductCreateRequest();
        productCreateRequest.setName("TestProduct");
        productCreateRequest.setDescription("A");
        productCreateRequest.setPrice(999);
        productCreateRequest.setCurrency("CZK");
        productCreateRequest.setQuantity(999);
        productCreateRequest.setSku("Something");

        //Act
        Product saved = productService.createProduct(productCreateRequest);

        //Assert
        assertNotNull(saved.getId());
        assertEquals("TestProduct", saved.getName());
        assertEquals("A", saved.getDescription());
        assertEquals(999, saved.getPrice());
        assertEquals(999, saved.getQuantity());
        assertEquals("CZK", saved.getCurrency());
        assertEquals("Something", saved.getSku());

        productRepository.findById(saved.getId()).orElseThrow();
    }

    @Test
    void getProductByID_foundAndMissing(){
        //Arrange
        Product product = product("A", "B", "CZK", 999, 999, "C");
        product = productRepository.save(product);

        //Act
        Product found = productService.getProductByID(product.getId());

        //Assert
        assertNotNull(found);
        assertEquals("A", found.getName());

        assertNull(productService.getProductByID("NONEXISTENT"));
    }

    private Product product(String name, String description, String currency, int price, int quantity, String sku){
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setCurrency(currency);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setSku(sku);
        return product;
    }

    @Test
    void updateProduct_selectedFields(){
        //Arrange
        Product product = product("A", "B", "CZK", 999,999,"C");
        product = productRepository.save(product);

        ProductPatchRequest patchRequest = new ProductPatchRequest();
        patchRequest.setId(product.getId());
        patchRequest.setPrice(1);
        patchRequest.setDescription("D");

        //Act
        productService.updateProduct(patchRequest);

        //Assert
        Product after = productRepository.findById(product.getId()).orElseThrow();
        assertEquals(1, after.getPrice());
        assertEquals("D", after.getDescription());
        assertEquals("A", after.getName());
        assertEquals("C", after.getSku());
        assertEquals(999, after.getQuantity());
    }

    @Test
    void updateProduct_whenMissing(){
        //Arrange
        ProductPatchRequest productPatchRequest = new ProductPatchRequest();
        productPatchRequest.setId("NONEXISTENT");
        productPatchRequest.setPrice(999);

        //Act + Assert
        assertThrows(NotFoundException.class, () -> productService.updateProduct(productPatchRequest));
    }

    @Test
    void getProducts_pages_sortedByName(){
        //Arrange
        productRepository.saveAll(List.of(
           product("Beta", "A", "CZK", 100, 1, "1"),
           product("Alpha", "B", "CZK", 200, 2, "2"),
           product("Zebra", "C", "CZK", 300, 3, "3")
        ));

        //Act
        PagedModel<Product> pageZero = productService.getProducts("a", 0, 2);
        PagedModel<Product> pageOne = productService.getProducts("a", 1, 2);

        //Assert
        assertNotNull(pageZero);
        assertEquals(2, pageZero.getContent().size());
        List<String> names = pageZero.getContent().stream().map(Product::getName).toList();
        assertEquals(List.of("Alpha", "Beta"), names);

        List<String> otherNames = pageOne.getContent().stream().map(Product::getName).toList();
        assertEquals(List.of("Zebra"), otherNames);
    }
}

package com.onlinestore.BestShop.controllers;

import com.onlinestore.BestShop.exceptions.NotFoundException;
import com.onlinestore.BestShop.model.Product;
import com.onlinestore.BestShop.model.dto.ProductCreateRequest;
import com.onlinestore.BestShop.model.dto.ProductPatchRequest;
import com.onlinestore.BestShop.services.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Tag(name = "Products")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductsController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PagedModel<Product>> getProducts(@RequestParam(value = "search") String search, @RequestParam(value = "page") Integer page, @RequestParam(value = "size") Integer size){
        return ResponseEntity.ok(productService.getProducts(search, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable(value = "id") String id){
        Product product = productService.getProductByID(id);
        if (product == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity createProduct(@RequestBody @Valid ProductCreateRequest productCreateRequest, UriComponentsBuilder uriBuilder){
        Product newlyCreatedProduct = productService.createProduct(productCreateRequest);
        URI uri = uriBuilder.path("/api/v1/products/{id}").buildAndExpand(newlyCreatedProduct.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PatchMapping("")
    public ResponseEntity updateProduct(@RequestBody @Valid ProductPatchRequest productPatchRequest){
        productService.updateProduct(productPatchRequest);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException notFoundException){
        return ResponseEntity.badRequest().body(Map.of("Error", notFoundException.getMessage()));
    }
}

package com.onlinestore.BestShop.product;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Tag(name = "Products")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductsController {
    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    public ResponseEntity<PagedModel<Product>> getProducts(@RequestParam(value = "search") String search, @RequestParam(value = "page") Integer page, @RequestParam(value = "size") Integer size){
        return ResponseEntity.ok(productService.getProducts(search, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable(value = "id") String id){
        Product product = productService.getProductByID(id);
        if (product == null)
            return ResponseEntity.notFound().build();
        ProductDto productDto = new ProductDto();
        productMapper.updateFromProductProductDto(product, productDto);

        return ResponseEntity.ok(productDto);
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
}

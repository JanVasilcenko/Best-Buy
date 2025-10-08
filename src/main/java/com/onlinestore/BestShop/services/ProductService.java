package com.onlinestore.BestShop.services;

import com.onlinestore.BestShop.exceptions.NotFoundException;
import com.onlinestore.BestShop.model.Product;
import com.onlinestore.BestShop.model.ProductMapper;
import com.onlinestore.BestShop.model.dto.ProductCreateRequest;
import com.onlinestore.BestShop.model.dto.ProductPatchRequest;
import com.onlinestore.BestShop.persistence.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productPatchMapper;

    public Product createProduct(ProductCreateRequest productCreateRequest){
        Product newProduct = new Product();
        productPatchMapper.updateFromCreateDto(productCreateRequest, newProduct);
        return productRepository.save(newProduct);
    }

    public Product getProductByID(String id){
        return productRepository.findById(id).orElse(null);
    }

    @Transactional
    public void updateProduct(ProductPatchRequest productPatchRequest){
        Product product = productRepository.findById(productPatchRequest.getId()).orElseThrow(
                () -> new NotFoundException("Product "+productPatchRequest.getId()+" not found"));

        productPatchMapper.productPatchRequestToProduct(productPatchRequest, product);

        productRepository.save(product);
    }

    public PagedModel<Product> getProducts(String name, int pageNumber, int size){
        PageRequest pageRequest = PageRequest.of(pageNumber, size, Sort.by("name").ascending());
        Page<Product> page = productRepository.findByNameLikeIgnoreCase(name, pageRequest);
        return new PagedModel<>(page);
    }
}

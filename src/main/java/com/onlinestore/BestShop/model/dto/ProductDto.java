package com.onlinestore.BestShop.model.dto;

import lombok.Data;

@Data
public class ProductDto {
    private String sku;
    private String name;
    private String description;
    private Integer price;
    private String currency;
    private Integer quantity;
}

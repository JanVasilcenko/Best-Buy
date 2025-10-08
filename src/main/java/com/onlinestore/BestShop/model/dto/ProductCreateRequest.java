package com.onlinestore.BestShop.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductCreateRequest {
    @NotBlank(message = "Stock Keeping Unit is required")
    private String sku;
    @NotBlank(message = "Name is required")
    private String name;
    @Size(max = 200)
    @NotBlank(message = "Description is required")
    private String description;
    @PositiveOrZero
    private Integer price;
    @NotBlank(message = "Currency is required")
    @Size(max = 3)
    private String currency;
    @Positive
    @NotNull(message = "Quantity is required")
    private Integer quantity;
}

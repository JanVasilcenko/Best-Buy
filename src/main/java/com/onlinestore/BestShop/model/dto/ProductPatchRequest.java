package com.onlinestore.BestShop.model.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductPatchRequest {
    @NotBlank(message = "ID is required")
    private String id;
    private String sku;
    private String name;
    private String description;
    private Integer price;
    private String currency;
    private Integer quantity;

    private static boolean hasText(String s){
        return s != null && !s.trim().isEmpty();
    }

    @AssertTrue(message = "Provide at least one field to update besides ID")
    public boolean isAnyOtherFieldProvided(){
        return (hasText(sku) || hasText(name) || hasText(description) || hasText(currency))
                || (price != null || quantity != null);
    }
}

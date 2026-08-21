package com.example.similarproducts.domain.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductNotFoundException extends RuntimeException {
    private final String productId;

    public ProductNotFoundException(String productId) {
        super("Product not found with id: " + productId);
        this.productId = productId;
    }
}
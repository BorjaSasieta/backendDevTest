package com.example.similarproducts.domain.exception;

import lombok.Getter;

@Getter
public class ProductNotFoundException extends RuntimeException {
    private final String productId;

    public ProductNotFoundException(String productId) {
        super("Product not found with id: " + productId);
        this.productId = productId;
    }

}

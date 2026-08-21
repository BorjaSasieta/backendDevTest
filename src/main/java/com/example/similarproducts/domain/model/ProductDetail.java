package com.example.similarproducts.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ProductDetail {

    @NotBlank(message = "Product id cannot be blank")
    private String id;

    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @NotNull(message = "Product price cannot be null")
    @PositiveOrZero(message = "Product price cannot be negative")
    private BigDecimal price;

    @NotNull(message = "Product availability cannot be null")
    private Boolean availability;
}
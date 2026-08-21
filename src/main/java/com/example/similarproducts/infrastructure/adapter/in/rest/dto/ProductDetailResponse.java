package com.example.similarproducts.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@AllArgsConstructor
public class ProductDetailResponse {
    @JsonProperty("id")
    @NotBlank
    private String id;

    @JsonProperty("name")
    @NotBlank
    private String name;

    @JsonProperty("price")
    @NotNull
    @PositiveOrZero
    private BigDecimal price;

    @JsonProperty("availability")
    @NotNull
    private Boolean availability;
}
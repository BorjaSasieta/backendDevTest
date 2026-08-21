package com.example.similarproducts.infrastructure.mapper;

import com.example.similarproducts.domain.model.ProductDetail;
import com.example.similarproducts.infrastructure.adapter.in.rest.dto.ProductDetailResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductDetailMapper {

    private final ObjectMapper objectMapper;

    public ProductDetailMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ProductDetailResponse toResponse(ProductDetail productDetail) {
        return objectMapper.convertValue(productDetail, ProductDetailResponse.class);
    }
}
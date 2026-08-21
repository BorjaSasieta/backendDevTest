package com.example.similarproducts.domain.port.in;

import com.example.similarproducts.domain.model.ProductDetail;
import reactor.core.publisher.Flux;

public interface GetSimilarProductsUseCase {
    Flux<ProductDetail> execute(String productId);
}
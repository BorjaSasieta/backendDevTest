package com.example.similarproducts.domain.port.out;

import com.example.similarproducts.domain.model.ProductDetail;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProductDetailProvider {
    Mono<ProductDetail> findById(String productId);
}
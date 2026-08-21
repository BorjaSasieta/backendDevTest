package com.example.similarproducts.domain.port.out;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface SimilarProductIdsProvider {
    Mono<List<String>> findSimilarIds(String productId);
}
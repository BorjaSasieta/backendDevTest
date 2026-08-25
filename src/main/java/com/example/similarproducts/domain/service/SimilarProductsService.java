package com.example.similarproducts.domain.service;

import com.example.similarproducts.domain.exception.ExternalServiceException;
import com.example.similarproducts.domain.exception.ProductNotFoundException;
import com.example.similarproducts.domain.model.ProductDetail;
import com.example.similarproducts.domain.port.in.GetSimilarProductsUseCase;
import com.example.similarproducts.domain.port.out.ProductDetailProvider;
import com.example.similarproducts.domain.port.out.SimilarProductIdsProvider;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashSet;
import java.util.List;

@Service
public class SimilarProductsService implements GetSimilarProductsUseCase {

    private static final Logger log = LoggerFactory.getLogger(SimilarProductsService.class);

    private final SimilarProductIdsProvider similarProductIdsProvider;
    private final ProductDetailProvider productDetailProvider;

    public SimilarProductsService(SimilarProductIdsProvider similarProductIdsProvider,
                                  ProductDetailProvider productDetailProvider) {
        this.similarProductIdsProvider = similarProductIdsProvider;
        this.productDetailProvider = productDetailProvider;
    }

    @Override
    @CircuitBreaker(name = "externalApi", fallbackMethod = "fallbackExecute")
    @Retry(name = "externalApi", fallbackMethod = "fallbackExecute")
    public Flux<ProductDetail> execute(String productId) {
        log.debug("Fetching similar products for productId={}", productId);
        return similarProductIdsProvider.findSimilarIds(productId)
                .doOnNext(ids -> log.debug("Found {} similar ids for productId={}", ids.size(), productId))
                .onErrorResume(this::handleSimilarIdsError)
                .flatMapIterable(this::removeDuplicatesPreservingOrder)
                .flatMap(this::fetchProductDetailSafe);
    }

    private Flux<ProductDetail> fallbackExecute(String productId, Throwable throwable) {
        log.warn("Resilience fallback triggered for productId={}: {}", productId, throwable.getMessage());
        return Flux.error(new ExternalServiceException("externalApi", "getSimilarProducts"));
    }

    private Mono<List<String>> handleSimilarIdsError(Throwable throwable) {
        if (throwable instanceof ProductNotFoundException) {
            log.debug("Product not found during similar ids lookup: {}", throwable.getMessage());
            return Mono.empty();
        }
        if (throwable instanceof ExternalServiceException) {
            log.warn("External service error during similar ids lookup: {}", throwable.getMessage());
            return Mono.error(throwable);
        }
        log.error("Unexpected error during similar ids lookup", throwable);
        return Mono.empty();
    }

    private List<String> removeDuplicatesPreservingOrder(List<String> ids) {
        return new LinkedHashSet<>(ids)
                .stream()
                .toList();
    }

    private Mono<ProductDetail> fetchProductDetailSafe(String productId) {
        return productDetailProvider.findById(productId)
                .doOnNext(detail -> log.debug("Fetched detail for productId={}", productId))
                .onErrorResume(throwable -> {
                    if (throwable instanceof ProductNotFoundException) {
                        log.debug("Product detail not found for productId={}", productId);
                        return Mono.empty();
                    }
                    if (throwable instanceof ExternalServiceException) {
                        log.warn("External service error fetching detail for productId={}: {}",
                                productId, throwable.getMessage());
                        return Mono.error(throwable);
                    }
                    log.error("Unexpected error fetching detail for productId={}", productId, throwable);
                    return Mono.empty();
                });
    }
}

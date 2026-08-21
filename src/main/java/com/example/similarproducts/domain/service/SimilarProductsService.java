package com.example.similarproducts.domain.service;

import com.example.similarproducts.domain.exception.ExternalServiceException;
import com.example.similarproducts.domain.exception.ProductNotFoundException;
import com.example.similarproducts.domain.model.ProductDetail;
import com.example.similarproducts.domain.port.in.GetSimilarProductsUseCase;
import com.example.similarproducts.domain.port.out.ProductDetailProvider;
import com.example.similarproducts.domain.port.out.SimilarProductIdsProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashSet;
import java.util.List;

@Service
public class SimilarProductsService implements GetSimilarProductsUseCase {

    private final SimilarProductIdsProvider similarProductIdsProvider;
    private final ProductDetailProvider productDetailProvider;

    public SimilarProductsService(SimilarProductIdsProvider similarProductIdsProvider,
                                  ProductDetailProvider productDetailProvider) {
        this.similarProductIdsProvider = similarProductIdsProvider;
        this.productDetailProvider = productDetailProvider;
    }

    @Override
    public Flux<ProductDetail> execute(String productId) {
        return similarProductIdsProvider.findSimilarIds(productId)
                .onErrorResume(this::handleSimilarIdsError)
                .flatMapIterable(this::removeDuplicatesPreservingOrder)
                .flatMap(this::fetchProductDetailSafe);
    }

    private Mono<List<String>> handleSimilarIdsError(Throwable throwable) {
        if (throwable instanceof ProductNotFoundException) {
            return Mono.empty();
        }
        if (throwable instanceof ExternalServiceException) {
            return Mono.error(throwable);
        }
        return Mono.empty();
    }

    private List<String> removeDuplicatesPreservingOrder(List<String> ids) {
        return new LinkedHashSet<>(ids)
                .stream()
                .toList();
    }

    private Mono<ProductDetail> fetchProductDetailSafe(String productId) {
        return productDetailProvider.findById(productId)
                .onErrorResume(throwable -> {
                    if (throwable instanceof ProductNotFoundException) {
                        return Mono.empty();
                    }
                    if (throwable instanceof ExternalServiceException) {
                        return Mono.error(throwable);
                    }
                    return Mono.empty();
                });
    }
}
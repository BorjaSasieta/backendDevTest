package com.example.similarproducts.infrastructure.adapter.in.rest;

import com.example.similarproducts.domain.port.in.GetSimilarProductsUseCase;
import com.example.similarproducts.infrastructure.adapter.in.rest.dto.ProductDetailResponse;
import com.example.similarproducts.infrastructure.mapper.ProductDetailMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/product")
@Validated
public class SimilarProductsController {

    private final GetSimilarProductsUseCase getSimilarProductsUseCase;
    private final ProductDetailMapper productDetailMapper;

    public SimilarProductsController(GetSimilarProductsUseCase getSimilarProductsUseCase,
                                     ProductDetailMapper productDetailMapper) {
        this.getSimilarProductsUseCase = getSimilarProductsUseCase;
        this.productDetailMapper = productDetailMapper;
    }

    @GetMapping(value = "/{productId}/similar", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Flux<ProductDetailResponse>>> getSimilarProducts(
            @PathVariable @NotBlank @Size(min = 1, max = 50) String productId) {
        return getSimilarProductsUseCase.execute(productId)
                .collectList()
                .map(productDetails -> {
                    Flux<ProductDetailResponse> response = Flux.fromIterable(productDetails)
                            .map(productDetailMapper::toResponse);
                    return ResponseEntity.ok(response);
                });
    }
}
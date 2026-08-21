package com.example.similarproducts.infrastructure.adapter.out.external;

import com.example.similarproducts.domain.exception.ExternalServiceException;
import com.example.similarproducts.domain.exception.ProductNotFoundException;
import com.example.similarproducts.domain.model.ProductDetail;
import com.example.similarproducts.domain.port.out.ProductDetailProvider;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ProductDetailAdapter implements ProductDetailProvider {

    private final WebClient webClient;

    public ProductDetailAdapter(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<ProductDetail> findById(String productId) {
        return webClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .onStatus(status -> status.value() == 404, response ->
                        Mono.error(new ProductNotFoundException(productId)))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new ExternalServiceException("ProductDetailApi", "findById")))
                .bodyToMono(ProductDetail.class);
    }
}
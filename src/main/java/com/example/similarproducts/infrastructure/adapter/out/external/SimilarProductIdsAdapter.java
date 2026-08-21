package com.example.similarproducts.infrastructure.adapter.out.external;

import org.springframework.core.ParameterizedTypeReference;

import com.example.similarproducts.domain.exception.ExternalServiceException;
import com.example.similarproducts.domain.exception.ProductNotFoundException;
import com.example.similarproducts.domain.port.out.SimilarProductIdsProvider;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SimilarProductIdsAdapter implements SimilarProductIdsProvider {

    private final WebClient webClient;

    public SimilarProductIdsAdapter(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<List<String>> findSimilarIds(String productId) {
        return webClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .onStatus(status -> status.value() == 404, response ->
                        Mono.error(new ProductNotFoundException(productId)))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new ExternalServiceException("SimilarIdsApi", "findSimilarIds")))
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }
}
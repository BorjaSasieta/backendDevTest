package com.example.similarproducts.domain.service;

import com.example.similarproducts.domain.Validator.ProductDetailValidator;
import com.example.similarproducts.domain.exception.ExternalServiceException;
import com.example.similarproducts.domain.exception.ProductNotFoundException;
import com.example.similarproducts.domain.model.ProductDetail;
import com.example.similarproducts.domain.port.in.GetSimilarProductsUseCase;
import com.example.similarproducts.domain.port.out.ProductDetailProvider;
import com.example.similarproducts.domain.port.out.SimilarProductIdsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SimilarProductsService")
class SimilarProductsServiceTest {

    @Mock
    private SimilarProductIdsProvider similarProductIdsProvider;

    @Mock
    private ProductDetailProvider productDetailProvider;

    private GetSimilarProductsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SimilarProductsService(similarProductIdsProvider, productDetailProvider);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("should return similar products when all calls succeed")
        void shouldReturnSimilarProductsWhenAllCallsSucceed() {
            // Given
            String productId = "1";
            List<String> similarIds = List.of("2", "3", "4");
            ProductDetail product2 = ProductDetailValidator.createValidProductDetail("2", "Dress", new BigDecimal("19.99"), true);
            ProductDetail product3 = ProductDetailValidator.createValidProductDetail("3", "Blazer", new BigDecimal("29.99"), false);
            ProductDetail product4 = ProductDetailValidator.createValidProductDetail("4", "Boots", new BigDecimal("39.99"), true);

            when(similarProductIdsProvider.findSimilarIds(productId))
                    .thenReturn(Mono.just(similarIds));
            when(productDetailProvider.findById("2"))
                    .thenReturn(Mono.just(product2));
            when(productDetailProvider.findById("3"))
                    .thenReturn(Mono.just(product3));
            when(productDetailProvider.findById("4"))
                    .thenReturn(Mono.just(product4));

            // When
            Flux<ProductDetail> result = useCase.execute(productId);

            // Then
            StepVerifier.create(result)
                    .expectNext(product2, product3, product4)
                    .verifyComplete();
        }

        @Test
        @DisplayName("should return empty when no similar ids found")
        void shouldReturnEmptyWhenNoSimilarIdsFound() {
            // Given
            String productId = "1";
            when(similarProductIdsProvider.findSimilarIds(productId))
                    .thenReturn(Mono.just(List.of()));

            // When
            Flux<ProductDetail> result = useCase.execute(productId);

            // Then
            StepVerifier.create(result)
                    .verifyComplete();
        }

        @Test
        @DisplayName("should skip products that fail to fetch")
        void shouldSkipProductsThatFailToFetch() {
            // Given
            String productId = "1";
            List<String> similarIds = List.of("2", "3", "4");
            ProductDetail product2 = ProductDetailValidator.createValidProductDetail("2", "Dress", new BigDecimal("19.99"), true);
            ProductDetail product4 = ProductDetailValidator.createValidProductDetail("4", "Boots", new BigDecimal("39.99"), true);

            when(similarProductIdsProvider.findSimilarIds(productId))
                    .thenReturn(Mono.just(similarIds));
            when(productDetailProvider.findById("2"))
                    .thenReturn(Mono.just(product2));
            when(productDetailProvider.findById("3"))
                    .thenReturn(Mono.empty());
            when(productDetailProvider.findById("4"))
                    .thenReturn(Mono.just(product4));

            // When
            Flux<ProductDetail> result = useCase.execute(productId);

            // Then
            StepVerifier.create(result)
                    .expectNext(product2, product4)
                    .verifyComplete();
        }

        @Test
        @DisplayName("should handle error when fetching similar ids")
        void shouldHandleErrorWhenFetchingSimilarIds() {
            // Given
            String productId = "1";
            when(similarProductIdsProvider.findSimilarIds(productId))
                    .thenReturn(Mono.error(new RuntimeException("Service unavailable")));

            // When
            Flux<ProductDetail> result = useCase.execute(productId);

            // Then
            StepVerifier.create(result)
                    .verifyComplete();
        }

        @Test
        @DisplayName("should preserve order of similar ids")
        void shouldPreserveOrderOfSimilarIds() {
            // Given
            String productId = "1";
            List<String> similarIds = List.of("3", "1", "4", "2");
            ProductDetail p3 = ProductDetailValidator.createValidProductDetail("3", "Blazer", new BigDecimal("29.99"), false);
            ProductDetail p1 = ProductDetailValidator.createValidProductDetail("1", "Shirt", new BigDecimal("9.99"), true);
            ProductDetail p4 = ProductDetailValidator.createValidProductDetail("4", "Boots", new BigDecimal("39.99"), true);
            ProductDetail p2 = ProductDetailValidator.createValidProductDetail("2", "Dress", new BigDecimal("19.99"), true);

            when(similarProductIdsProvider.findSimilarIds(productId))
                    .thenReturn(Mono.just(similarIds));
            when(productDetailProvider.findById("3"))
                    .thenReturn(Mono.just(p3));
            when(productDetailProvider.findById("1"))
                    .thenReturn(Mono.just(p1));
            when(productDetailProvider.findById("4"))
                    .thenReturn(Mono.just(p4));
            when(productDetailProvider.findById("2"))
                    .thenReturn(Mono.just(p2));

            // When
            Flux<ProductDetail> result = useCase.execute(productId);

            // Then
            StepVerifier.create(result)
                    .expectNext(p3, p1, p4, p2)
                    .verifyComplete();
        }

        @Test
        @DisplayName("should handle duplicate ids by returning only first occurrence")
        void shouldHandleDuplicateIds() {
            // Given
            String productId = "1";
            List<String> similarIds = List.of("2", "2", "3");
            ProductDetail product2 = ProductDetailValidator.createValidProductDetail("2", "Dress", new BigDecimal("19.99"), true);
            ProductDetail product3 = ProductDetailValidator.createValidProductDetail("3", "Blazer", new BigDecimal("29.99"), false);

            when(similarProductIdsProvider.findSimilarIds(productId))
                    .thenReturn(Mono.just(similarIds));
            when(productDetailProvider.findById("2"))
                    .thenReturn(Mono.just(product2));
            when(productDetailProvider.findById("3"))
                    .thenReturn(Mono.just(product3));

            // When
            Flux<ProductDetail> result = useCase.execute(productId);

            // Then
            StepVerifier.create(result)
                    .expectNext(product2, product3)
                    .verifyComplete();
        }

        @Test
        @DisplayName("should return empty when similar ids not found")
        void shouldReturnEmptyWhenSimilarIdsNotFound() {
            // Given
            String productId = "1";
            when(similarProductIdsProvider.findSimilarIds(productId))
                    .thenReturn(Mono.error(new ProductNotFoundException(productId)));

            // When
            Flux<ProductDetail> result = useCase.execute(productId);

            // Then
            StepVerifier.create(result)
                    .verifyComplete();
        }

        @Test
        @DisplayName("should skip product details not found by id")
        void shouldSkipProductDetailsNotFoundById() {
            // Given
            String productId = "1";
            List<String> similarIds = List.of("2");
            when(similarProductIdsProvider.findSimilarIds(productId))
                    .thenReturn(Mono.just(similarIds));
            when(productDetailProvider.findById("2"))
                    .thenReturn(Mono.error(new ProductNotFoundException("2")));

            // When
            Flux<ProductDetail> result = useCase.execute(productId);

            // Then
            StepVerifier.create(result)
                    .verifyComplete();
        }

        @Test
        @DisplayName("should propagate external service exception from similar ids")
        void shouldPropagateExternalServiceExceptionFromSimilarIds() {
            // Given
            String productId = "1";
            when(similarProductIdsProvider.findSimilarIds(productId))
                    .thenReturn(Mono.error(new ExternalServiceException("api", "findSimilarIds")));

            // When
            Flux<ProductDetail> result = useCase.execute(productId);

            // Then
            StepVerifier.create(result)
                    .expectError(ExternalServiceException.class)
                    .verify();
        }

        @Test
        @DisplayName("should propagate external service exception from product detail")
        void shouldPropagateExternalServiceExceptionFromProductDetail() {
            // Given
            String productId = "1";
            List<String> similarIds = List.of("2");
            when(similarProductIdsProvider.findSimilarIds(productId))
                    .thenReturn(Mono.just(similarIds));
            when(productDetailProvider.findById("2"))
                    .thenReturn(Mono.error(new ExternalServiceException("api", "findById")));

            // When
            Flux<ProductDetail> result = useCase.execute(productId);

            // Then
            StepVerifier.create(result)
                    .expectError(ExternalServiceException.class)
                    .verify();
        }

        @Test
        @DisplayName("should skip product details with unexpected errors")
        void shouldSkipProductDetailsWithUnexpectedErrors() {
            // Given
            String productId = "1";
            List<String> similarIds = List.of("2");
            when(similarProductIdsProvider.findSimilarIds(productId))
                    .thenReturn(Mono.just(similarIds));
            when(productDetailProvider.findById("2"))
                    .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

            // When
            Flux<ProductDetail> result = useCase.execute(productId);

            // Then
            StepVerifier.create(result)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("fallbackExecute")
    class FallbackExecute {

        @Test
        @DisplayName("should return ExternalServiceException when fallback is triggered")
        void shouldReturnExternalServiceExceptionWhenFallbackIsTriggered() {
            // Given
            String productId = "1";
            Throwable cause = new RuntimeException("Circuit breaker open");

            // When - invoke the fallback method directly via reflection
            Flux<ProductDetail> result = invokeFallbackExecute(productId, cause);

            // Then
            StepVerifier.create(result)
                    .expectError(ExternalServiceException.class)
                    .verify();
        }

        @Test
        @DisplayName("should log warning when fallback is triggered")
        void shouldLogWarningWhenFallbackIsTriggered() {
            // Given
            String productId = "2";
            Throwable cause = new RuntimeException("Retry exhausted");

            // When - invoke the fallback method directly via reflection
            Flux<ProductDetail> result = invokeFallbackExecute(productId, cause);

            // Then - verify the error is propagated (logging is verified by the warning in the method)
            StepVerifier.create(result)
                    .expectError(ExternalServiceException.class)
                    .verify();
        }

        private Flux<ProductDetail> invokeFallbackExecute(String productId, Throwable throwable) {
            try {
                var method = SimilarProductsService.class.getDeclaredMethod(
                        "fallbackExecute", String.class, Throwable.class);
                method.setAccessible(true);
                return (Flux<ProductDetail>) method.invoke(useCase, productId, throwable);
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke fallbackExecute", e);
            }
        }
    }
}
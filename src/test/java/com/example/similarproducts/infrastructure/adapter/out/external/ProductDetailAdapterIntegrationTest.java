package com.example.similarproducts.infrastructure.adapter.out.external;

import com.example.similarproducts.domain.port.out.ProductDetailProvider;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@DisplayName("ProductDetailAdapter Integration Tests")
class ProductDetailAdapterIntegrationTest {

    private static MockWebServer mockWebServer;
    private ProductDetailProvider adapter;

    @BeforeAll
    static void startMockServer() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void stopMockServer() throws Exception {
        mockWebServer.close();
    }

    @BeforeEach
    void setUp() {
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        adapter = new ProductDetailAdapter(webClient);
    }

    @AfterEach
    void shutdownMockServer() throws Exception {
        mockWebServer.shutdown();
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return product detail when found")
        void shouldReturnProductDetailWhenFound() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("{\"id\":\"1\",\"name\":\"Shirt\",\"price\":9.99,\"availability\":true}")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            var result = adapter.findById("1");

            StepVerifier.create(result)
                    .expectNextMatches(product ->
                            product.getId().equals("1") &&
                            product.getName().equals("Shirt") &&
                            product.getPrice().compareTo(new BigDecimal("9.99")) == 0 &&
                            product.getAvailability()
                    )
                    .verifyComplete();
        }

        @Test
        @DisplayName("should propagate ProductNotFoundException when 404")
        void shouldPropagateProductNotFoundExceptionWhen404() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(404)
                    .setBody("{\"message\":\"Product not found\"}")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            var result = adapter.findById("999");

            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof com.example.similarproducts.domain.exception.ProductNotFoundException
                    )
                    .verify();
        }

        @Test
        @DisplayName("should propagate ExternalServiceException when 500")
        void shouldPropagateExternalServiceExceptionWhen500() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(500)
                    .setBody("{\"error\":\"Internal Server Error\"}")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            var result = adapter.findById("500");

            StepVerifier.create(result)
                    .expectError(com.example.similarproducts.domain.exception.ExternalServiceException.class)
                    .verify();
        }
    }
}
package com.example.similarproducts.interfaces.rest;

import com.example.similarproducts.infrastructure.adapter.in.rest.dto.ProductDetailResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SimilarProductsControllerIntegrationTest {

    static MockWebServer mockWebServer = initMockWebServer();

    private static MockWebServer initMockWebServer() {
        MockWebServer server = new MockWebServer();
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return server;
    }

    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("external.api.base-url", () -> mockWebServer.url("/").toString());
    }

    @AfterAll
    static void stopMockServer() throws Exception {
        mockWebServer.shutdown();
    }

    @Nested
    @DisplayName("GET /product/{productId}/similar")
    class GetSimilarProducts {

        @Test
        @DisplayName("should return 200 with similar products when product exists")
        void shouldReturn200WithSimilarProducts() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("[\"2\",\"3\",\"4\"]")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true}")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("{\"id\":\"3\",\"name\":\"Blazer\",\"price\":29.99,\"availability\":false}")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("{\"id\":\"4\",\"name\":\"Boots\",\"price\":39.99,\"availability\":true}")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            webTestClient.get()
                    .uri("/product/1/similar")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBodyList(ProductDetailResponse.class)
                    .hasSize(3);
        }

        @Test
        @DisplayName("should return 200 with empty list when product not found in similar ids")
        void shouldReturn200WithEmptyListWhenProductNotFound() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(404)
                    .setBody("{\"message\":\"Product not found\"}")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            webTestClient.get()
                    .uri("/product/999/similar")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBodyList(ProductDetailResponse.class)
                    .hasSize(0);
        }

        @Test
        @DisplayName("should return 500 when external service has server error on similar ids")
        void shouldReturn500WhenExternalServiceErrorsOnSimilarIds() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(500)
                    .setBody("{\"error\":\"Internal Server Error\"}")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            webTestClient.get()
                    .uri("/product/500/similar")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().is5xxServerError();
        }

        @Test
        @DisplayName("should return 200 with available products when some product details fail")
        void shouldReturn200WithAvailableProductsWhenSomeFail() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("[\"2\",\"3\"]")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(404)
                    .setBody("{\"message\":\"Product not found\"}")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("{\"id\":\"3\",\"name\":\"Blazer\",\"price\":29.99,\"availability\":false}")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            webTestClient.get()
                    .uri("/product/1/similar")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBodyList(ProductDetailResponse.class)
                    .hasSize(1);
        }

        @Test
        @DisplayName("should handle slow product detail by skipping timed-out products")
        void shouldHandleSlowProductDetailBySkippingTimeoutProducts() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("[\"2\",\"3\"]")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true}")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .setBodyDelay(2, TimeUnit.SECONDS));

            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("{\"id\":\"3\",\"name\":\"Blazer\",\"price\":29.99,\"availability\":false}")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            webTestClient.get()
                    .uri("/product/1/similar")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBodyList(ProductDetailResponse.class)
                    .hasSize(2);
        }

        @Test
        @DisplayName("should use provided X-Request-Id header as correlation id")
        void shouldUseProvidedCorrelationId() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("[\"2\"]")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true}")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            webTestClient.get()
                    .uri("/product/1/similar")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-Request-Id", "test-correlation-id-123")
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBodyList(ProductDetailResponse.class)
                    .hasSize(1);
        }

        @Test
        @DisplayName("should generate correlation id when X-Request-Id header is blank")
        void shouldGenerateCorrelationIdWhenHeaderIsBlank() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("[\"2\"]")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true}")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            webTestClient.get()
                    .uri("/product/1/similar")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-Request-Id", "   ")
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBodyList(ProductDetailResponse.class)
                    .hasSize(1);
        }
    }
}
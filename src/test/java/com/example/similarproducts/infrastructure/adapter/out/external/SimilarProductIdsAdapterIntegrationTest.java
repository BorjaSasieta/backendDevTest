package com.example.similarproducts.infrastructure.adapter.out.external;

import com.example.similarproducts.domain.port.out.SimilarProductIdsProvider;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.util.List;

@DisplayName("SimilarProductIdsAdapter Integration Tests")
class SimilarProductIdsAdapterIntegrationTest {

    private static MockWebServer mockWebServer;
    private SimilarProductIdsProvider adapter;

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
        adapter = new SimilarProductIdsAdapter(webClient);
    }

    @AfterEach
    void shutdownMockServer() throws Exception {
        mockWebServer.shutdown();
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @Nested
    @DisplayName("findSimilarIds")
    class FindSimilarIds {

        @Test
        @DisplayName("should return list of similar product ids")
        void shouldReturnListOfSimilarProductIds() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("[\"2\",\"3\",\"4\"]")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            var result = adapter.findSimilarIds("1");

            StepVerifier.create(result)
                    .expectNextMatches(list ->
                            list.size() == 3 &&
                            list.get(0).equals("2") &&
                            list.get(1).equals("3") &&
                            list.get(2).equals("4")
                    )
                    .verifyComplete();
        }

        @Test
        @DisplayName("should return empty list when no similar ids")
        void shouldReturnEmptyListWhenNoSimilarIds() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("[]")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            var result = adapter.findSimilarIds("1");

            StepVerifier.create(result)
                    .expectNextMatches(List::isEmpty)
                    .verifyComplete();
        }

        @Test
        @DisplayName("should propagate ProductNotFoundException when 404")
        void shouldPropagateProductNotFoundExceptionWhen404() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(404)
                    .setBody("{\"message\":\"Product not found\"}")
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

            var result = adapter.findSimilarIds("999");

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

            var result = adapter.findSimilarIds("500");

            StepVerifier.create(result)
                    .expectError(com.example.similarproducts.domain.exception.ExternalServiceException.class)
                    .verify();
        }
    }
}
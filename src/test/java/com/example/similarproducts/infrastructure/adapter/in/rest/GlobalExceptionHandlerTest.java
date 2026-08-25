package com.example.similarproducts.infrastructure.adapter.in.rest;

import com.example.similarproducts.domain.exception.ExternalServiceException;
import com.example.similarproducts.domain.exception.ProductNotFoundException;
import com.example.similarproducts.infrastructure.adapter.in.rest.dto.ErrorResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        MockServerHttpRequest request = MockServerHttpRequest.get("/product/1/similar").build();
        exchange = MockServerWebExchange.from(request);
    }

    @Test
    void shouldReturn404WhenProductNotFound() {
        ProductNotFoundException ex = new ProductNotFoundException("1");

        ResponseEntity<ErrorResponse> response = handler.handleProductNotFoundException(ex, exchange);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessage()).contains("1");
        assertThat(response.getBody().getPath()).isEqualTo("/product/1/similar");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void shouldReturn502WhenExternalServiceFails() {
        ExternalServiceException ex = new ExternalServiceException("simulado", "getSimilarIds");

        ResponseEntity<ErrorResponse> response = handler.handleExternalServiceException(ex, exchange);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(502);
        assertThat(response.getBody().getError()).isEqualTo("Bad Gateway");
        assertThat(response.getBody().getMessage()).contains("simulado");
    }

    @Test
    void shouldReturn503WhenCircuitBreakerOpen() {
        CircuitBreaker circuitBreaker = CircuitBreaker.of("externalApi", CircuitBreakerConfig.custom().build());
        CallNotPermittedException ex = CallNotPermittedException.createCallNotPermittedException(circuitBreaker);

        ResponseEntity<ErrorResponse> response = handler.handleCircuitBreakerOpen(ex, exchange);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(503);
        assertThat(response.getBody().getError()).isEqualTo("Service Unavailable");
        assertThat(response.getBody().getMessage()).contains("Circuit breaker");
    }

    @Test
    void shouldReturn400WhenConstraintViolation() {
        ConstraintViolationException ex = new ConstraintViolationException("productId must not be blank", null);

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex, exchange);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
    }

    @Test
    void shouldReturn500OnGenericException() {
        Exception ex = new RuntimeException("unexpected failure");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, exchange);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage()).isEqualTo("Internal server error");
    }
}

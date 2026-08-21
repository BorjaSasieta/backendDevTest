package com.example.similarproducts.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Domain Exceptions")
class DomainExceptionTest {

    @Nested
    @DisplayName("ProductNotFoundException")
    class ProductNotFoundExceptionTest {

        @Test
        @DisplayName("should store productId and produce message")
        void shouldStoreProductIdAndProduceMessage() {
            ProductNotFoundException ex = new ProductNotFoundException("123");

            assertThat(ex.getProductId()).isEqualTo("123");
            assertThat(ex.getMessage()).contains("123");
        }
    }

    @Nested
    @DisplayName("ExternalServiceException")
    class ExternalServiceExceptionTest {

        @Test
        @DisplayName("should store serviceName and operation")
        void shouldStoreServiceNameAndOperation() {
            ExternalServiceException ex = new ExternalServiceException("MyService", "doStuff");

            assertThat(ex.getServiceName()).isEqualTo("MyService");
            assertThat(ex.getOperation()).isEqualTo("doStuff");
            assertThat(ex.getMessage()).contains("MyService", "doStuff");
        }
    }
}

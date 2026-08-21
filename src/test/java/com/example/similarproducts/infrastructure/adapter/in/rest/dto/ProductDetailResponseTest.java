package com.example.similarproducts.infrastructure.adapter.in.rest.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductDetailResponse")
class ProductDetailResponseTest {

    @Nested
    @DisplayName("Creation and Getters")
    class CreationAndGetters {

        @Test
        @DisplayName("should create via all-args constructor and access getters")
        void shouldCreateViaAllArgsConstructorAndGetters() {
            ProductDetailResponse response = new ProductDetailResponse(
                    "1", "Shirt", new BigDecimal("19.99"), true);

            assertThat(response.getId()).isEqualTo("1");
            assertThat(response.getName()).isEqualTo("Shirt");
            assertThat(response.getPrice()).isEqualByComparingTo("19.99");
            assertThat(response.getAvailability()).isTrue();
        }

        @Test
        @DisplayName("should create via builder")
        void shouldCreateViaBuilder() {
            ProductDetailResponse response = ProductDetailResponse.builder()
                    .id("2")
                    .name("Dress")
                    .price(new BigDecimal("29.99"))
                    .availability(false)
                    .build();

            assertThat(response.getId()).isEqualTo("2");
            assertThat(response.getName()).isEqualTo("Dress");
            assertThat(response.getPrice()).isEqualByComparingTo("29.99");
            assertThat(response.getAvailability()).isFalse();
        }

        @Test
        @DisplayName("should create via no-args constructor and set fields")
        void shouldCreateViaNoArgsConstructorAndSetFields() {
            ProductDetailResponse response = new ProductDetailResponse();
            response.setId("3");
            response.setName("Blazer");
            response.setPrice(new BigDecimal("49.99"));
            response.setAvailability(true);

            assertThat(response.getId()).isEqualTo("3");
            assertThat(response.getName()).isEqualTo("Blazer");
            assertThat(response.getPrice()).isEqualByComparingTo("49.99");
            assertThat(response.getAvailability()).isTrue();
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("should be equal when same reference")
        void shouldBeEqualWhenSameReference() {
            ProductDetailResponse response = ProductDetailResponse.builder()
                    .id("1").name("Shirt").price(new BigDecimal("19.99")).availability(true)
                    .build();

            assertThat(response).isEqualTo(response);
        }

        @Test
        @DisplayName("should be equal when all fields match")
        void shouldBeEqualWhenAllFieldsMatch() {
            ProductDetailResponse r1 = ProductDetailResponse.builder()
                    .id("1").name("Shirt").price(new BigDecimal("19.99")).availability(true)
                    .build();
            ProductDetailResponse r2 = ProductDetailResponse.builder()
                    .id("1").name("Shirt").price(new BigDecimal("19.99")).availability(true)
                    .build();

            assertThat(r1).isEqualTo(r2);
            assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when id differs")
        void shouldNotBeEqualWhenIdDiffers() {
            ProductDetailResponse r1 = ProductDetailResponse.builder()
                    .id("1").name("Shirt").price(new BigDecimal("19.99")).availability(true)
                    .build();
            ProductDetailResponse r2 = ProductDetailResponse.builder()
                    .id("2").name("Shirt").price(new BigDecimal("19.99")).availability(true)
                    .build();

            assertThat(r1).isNotEqualTo(r2);
        }

        @Test
        @DisplayName("should not be equal to null")
        void shouldNotBeEqualToNull() {
            ProductDetailResponse r1 = ProductDetailResponse.builder()
                    .id("1").name("Shirt").price(new BigDecimal("19.99")).availability(true)
                    .build();

            assertThat(r1).isNotEqualTo(null);
        }

        @Test
        @DisplayName("should be equal when all fields are null")
        void shouldBeEqualWhenAllFieldsAreNull() {
            ProductDetailResponse r1 = ProductDetailResponse.builder().build();
            ProductDetailResponse r2 = ProductDetailResponse.builder().build();

            assertThat(r1).isEqualTo(r2);
            assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when this.id is null but other.id is not")
        void shouldNotBeEqualWhenThisIdIsNullButOtherIdIsNotNull() {
            ProductDetailResponse r1 = ProductDetailResponse.builder()
                    .name("Shirt").price(new BigDecimal("19.99")).availability(true).build();
            ProductDetailResponse r2 = ProductDetailResponse.builder()
                    .id("1").name("Shirt").price(new BigDecimal("19.99")).availability(true).build();

            assertThat(r1).isNotEqualTo(r2);
        }

        @Test
        @DisplayName("should not be equal when this.name is null but other.name is not")
        void shouldNotBeEqualWhenThisNameIsNullButOtherNameIsNotNull() {
            ProductDetailResponse r1 = ProductDetailResponse.builder()
                    .id("1").price(new BigDecimal("19.99")).availability(true).build();
            ProductDetailResponse r2 = ProductDetailResponse.builder()
                    .id("1").name("Shirt").price(new BigDecimal("19.99")).availability(true).build();

            assertThat(r1).isNotEqualTo(r2);
        }

        @Test
        @DisplayName("should not be equal when this.price is null but other.price is not")
        void shouldNotBeEqualWhenThisPriceIsNullButOtherPriceIsNotNull() {
            ProductDetailResponse r1 = ProductDetailResponse.builder()
                    .id("1").name("Shirt").availability(true).build();
            ProductDetailResponse r2 = ProductDetailResponse.builder()
                    .id("1").name("Shirt").price(new BigDecimal("19.99")).availability(true).build();

            assertThat(r1).isNotEqualTo(r2);
        }

        @Test
        @DisplayName("should not be equal when this.availability is null but other.availability is not")
        void shouldNotBeEqualWhenThisAvailabilityIsNullButOtherAvailabilityIsNotNull() {
            ProductDetailResponse r1 = ProductDetailResponse.builder()
                    .id("1").name("Shirt").price(new BigDecimal("19.99")).build();
            ProductDetailResponse r2 = ProductDetailResponse.builder()
                    .id("1").name("Shirt").price(new BigDecimal("19.99")).availability(true).build();

            assertThat(r1).isNotEqualTo(r2);
        }

        @Test
        @DisplayName("should not be equal when this.id is not null but other.id is null")
        void shouldNotBeEqualWhenThisIdIsNotNullButOtherIdIsNull() {
            ProductDetailResponse r1 = ProductDetailResponse.builder()
                    .id("1").name("Shirt").price(new BigDecimal("19.99")).availability(true).build();
            ProductDetailResponse r2 = ProductDetailResponse.builder()
                    .name("Shirt").price(new BigDecimal("19.99")).availability(true).build();

            assertThat(r1).isNotEqualTo(r2);
        }

        @Test
        @DisplayName("should not be equal when this.name is not null but other.name is null")
        void shouldNotBeEqualWhenThisNameIsNotNullButOtherNameIsNull() {
            ProductDetailResponse r1 = ProductDetailResponse.builder()
                    .id("1").name("Shirt").price(new BigDecimal("19.99")).availability(true).build();
            ProductDetailResponse r2 = ProductDetailResponse.builder()
                    .id("1").price(new BigDecimal("19.99")).availability(true).build();

            assertThat(r1).isNotEqualTo(r2);
        }

        @Test
        @DisplayName("should not be equal when this.price is not null but other.price is null")
        void shouldNotBeEqualWhenThisPriceIsNotNullButOtherPriceIsNull() {
            ProductDetailResponse r1 = ProductDetailResponse.builder()
                    .id("1").name("Shirt").price(new BigDecimal("19.99")).availability(true).build();
            ProductDetailResponse r2 = ProductDetailResponse.builder()
                    .id("1").name("Shirt").availability(true).build();

            assertThat(r1).isNotEqualTo(r2);
        }

        @Test
        @DisplayName("should not be equal when this.availability is not null but other.availability is null")
        void shouldNotBeEqualWhenThisAvailabilityIsNotNullButOtherAvailabilityIsNull() {
            ProductDetailResponse r1 = ProductDetailResponse.builder()
                    .id("1").name("Shirt").price(new BigDecimal("19.99")).availability(true).build();
            ProductDetailResponse r2 = ProductDetailResponse.builder()
                    .id("1").name("Shirt").price(new BigDecimal("19.99")).build();

            assertThat(r1).isNotEqualTo(r2);
        }

        @Test
        @DisplayName("should not be equal when canEqual returns false on subclass")
        void shouldNotBeEqualWhenCanEqualReturnsFalseOnSubclass() {
            ProductDetailResponse response = ProductDetailResponse.builder()
                    .id("1").name("Shirt").price(new BigDecimal("19.99")).availability(true).build();
            ProductDetailResponseExtended extended = new ProductDetailResponseExtended();
            extended.setId("1");
            extended.setName("Shirt");
            extended.setPrice(new BigDecimal("19.99"));
            extended.setAvailability(true);

            assertThat(response).isNotEqualTo(extended);
        }

        @Test
        @DisplayName("should produce meaningful toString")
        void shouldProduceMeaningfulToString() {
            ProductDetailResponse response = ProductDetailResponse.builder()
                    .id("1").name("Shirt").price(new BigDecimal("19.99")).availability(true)
                    .build();

            String toString = response.toString();

            assertThat(toString).contains("1");
            assertThat(toString).contains("Shirt");
            assertThat(toString).contains("19.99");
            assertThat(toString).contains("true");
        }

        @Test
        @DisplayName("should produce meaningful builder toString")
        void shouldProduceMeaningfulBuilderToString() {
            String builderToString = ProductDetailResponse.builder().toString();

            assertThat(builderToString).contains("ProductDetailResponse");
        }
    }

    static class ProductDetailResponseExtended extends ProductDetailResponse {
        @Override
        public boolean canEqual(Object other) {
            return false;
        }
    }
}

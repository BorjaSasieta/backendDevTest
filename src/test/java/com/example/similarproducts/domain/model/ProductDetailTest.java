package com.example.similarproducts.domain.model;

import com.example.similarproducts.domain.Validator.ProductDetailValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ProductDetail domain model")
class ProductDetailTest {

    @Nested
    @DisplayName("Creation")
    class Creation {

        @Test
        @DisplayName("should create valid product detail")
        void shouldCreateValidProductDetail() {
            // Given
            String id = "1";
            String name = "Shirt";
            BigDecimal price = new BigDecimal("9.99");
            Boolean availability = true;

            // When
            ProductDetail product = ProductDetailValidator.createValidProductDetail(id, name, price, availability);

            // Then
            assertThat(product.getId()).isEqualTo(id);
            assertThat(product.getName()).isEqualTo(name);
            assertThat(product.getPrice()).isEqualTo(price);
            assertThat(product.getAvailability()).isEqualTo(availability);
        }

        @Test
        @DisplayName("should create product with zero price")
        void shouldCreateProductWithZeroPrice() {
            // Given
            BigDecimal zeroPrice = BigDecimal.ZERO;

            // When
            ProductDetail product = ProductDetailValidator.createValidProductDetail("1", "Free Item", zeroPrice, true);

            // Then
            assertThat(product.getPrice()).isEqualTo(zeroPrice);
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "\t", "\n"})
        @DisplayName("should reject blank id")
        void shouldRejectBlankId(String invalidId) {
            assertThatThrownBy(() -> ProductDetailValidator.createValidProductDetail(invalidId, "Name", BigDecimal.ONE, true))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("should reject null id")
        void shouldRejectNullId() {
            assertThatThrownBy(() -> ProductDetailValidator.createValidProductDetail(null, "Name", BigDecimal.ONE, true))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "\t", "\n"})
        @DisplayName("should reject blank name")
        void shouldRejectBlankName(String invalidName) {
            assertThatThrownBy(() -> ProductDetailValidator.createValidProductDetail("1", invalidName, BigDecimal.ONE, true))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("should reject null name")
        void shouldRejectNullName() {
            assertThatThrownBy(() -> ProductDetailValidator.createValidProductDetail("1", null, BigDecimal.ONE, true))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("should reject null price")
        void shouldRejectNullPrice() {
            assertThatThrownBy(() -> ProductDetailValidator.createValidProductDetail("1", "Name", null, true))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("should reject negative price")
        void shouldRejectNegativePrice() {
            assertThatThrownBy(() -> ProductDetailValidator.createValidProductDetail("1", "Name", new BigDecimal("-1.00"), true))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("should reject null availability")
        void shouldRejectNullAvailability() {
            assertThatThrownBy(() -> ProductDetailValidator.createValidProductDetail("1", "Name", BigDecimal.ONE, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("should be equal when same reference")
        void shouldBeEqualWhenSameReference() {
            ProductDetail product = ProductDetailValidator.createValidProductDetail("1", "Shirt", new BigDecimal("9.99"), true);

            assertThat(product).isEqualTo(product);
        }

        @Test
        @DisplayName("should be equal when all fields match")
        void shouldBeEqualWhenAllFieldsMatch() {
            ProductDetail product1 = ProductDetailValidator.createValidProductDetail("1", "Shirt", new BigDecimal("9.99"), true);
            ProductDetail product2 = ProductDetailValidator.createValidProductDetail("1", "Shirt", new BigDecimal("9.99"), true);

            assertThat(product1).isEqualTo(product2);
            assertThat(product1.hashCode()).isEqualTo(product2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when id differs")
        void shouldNotBeEqualWhenIdDiffers() {
            ProductDetail product1 = ProductDetailValidator.createValidProductDetail("1", "Shirt", new BigDecimal("9.99"), true);
            ProductDetail product2 = ProductDetailValidator.createValidProductDetail("2", "Shirt", new BigDecimal("9.99"), true);

            assertThat(product1).isNotEqualTo(product2);
        }

        @Test
        @DisplayName("should not be equal when name differs")
        void shouldNotBeEqualWhenNameDiffers() {
            ProductDetail product1 = ProductDetailValidator.createValidProductDetail("1", "Shirt", new BigDecimal("9.99"), true);
            ProductDetail product2 = ProductDetailValidator.createValidProductDetail("1", "Pants", new BigDecimal("9.99"), true);

            assertThat(product1).isNotEqualTo(product2);
        }

        @Test
        @DisplayName("should not be equal when price differs")
        void shouldNotBeEqualWhenPriceDiffers() {
            ProductDetail product1 = ProductDetailValidator.createValidProductDetail("1", "Shirt", new BigDecimal("9.99"), true);
            ProductDetail product2 = ProductDetailValidator.createValidProductDetail("1", "Shirt", new BigDecimal("19.99"), true);

            assertThat(product1).isNotEqualTo(product2);
        }

        @Test
        @DisplayName("should not be equal when availability differs")
        void shouldNotBeEqualWhenAvailabilityDiffers() {
            ProductDetail product1 = ProductDetailValidator.createValidProductDetail("1", "Shirt", new BigDecimal("9.99"), true);
            ProductDetail product2 = ProductDetailValidator.createValidProductDetail("1", "Shirt", new BigDecimal("9.99"), false);

            assertThat(product1).isNotEqualTo(product2);
        }

        @Test
        @DisplayName("should not be equal to null")
        void shouldNotBeEqualToNull() {
            ProductDetail product = ProductDetailValidator.createValidProductDetail("1", "Shirt", new BigDecimal("9.99"), true);

            assertThat(product).isNotEqualTo(null);
        }

        @Test
        @DisplayName("should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            ProductDetail product = ProductDetailValidator.createValidProductDetail("1", "Shirt", new BigDecimal("9.99"), true);

            assertThat(product).isNotEqualTo("not a product");
        }

        @Test
        @DisplayName("should not be equal when canEqual returns false on subclass")
        void shouldNotBeEqualWhenCanEqualReturnsFalseOnSubclass() {
            ProductDetail product = ProductDetailValidator.createValidProductDetail("1", "Shirt", new BigDecimal("9.99"), true);
            ProductDetailExtended extended = new ProductDetailExtended();
            extended.setId("1");
            extended.setName("Shirt");
            extended.setPrice(new BigDecimal("9.99"));
            extended.setAvailability(true);

            assertThat(product).isNotEqualTo(extended);
        }

        @Test
        @DisplayName("should be equal when all fields are null")
        void shouldBeEqualWhenAllFieldsAreNull() {
            ProductDetail product1 = ProductDetail.builder().build();
            ProductDetail product2 = ProductDetail.builder().build();

            assertThat(product1).isEqualTo(product2);
            assertThat(product1.hashCode()).isEqualTo(product2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when this.id is null but other.id is not")
        void shouldNotBeEqualWhenThisIdIsNullButOtherIdIsNotNull() {
            ProductDetail product1 = ProductDetail.builder()
                    .name("Shirt").price(new BigDecimal("9.99")).availability(true).build();
            ProductDetail product2 = ProductDetailValidator.createValidProductDetail("1", "Shirt", new BigDecimal("9.99"), true);

            assertThat(product1).isNotEqualTo(product2);
        }

        @Test
        @DisplayName("should not be equal when this.name is null but other.name is not")
        void shouldNotBeEqualWhenThisNameIsNullButOtherNameIsNotNull() {
            ProductDetail product1 = ProductDetail.builder()
                    .id("1").price(new BigDecimal("9.99")).availability(true).build();
            ProductDetail product2 = ProductDetailValidator.createValidProductDetail("1", "Shirt", new BigDecimal("9.99"), true);

            assertThat(product1).isNotEqualTo(product2);
        }

        @Test
        @DisplayName("should not be equal when this.price is null but other.price is not")
        void shouldNotBeEqualWhenThisPriceIsNullButOtherPriceIsNotNull() {
            ProductDetail product1 = ProductDetail.builder()
                    .id("1").name("Shirt").availability(true).build();
            ProductDetail product2 = ProductDetailValidator.createValidProductDetail("1", "Shirt", new BigDecimal("9.99"), true);

            assertThat(product1).isNotEqualTo(product2);
        }

        @Test
        @DisplayName("should not be equal when this.availability is null but other.availability is not")
        void shouldNotBeEqualWhenThisAvailabilityIsNullButOtherAvailabilityIsNotNull() {
            ProductDetail product1 = ProductDetail.builder()
                    .id("1").name("Shirt").price(new BigDecimal("9.99")).build();
            ProductDetail product2 = ProductDetailValidator.createValidProductDetail("1", "Shirt", new BigDecimal("9.99"), true);

            assertThat(product1).isNotEqualTo(product2);
        }
    }

    @Nested
    @DisplayName("toString")
    class ToString {

        @Test
        @DisplayName("should produce meaningful string representation")
        void shouldProduceMeaningfulString() {
            ProductDetail product = ProductDetailValidator.createValidProductDetail("1", "Shirt", new BigDecimal("9.99"), true);

            String toString = product.toString();

            assertThat(toString).contains("1");
            assertThat(toString).contains("Shirt");
            assertThat(toString).contains("9.99");
            assertThat(toString).contains("true");
        }

        @Test
        @DisplayName("should produce meaningful builder toString")
        void shouldProduceMeaningfulBuilderToString() {
            String builderToString = ProductDetail.builder().toString();

            assertThat(builderToString).contains("ProductDetail");
        }
    }

    static class ProductDetailExtended extends ProductDetail {
        @Override
        public boolean canEqual(Object other) {
            return false;
        }
    }
}

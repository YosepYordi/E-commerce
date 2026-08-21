package com.example.demo.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductRequestValidationTest {

    private static Validator validator;
    private static ValidatorFactory validatorFactory;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void rejectsInvalidProductFields() {
        ProductRequest request = new ProductRequest();
        request.setNombre(" ");
        request.setDescripcion("d".repeat(1001));
        request.setPrecio(new BigDecimal("-0.01"));
        request.setStock(-1);
        request.setCategoria(" ");
        request.setImagenUrl("not-a-url");

        Set<String> invalidFields = validator.validate(request).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(invalidFields.contains("nombre"));
        assertTrue(invalidFields.contains("descripcion"));
        assertTrue(invalidFields.contains("precio"));
        assertTrue(invalidFields.contains("stock"));
        assertTrue(invalidFields.contains("categoria"));
        assertTrue(invalidFields.contains("imagenUrl"));
    }

    @Test
    void acceptsZeroPriceAndStockAndOptionalImageUrl() {
        ProductRequest request = new ProductRequest();
        request.setNombre("Rompecabezas");
        request.setPrecio(BigDecimal.ZERO);
        request.setStock(0);
        request.setCategoria("Juegos");
        request.setImagenUrl("https://example.com/image.png");

        assertEquals(0, validator.validate(request).size());
    }

    @Test
    void rejectsPriceWithMoreThanEightIntegerDigits() {
        ProductRequest request = new ProductRequest();
        request.setNombre("Rompecabezas");
        request.setPrecio(new BigDecimal("100000000.00"));
        request.setStock(0);
        request.setCategoria("Juegos");

        Set<String> invalidFields = validator.validate(request).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(invalidFields.contains("precio"));
    }

    @Test
    void rejectsPriceWithMoreThanTwoFractionDigits() {
        ProductRequest request = new ProductRequest();
        request.setNombre("Rompecabezas");
        request.setPrecio(new BigDecimal("10.001"));
        request.setStock(0);
        request.setCategoria("Juegos");

        Set<String> invalidFields = validator.validate(request).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(invalidFields.contains("precio"));
    }

    @Test
    void acceptsPriceWithEightIntegerDigitsAndTwoFractionDigits() {
        ProductRequest request = new ProductRequest();
        request.setNombre("Rompecabezas");
        request.setPrecio(new BigDecimal("99999999.99"));
        request.setStock(0);
        request.setCategoria("Juegos");

        assertEquals(0, validator.validate(request).size());
    }

    @Test
    void rejectsCategoryAndImageUrlThatExceedEntityColumnLengths() {
        ProductRequest request = new ProductRequest();
        request.setNombre("Rompecabezas");
        request.setPrecio(BigDecimal.ZERO);
        request.setStock(0);
        request.setCategoria("c".repeat(256));
        request.setImagenUrl("https://example.com/" + "i".repeat(500));

        Set<String> invalidFields = validator.validate(request).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(invalidFields.contains("categoria"));
        assertTrue(invalidFields.contains("imagenUrl"));
    }

    @Test
    void rejectsPriceExceedingPrecisionOrScale() {
        // Test case 1: Price with more than 8 integer digits (e.g. 123456789.99)
        ProductRequest requestIntegerExceeded = new ProductRequest();
        requestIntegerExceeded.setNombre("Rompecabezas");
        requestIntegerExceeded.setPrecio(new BigDecimal("123456789.99"));
        requestIntegerExceeded.setStock(10);
        requestIntegerExceeded.setCategoria("Juegos");
        requestIntegerExceeded.setImagenUrl("https://example.com/image.png");

        Set<String> violationsInteger = validator.validate(requestIntegerExceeded).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());
        assertTrue(violationsInteger.contains("precio"));

        // Test case 2: Price with more than 2 decimal places (e.g. 10.001)
        ProductRequest requestFractionExceeded = new ProductRequest();
        requestFractionExceeded.setNombre("Rompecabezas");
        requestFractionExceeded.setPrecio(new BigDecimal("10.001"));
        requestFractionExceeded.setStock(10);
        requestFractionExceeded.setCategoria("Juegos");
        requestFractionExceeded.setImagenUrl("https://example.com/image.png");

        Set<String> violationsFraction = validator.validate(requestFractionExceeded).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());
        assertTrue(violationsFraction.contains("precio"));
    }
}

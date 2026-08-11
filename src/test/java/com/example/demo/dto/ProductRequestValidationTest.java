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
}

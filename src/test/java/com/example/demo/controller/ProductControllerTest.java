package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ProductRequest;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void getAllProductsReturnsProductsAndPassesFilters() throws Exception {
        when(productService.getAllProducts("Juegos", "rompe"))
                .thenReturn(List.of(product(1L)));

        mockMvc.perform(get("/api/products")
                        .param("categoria", "Juegos")
                        .param("query", "rompe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(productService).getAllProducts("Juegos", "rompe");
    }

    @Test
    void getProductByIdReturnsNotFoundWithPathWhenMissing() throws Exception {
        when(productService.getProductById(99L))
                .thenThrow(new ResourceNotFoundException("Producto no encontrado con ID: 99"));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/products/99"));
    }

    @Test
    void createProductReturnsCreatedAndDoesNotAcceptIdInRequest() throws Exception {
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(product(10L));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));

        ArgumentCaptor<ProductRequest> requestCaptor = ArgumentCaptor.forClass(ProductRequest.class);
        verify(productService).createProduct(requestCaptor.capture());
        org.junit.jupiter.api.Assertions.assertEquals("Rompecabezas", requestCaptor.getValue().getNombre());
    }

    @Test
    void updateProductReturnsUpdatedProduct() throws Exception {
        when(productService.updateProduct(eq(10L), any(ProductRequest.class))).thenReturn(product(10L));

        mockMvc.perform(put("/api/products/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void deleteProductReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/products/10"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(10L);
    }

    @Test
    void createProductRejectsInvalidRequestWithCommonErrorShape() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "",
                                  "descripcion": "válido",
                                  "precio": -1,
                                  "stock": -1,
                                  "categoria": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/products"))
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    void malformedJsonReturnsBadRequestWithCommonErrorShape() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Rompecabezas\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/products"));
    }

    @Test
    void emptyJsonBodyReturnsBadRequestWithCommonErrorShape() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/products"));
    }

    private ProductDTO product(Long id) {
        return new ProductDTO(id, "Rompecabezas", "Juego de piezas", new BigDecimal("10.00"), 4, "Juegos", "https://example.com/image.png");
    }

    private String validJson() {
        return """
                {
                  "nombre": "Rompecabezas",
                  "descripcion": "Juego de piezas",
                  "precio": 10.00,
                  "stock": 4,
                  "categoria": "Juegos",
                  "imagenUrl": "https://example.com/image.png"
                }
                """;
    }
}

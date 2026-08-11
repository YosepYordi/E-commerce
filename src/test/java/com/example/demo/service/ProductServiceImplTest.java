package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ProductRequest;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository);
    }

    @Test
    void createProductMapsRequestWithoutAcceptingAnId() {
        ProductRequest request = validRequest();
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            assertNull(product.getId());
            product.setId(42L);
            return product;
        });

        ProductDTO result = productService.createProduct(request);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        Product savedProduct = productCaptor.getValue();
        assertEquals("Rompecabezas", savedProduct.getNombre());
        assertEquals(42L, result.getId());
    }

    @Test
    void updateProductChangesEditableFieldsOnTheRequestedProduct() {
        Product existingProduct = new Product(7L, "Antes", "Descripción anterior", new BigDecimal("10.00"), 2, "Juegos", null);
        ProductRequest request = validRequest();
        when(productRepository.findById(7L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        ProductDTO result = productService.updateProduct(7L, request);

        assertEquals(7L, result.getId());
        assertEquals(request.getNombre(), existingProduct.getNombre());
        assertEquals(request.getPrecio(), existingProduct.getPrecio());
        assertEquals(request.getStock(), existingProduct.getStock());
    }

    @Test
    void getAllProductsUsesCombinedCategoryAndQueryFilter() {
        Product product = new Product(1L, "Rompecabezas", "", new BigDecimal("10.00"), 4, "Juegos", null);
        when(productRepository.findByCategoriaIgnoreCaseAndNombreContainingIgnoreCase("Juegos", "rompe"))
                .thenReturn(List.of(product));

        List<ProductDTO> result = productService.getAllProducts("Juegos", "rompe");

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(productRepository).findByCategoriaIgnoreCaseAndNombreContainingIgnoreCase("Juegos", "rompe");
    }

    @Test
    void updateProductRejectsUnknownId() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(99L, validRequest()));
    }

    @Test
    void deleteProductRejectsUnknownId() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(99L));
    }

    private ProductRequest validRequest() {
        ProductRequest request = new ProductRequest();
        request.setNombre("Rompecabezas");
        request.setDescripcion("Juego de piezas");
        request.setPrecio(new BigDecimal("10.00"));
        request.setStock(4);
        request.setCategoria("Juegos");
        request.setImagenUrl("https://example.com/image.png");
        return request;
    }
}

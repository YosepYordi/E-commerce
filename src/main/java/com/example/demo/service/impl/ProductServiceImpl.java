package com.example.demo.service.impl;

import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ProductRequest;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts(String categoria, String query) {
        List<Product> products;
        boolean hasCategory = categoria != null && !categoria.isBlank();
        boolean hasQuery = query != null && !query.isBlank();

        if (hasCategory && hasQuery) {
            products = productRepository.findByCategoriaIgnoreCaseAndNombreContainingIgnoreCase(categoria, query);
        } else if (hasCategory) {
            products = productRepository.findByCategoriaIgnoreCase(categoria);
        } else if (hasQuery) {
            products = productRepository.findByNombreContainingIgnoreCase(query);
        } else {
            products = productRepository.findAll();
        }

        return products.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return mapToDTO(product);
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductRequest productRequest) {
        validateProductRequest(productRequest);

        Product product = mapToEntity(productRequest);
        Product savedProduct = productRepository.save(product);

        return mapToDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductRequest productRequest) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        validateProductRequest(productRequest);

        existingProduct.setNombre(productRequest.getNombre());
        existingProduct.setDescripcion(productRequest.getDescripcion());
        existingProduct.setPrecio(productRequest.getPrecio());
        existingProduct.setStock(productRequest.getStock());
        existingProduct.setCategoria(productRequest.getCategoria());
        existingProduct.setImagenUrl(productRequest.getImagenUrl());

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        productRepository.deleteById(id);
    }

    private void validateProductRequest(ProductRequest request) {
        if (request == null) {
            throw new BadRequestException("El producto es obligatorio");
        }
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del producto es obligatorio");
        }
        if (request.getNombre().length() > 120) {
            throw new BadRequestException("El nombre no puede superar los 120 caracteres");
        }
        if (request.getDescripcion() != null && request.getDescripcion().length() > 1000) {
            throw new BadRequestException("La descripción no puede superar los 1000 caracteres");
        }
        if (request.getPrecio() == null || request.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("El precio debe ser mayor o igual a 0");
        }
        if (request.getStock() == null || request.getStock() < 0) {
            throw new BadRequestException("El stock debe ser mayor o igual a 0");
        }
        if (request.getCategoria() == null || request.getCategoria().isBlank()) {
            throw new BadRequestException("La categoría es obligatoria");
        }
    }

    private ProductDTO mapToDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getNombre(),
                product.getDescripcion(),
                product.getPrecio(),
                product.getStock(),
                product.getCategoria(),
                product.getImagenUrl()
        );
    }

    private Product mapToEntity(ProductRequest request) {
        return new Product(
                request.getNombre(),
                request.getDescripcion(),
                request.getPrecio(),
                request.getStock(),
                request.getCategoria(),
                request.getImagenUrl()
        );
    }
}

package com.example.demo.service.impl;

import com.example.demo.dto.ProductDTO;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ProductDTO createProduct(ProductDTO productDTO) {
        validateProductDTO(productDTO);

        Product product = mapToEntity(productDTO);
        product.setId(null);
        Product savedProduct = productRepository.save(product);

        return mapToDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        validateProductDTO(productDTO);

        existingProduct.setNombre(productDTO.getNombre());
        existingProduct.setDescripcion(productDTO.getDescripcion());
        existingProduct.setPrecio(productDTO.getPrecio());
        existingProduct.setStock(productDTO.getStock());
        existingProduct.setCategoria(productDTO.getCategoria());
        existingProduct.setImagenUrl(productDTO.getImagenUrl());

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

    private void validateProductDTO(ProductDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del producto es obligatorio");
        }
        if (dto.getPrecio() == null || dto.getPrecio().doubleValue() < 0) {
            throw new BadRequestException("El precio debe ser mayor o igual a 0");
        }
        if (dto.getStock() == null || dto.getStock() < 0) {
            throw new BadRequestException("El stock no puede ser negativo");
        }
        if (dto.getCategoria() == null || dto.getCategoria().isBlank()) {
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

    private Product mapToEntity(ProductDTO dto) {
        return new Product(
                dto.getId(),
                dto.getNombre(),
                dto.getDescripcion(),
                dto.getPrecio(),
                dto.getStock(),
                dto.getCategoria(),
                dto.getImagenUrl()
        );
    }
}

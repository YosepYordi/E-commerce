package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ProductRequest;

import java.util.List;

public interface ProductService {
    List<ProductDTO> getAllProducts(String categoria, String query);
    ProductDTO getProductById(Long id);
    ProductDTO createProduct(ProductRequest productRequest);
    ProductDTO updateProduct(Long id, ProductRequest productRequest);
    void deleteProduct(Long id);
}

package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoriaIgnoreCase(String categoria);
    List<Product> findByNombreContainingIgnoreCase(String nombre);
    List<Product> findByCategoriaIgnoreCaseAndNombreContainingIgnoreCase(String categoria, String nombre);
}

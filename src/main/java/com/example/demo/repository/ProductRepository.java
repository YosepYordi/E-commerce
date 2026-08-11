package com.example.demo.repository;

import com.example.demo.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoriaIgnoreCase(String categoria);
    List<Product> findByNombreContainingIgnoreCase(String nombre);
    List<Product> findByCategoriaIgnoreCaseAndNombreContainingIgnoreCase(String categoria, String nombre);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id in :ids")
    List<Product> findAllByIdInForUpdate(@Param("ids") Collection<Long> ids);
}

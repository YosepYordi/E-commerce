package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    private Product product;
    private Integer cantidad;

    public BigDecimal getSubtotal() {
        if (product == null || product.getPrecio() == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return product.getPrecio().multiply(BigDecimal.valueOf(cantidad));
    }
}

package com.example.demo.model;

import java.math.BigDecimal;

/**
 * A calculated cart line. It is created from the current Product data and is
 * never stored in the HTTP session.
 */
public class CartItem {

    private final Product product;
    private final int cantidad;

    public CartItem(Product product, int cantidad) {
        this.product = product;
        this.cantidad = cantidad;
    }

    public Product getProduct() {
        return product;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getSubtotal() {
        if (product == null || product.getPrecio() == null) {
            return BigDecimal.ZERO;
        }
        return product.getPrecio().multiply(BigDecimal.valueOf(cantidad));
    }
}

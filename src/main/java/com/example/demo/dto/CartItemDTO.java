package com.example.demo.dto;

import java.math.BigDecimal;

public class CartItemDTO {
    private ProductDTO product;
    private Integer cantidad;
    private BigDecimal subtotal;

    public CartItemDTO() {
    }

    public CartItemDTO(ProductDTO product, Integer cantidad, BigDecimal subtotal) {
        this.product = product;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public ProductDTO getProduct() { return product; }
    public void setProduct(ProductDTO product) { this.product = product; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}

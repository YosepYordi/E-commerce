package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartSummaryDTO {
    private List<CartItemDTO> items;
    private BigDecimal total;
    private Integer totalItems;

    public CartSummaryDTO() {
    }

    public CartSummaryDTO(List<CartItemDTO> items, BigDecimal total, Integer totalItems) {
        this.items = items;
        this.total = total;
        this.totalItems = totalItems;
    }

    public List<CartItemDTO> getItems() { return items; }
    public void setItems(List<CartItemDTO> items) { this.items = items; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
}

package com.example.demo.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Read model used to calculate a response from the ids and quantities kept in
 * the session. It is intentionally not a session-persisted object.
 */
public class ShoppingCart {

    private final List<CartItem> items;

    public ShoppingCart(List<CartItem> items) {
        this.items = List.copyOf(items);
    }

    public List<CartItem> getItems() {
        return items;
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalItemCount() {
        return items.stream()
                .mapToInt(CartItem::getCantidad)
                .sum();
    }
}

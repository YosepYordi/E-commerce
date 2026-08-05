package com.example.demo.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class ShoppingCart {

    private List<CartItem> items = new ArrayList<>();

    public void addItem(Product product, int cantidad) {
        Optional<CartItem> existingItem = items.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setCantidad(item.getCantidad() + cantidad);
        } else {
            items.add(new CartItem(product, cantidad));
        }
    }

    public void updateQuantity(Long productId, int cantidad) {
        if (cantidad <= 0) {
            removeItem(productId);
            return;
        }
        items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setCantidad(cantidad));
    }

    public void removeItem(Long productId) {
        items.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public void clear() {
        items.clear();
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

package com.example.demo.repository;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Session-backed cart storage. Never place Product entities in the session: only
 * a Map<Long, Integer> of product ids and quantities is persisted.
 */
@Repository
public class SessionCartRepository implements CartRepository {

    static final String CART_SESSION_KEY = "SHOPPING_CART";

    @Override
    public Map<Long, Integer> getCart(HttpSession session) {
        Object storedCart = session.getAttribute(CART_SESSION_KEY);
        if (!(storedCart instanceof Map<?, ?> persistedCart)) {
            return new LinkedHashMap<>();
        }

        Map<Long, Integer> cart = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : persistedCart.entrySet()) {
            if (entry.getKey() instanceof Long productId
                    && entry.getValue() instanceof Integer quantity
                    && quantity > 0) {
                cart.put(productId, quantity);
            }
        }
        return cart;
    }

    @Override
    public void saveCart(HttpSession session, Map<Long, Integer> cart) {
        Map<Long, Integer> cartToPersist = new LinkedHashMap<>();
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();
            if (productId == null || quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("A cart entry requires a product id and a positive quantity");
            }
            cartToPersist.put(productId, quantity);
        }

        session.setAttribute(CART_SESSION_KEY, cartToPersist);
    }

    @Override
    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }
}

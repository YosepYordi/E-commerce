package com.example.demo.repository;

import jakarta.servlet.http.HttpSession;

import java.util.Map;

/**
 * Persists the cart associated with an HTTP session. The persisted representation
 * deliberately contains only product identifiers and quantities.
 */
public interface CartRepository {

    Map<Long, Integer> getCart(HttpSession session);

    void saveCart(HttpSession session, Map<Long, Integer> cart);

    void clearCart(HttpSession session);
}

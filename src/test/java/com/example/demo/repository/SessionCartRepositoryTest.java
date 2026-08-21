package com.example.demo.repository;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionCartRepositoryTest {

    private final SessionCartRepository cartRepository = new SessionCartRepository();

    @Test
    void storesOnlyProductIdsAndQuantitiesAndKeepsSessionsIsolated() {
        MockHttpSession firstSession = new MockHttpSession();
        MockHttpSession secondSession = new MockHttpSession();

        cartRepository.saveCart(firstSession, Map.of(10L, 2));

        Object storedCart = firstSession.getAttribute(SessionCartRepository.CART_SESSION_KEY);
        assertTrue(storedCart instanceof Map<?, ?>);
        assertEquals(Map.of(10L, 2), storedCart);
        assertEquals(Map.of(10L, 2), cartRepository.getCart(firstSession));
        assertTrue(cartRepository.getCart(secondSession).isEmpty());

        cartRepository.clearCart(firstSession);
        assertNull(firstSession.getAttribute(SessionCartRepository.CART_SESSION_KEY));
    }
}

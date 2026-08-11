package com.example.demo.service.impl;

import com.example.demo.dto.AddToCartRequest;
import com.example.demo.dto.CartSummaryDTO;
import com.example.demo.dto.UpdateCartRequest;
import com.example.demo.exception.BadRequestException;
import com.example.demo.model.Product;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CartServiceImplTest {

    private ProductRepository productRepository;
    private InMemoryCartRepository cartRepository;
    private CartServiceImpl cartService;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        cartRepository = new InMemoryCartRepository();
        cartService = new CartServiceImpl(productRepository, cartRepository);
        session = mock(HttpSession.class);
    }

    @Test
    void returnsAnEmptyCartWhenTheSessionHasNoItems() {
        CartSummaryDTO cart = cartService.getCart(session);

        assertTrue(cart.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getTotal());
        assertEquals(0, cart.getTotalItems());
    }

    @Test
    void addsTheSameProductTwiceAndRejectsCombinedQuantityAboveStock() {
        Product product = product(1L, "Robot", "10.00", 5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        cartService.addToCart(new AddToCartRequest(1L, 2), session);
        CartSummaryDTO summary = cartService.addToCart(new AddToCartRequest(1L, 3), session);

        assertEquals(Map.of(1L, 5), cartRepository.getCart(session));
        assertEquals(5, summary.getTotalItems());
        assertEquals(new BigDecimal("50.00"), summary.getTotal());

        assertThrows(BadRequestException.class,
                () -> cartService.addToCart(new AddToCartRequest(1L, 1), session));
        assertEquals(Map.of(1L, 5), cartRepository.getCart(session));
    }

    @Test
    void recalculatesTotalsUsingTheCurrentProductPrice() {
        Product product = product(1L, "Robot", "10.00", 5);
        cartRepository.saveCart(session, Map.of(1L, 2));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertEquals(new BigDecimal("20.00"), cartService.getCart(session).getTotal());

        product.setPrecio(new BigDecimal("12.50"));
        assertEquals(new BigDecimal("25.00"), cartService.getCart(session).getTotal());
    }

    @Test
    void updatesQuantityAndUsesZeroToRemoveTheItem() {
        Product product = product(1L, "Robot", "10.00", 5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        cartService.addToCart(new AddToCartRequest(1L, 2), session);

        CartSummaryDTO updatedCart = cartService.updateCartQuantity(new UpdateCartRequest(1L, 4), session);
        assertEquals(Map.of(1L, 4), cartRepository.getCart(session));
        assertEquals(4, updatedCart.getTotalItems());

        CartSummaryDTO emptyCart = cartService.updateCartQuantity(new UpdateCartRequest(1L, 0), session);
        assertTrue(emptyCart.getItems().isEmpty());
        assertTrue(cartRepository.getCart(session).isEmpty());
    }

    @Test
    void removesAndClearsCartItems() {
        Product product = product(1L, "Robot", "10.00", 5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        cartService.addToCart(new AddToCartRequest(1L, 2), session);

        assertTrue(cartService.removeFromCart(1L, session).getItems().isEmpty());

        cartService.addToCart(new AddToCartRequest(1L, 2), session);
        cartService.clearCart(session);
        assertTrue(cartService.getCart(session).getItems().isEmpty());
    }

    @Test
    void checkoutUpdatesStockAndClearsTheSessionAfterSaving() {
        Product robot = product(1L, "Robot", "10.00", 5);
        Product doll = product(2L, "Muñeca", "7.50", 3);
        cartRepository.saveCart(session, Map.of(1L, 2, 2L, 1));
        when(productRepository.findAllByIdInForUpdate(anyCollection())).thenReturn(List.of(robot, doll));

        CartSummaryDTO summary = cartService.checkout(session);

        assertEquals(new BigDecimal("27.50"), summary.getTotal());
        assertEquals(3, summary.getTotalItems());
        assertEquals(3, robot.getStock());
        assertEquals(2, doll.getStock());
        assertTrue(cartRepository.getCart(session).isEmpty());
        verify(productRepository).saveAllAndFlush(any());
    }

    @Test
    void checkoutDoesNotModifyAnyStockOrClearTheCartWhenOneItemHasNoStock() {
        Product robot = product(1L, "Robot", "10.00", 5);
        Product doll = product(2L, "Muñeca", "7.50", 1);
        cartRepository.saveCart(session, Map.of(1L, 2, 2L, 2));
        when(productRepository.findAllByIdInForUpdate(anyCollection())).thenReturn(List.of(robot, doll));

        assertThrows(BadRequestException.class, () -> cartService.checkout(session));

        assertEquals(5, robot.getStock());
        assertEquals(1, doll.getStock());
        assertEquals(Map.of(1L, 2, 2L, 2), cartRepository.getCart(session));
        verify(productRepository, never()).saveAllAndFlush(any());
    }

    @Test
    void checkoutRejectsAnEmptyCart() {
        assertThrows(BadRequestException.class, () -> cartService.checkout(session));
        verify(productRepository, never()).findAllByIdInForUpdate(anyCollection());
    }

    private Product product(Long id, String name, String price, int stock) {
        return new Product(id, name, "Descripcion", new BigDecimal(price), stock, "Categoria", "image.jpg");
    }

    private static class InMemoryCartRepository implements CartRepository {

        private final Map<HttpSession, Map<Long, Integer>> carts = new IdentityHashMap<>();

        @Override
        public Map<Long, Integer> getCart(HttpSession session) {
            return new LinkedHashMap<>(carts.getOrDefault(session, Map.of()));
        }

        @Override
        public void saveCart(HttpSession session, Map<Long, Integer> cart) {
            carts.put(session, new LinkedHashMap<>(cart));
        }

        @Override
        public void clearCart(HttpSession session) {
            carts.remove(session);
        }
    }
}

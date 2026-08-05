package com.example.demo.controller;

import com.example.demo.dto.AddToCartRequest;
import com.example.demo.dto.CartSummaryDTO;
import com.example.demo.dto.UpdateCartRequest;
import com.example.demo.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartSummaryDTO> getCart(HttpSession session) {
        return ResponseEntity.ok(cartService.getCart(session));
    }

    @PostMapping("/add")
    public ResponseEntity<CartSummaryDTO> addToCart(@RequestBody AddToCartRequest request, HttpSession session) {
        return ResponseEntity.ok(cartService.addToCart(request, session));
    }

    @PutMapping("/update")
    public ResponseEntity<CartSummaryDTO> updateCartQuantity(@RequestBody UpdateCartRequest request, HttpSession session) {
        return ResponseEntity.ok(cartService.updateCartQuantity(request, session));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartSummaryDTO> removeFromCart(@PathVariable Long productId, HttpSession session) {
        return ResponseEntity.ok(cartService.removeFromCart(productId, session));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(HttpSession session) {
        cartService.clearCart(session);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<CartSummaryDTO> checkout(HttpSession session) {
        return ResponseEntity.ok(cartService.checkout(session));
    }
}

package com.example.demo.service;

import com.example.demo.dto.AddToCartRequest;
import com.example.demo.dto.CartSummaryDTO;
import com.example.demo.dto.UpdateCartRequest;
import jakarta.servlet.http.HttpSession;

public interface CartService {
    CartSummaryDTO getCart(HttpSession session);
    CartSummaryDTO addToCart(AddToCartRequest request, HttpSession session);
    CartSummaryDTO updateCartQuantity(UpdateCartRequest request, HttpSession session);
    CartSummaryDTO removeFromCart(Long productId, HttpSession session);
    void clearCart(HttpSession session);
    CartSummaryDTO checkout(HttpSession session);
}

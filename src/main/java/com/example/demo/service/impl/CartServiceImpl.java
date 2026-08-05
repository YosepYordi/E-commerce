package com.example.demo.service.impl;

import com.example.demo.dto.AddToCartRequest;
import com.example.demo.dto.CartItemDTO;
import com.example.demo.dto.CartSummaryDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.UpdateCartRequest;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.CartItem;
import com.example.demo.model.Product;
import com.example.demo.model.ShoppingCart;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private static final String CART_SESSION_KEY = "SHOPPING_CART";
    private final ProductRepository productRepository;

    public CartServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public CartSummaryDTO getCart(HttpSession session) {
        ShoppingCart cart = getOrCreateCart(session);
        return mapToCartSummaryDTO(cart);
    }

    @Override
    public CartSummaryDTO addToCart(AddToCartRequest request, HttpSession session) {
        if (request.getProductId() == null) {
            throw new BadRequestException("El ID del producto es obligatorio");
        }
        int cantidad = request.getCantidad() != null ? request.getCantidad() : 1;
        if (cantidad <= 0) {
            throw new BadRequestException("La cantidad debe ser mayor a 0");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + request.getProductId()));

        if (product.getStock() < cantidad) {
            throw new BadRequestException("Stock insuficiente para el producto: " + product.getNombre());
        }

        ShoppingCart cart = getOrCreateCart(session);
        cart.addItem(product, cantidad);

        return mapToCartSummaryDTO(cart);
    }

    @Override
    public CartSummaryDTO updateCartQuantity(UpdateCartRequest request, HttpSession session) {
        if (request.getProductId() == null) {
            throw new BadRequestException("El ID del producto es obligatorio");
        }

        ShoppingCart cart = getOrCreateCart(session);
        int cantidad = request.getCantidad() != null ? request.getCantidad() : 0;

        if (cantidad > 0) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + request.getProductId()));
            if (product.getStock() < cantidad) {
                throw new BadRequestException("Stock insuficiente para actualizar a la cantidad solicitada");
            }
        }

        cart.updateQuantity(request.getProductId(), cantidad);
        return mapToCartSummaryDTO(cart);
    }

    @Override
    public CartSummaryDTO removeFromCart(Long productId, HttpSession session) {
        ShoppingCart cart = getOrCreateCart(session);
        cart.removeItem(productId);
        return mapToCartSummaryDTO(cart);
    }

    @Override
    public void clearCart(HttpSession session) {
        ShoppingCart cart = getOrCreateCart(session);
        cart.clear();
    }

    @Override
    public CartSummaryDTO checkout(HttpSession session) {
        ShoppingCart cart = getOrCreateCart(session);
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("El carrito está vacío, no se puede procesar la compra.");
        }

        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado durante la compra"));
            if (product.getStock() < item.getCantidad()) {
                throw new BadRequestException("Stock insuficiente para " + product.getNombre() + " durante el checkout");
            }
            product.setStock(product.getStock() - item.getCantidad());
            productRepository.save(product);
        }

        CartSummaryDTO summary = mapToCartSummaryDTO(cart);
        cart.clear();
        return summary;
    }

    private ShoppingCart getOrCreateCart(HttpSession session) {
        ShoppingCart cart = (ShoppingCart) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ShoppingCart();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    private CartSummaryDTO mapToCartSummaryDTO(ShoppingCart cart) {
        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(item -> new CartItemDTO(
                        mapProductToDTO(item.getProduct()),
                        item.getCantidad(),
                        item.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new CartSummaryDTO(
                itemDTOs,
                cart.getTotal(),
                cart.getTotalItemCount()
        );
    }

    private ProductDTO mapProductToDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getNombre(),
                product.getDescripcion(),
                product.getPrecio(),
                product.getStock(),
                product.getCategoria(),
                product.getImagenUrl()
        );
    }
}

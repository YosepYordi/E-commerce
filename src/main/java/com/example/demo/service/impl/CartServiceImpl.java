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
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public CartServiceImpl(ProductRepository productRepository, CartRepository cartRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public CartSummaryDTO getCart(HttpSession session) {
        return mapToCartSummaryDTO(createShoppingCart(cartRepository.getCart(session)));
    }

    @Override
    public CartSummaryDTO addToCart(AddToCartRequest request, HttpSession session) {
        validateRequest(request == null ? null : request.getProductId(), request == null ? null : request.getCantidad(), true);

        Long productId = request.getProductId();
        int quantityToAdd = request.getCantidad();
        Product product = getProduct(productId);
        Map<Long, Integer> cart = getMutableCart(session);
        int currentQuantity = cart.getOrDefault(productId, 0);
        int requestedQuantity = addQuantities(currentQuantity, quantityToAdd);

        validateStock(product, requestedQuantity);
        cart.put(productId, requestedQuantity);
        cartRepository.saveCart(session, cart);

        return mapToCartSummaryDTO(createShoppingCart(cart));
    }

    @Override
    public CartSummaryDTO updateCartQuantity(UpdateCartRequest request, HttpSession session) {
        validateRequest(request == null ? null : request.getProductId(), request == null ? null : request.getCantidad(), false);

        Long productId = request.getProductId();
        int requestedQuantity = request.getCantidad();
        Map<Long, Integer> cart = getMutableCart(session);

        if (requestedQuantity == 0) {
            cart.remove(productId);
        } else {
            if (!cart.containsKey(productId)) {
                throw new BadRequestException("El producto no esta en el carrito");
            }
            Product product = getProduct(productId);
            validateStock(product, requestedQuantity);
            cart.put(productId, requestedQuantity);
        }

        cartRepository.saveCart(session, cart);
        return mapToCartSummaryDTO(createShoppingCart(cart));
    }

    @Override
    public CartSummaryDTO removeFromCart(Long productId, HttpSession session) {
        if (productId == null) {
            throw new BadRequestException("El ID del producto es obligatorio");
        }

        Map<Long, Integer> cart = getMutableCart(session);
        cart.remove(productId);
        cartRepository.saveCart(session, cart);
        return mapToCartSummaryDTO(createShoppingCart(cart));
    }

    @Override
    public void clearCart(HttpSession session) {
        cartRepository.clearCart(session);
    }

    @Override
    @Transactional
    public CartSummaryDTO checkout(HttpSession session) {
        Map<Long, Integer> cart = getMutableCart(session);
        if (cart.isEmpty()) {
            throw new BadRequestException("El carrito esta vacio, no se puede procesar la compra");
        }

        Map<Long, Product> lockedProducts = getLockedProducts(cart);
        ShoppingCart shoppingCart = createShoppingCart(cart, lockedProducts);

        // Validate every item before changing any product stock.
        for (CartItem item : shoppingCart.getItems()) {
            validateStock(item.getProduct(), item.getCantidad());
        }

        CartSummaryDTO summary = mapToCartSummaryDTO(shoppingCart);
        List<Product> productsToUpdate = new ArrayList<>();
        for (CartItem item : shoppingCart.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getCantidad());
            productsToUpdate.add(product);
        }

        // Flush first so the session is only cleared after the stock changes are saved.
        productRepository.saveAllAndFlush(productsToUpdate);
        cartRepository.clearCart(session);
        return summary;
    }

    private Map<Long, Integer> getMutableCart(HttpSession session) {
        return new LinkedHashMap<>(cartRepository.getCart(session));
    }

    private Map<Long, Product> getLockedProducts(Map<Long, Integer> cart) {
        Map<Long, Product> productsById = new LinkedHashMap<>();
        for (Product product : productRepository.findAllByIdInForUpdate(cart.keySet())) {
            productsById.put(product.getId(), product);
        }
        return productsById;
    }

    private ShoppingCart createShoppingCart(Map<Long, Integer> cart) {
        Map<Long, Product> productsById = new LinkedHashMap<>();
        for (Long productId : cart.keySet()) {
            productsById.put(productId, getProduct(productId));
        }
        return createShoppingCart(cart, productsById);
    }

    private ShoppingCart createShoppingCart(Map<Long, Integer> cart, Map<Long, Product> productsById) {
        List<CartItem> items = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();
            if (productId == null || quantity == null || quantity <= 0) {
                throw new BadRequestException("El carrito contiene una cantidad invalida");
            }

            Product product = productsById.get(productId);
            if (product == null) {
                throw new ResourceNotFoundException("Producto no encontrado con ID: " + productId);
            }
            items.add(new CartItem(product, quantity));
        }
        return new ShoppingCart(items);
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productId));
    }

    private void validateRequest(Long productId, Integer quantity, boolean requiresPositiveQuantity) {
        if (productId == null) {
            throw new BadRequestException("El ID del producto es obligatorio");
        }
        if (quantity == null) {
            throw new BadRequestException("La cantidad es obligatoria");
        }
        if (requiresPositiveQuantity && quantity <= 0) {
            throw new BadRequestException("La cantidad debe ser mayor a 0");
        }
        if (!requiresPositiveQuantity && quantity < 0) {
            throw new BadRequestException("La cantidad no puede ser negativa");
        }
    }

    private int addQuantities(int currentQuantity, int quantityToAdd) {
        try {
            return Math.addExact(currentQuantity, quantityToAdd);
        } catch (ArithmeticException exception) {
            throw new BadRequestException("La cantidad solicitada es demasiado grande");
        }
    }

    private void validateStock(Product product, int requestedQuantity) {
        if (product.getStock() == null || product.getStock() < requestedQuantity) {
            throw new BadRequestException("Stock insuficiente para el producto: " + product.getNombre());
        }
    }

    private CartSummaryDTO mapToCartSummaryDTO(ShoppingCart cart) {
        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(item -> new CartItemDTO(
                        mapProductToDTO(item.getProduct()),
                        item.getCantidad(),
                        item.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new CartSummaryDTO(itemDTOs, cart.getTotal(), cart.getTotalItemCount());
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

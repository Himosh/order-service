package com.mini_project.service;

import com.mini_project.model.Cart;
import com.mini_project.model.dto.ProductResponse;
import com.mini_project.model.enums.CartStatus;
import com.mini_project.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    @Override
    public Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    public Cart getCartByUserId(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
    }

    public Cart createCart(String userId) {
        Optional<Cart> activeCart = cartRepository.findByUserIdAndCartStatus(userId, CartStatus.ACTIVE);
        if (activeCart.isPresent()) {
            throw new RuntimeException("User already has an active cart. Please cancel it before creating a new one.");
        }

        Cart newCart = new Cart();
        newCart.setUserId(userId);
        newCart.setItems(new ArrayList<>());
        newCart.setTotalAmount(0.0);
        newCart.setCartStatus(CartStatus.ACTIVE);
        return cartRepository.save(newCart);
    }

    @Override
    public Cart updateCart(Cart cart) {
        if (!cart.getCartStatus().equals(CartStatus.ACTIVE)) {
            throw new RuntimeException("Cannot update a non-active cart.");
        }
        return cartRepository.save(cart);
    }

    @Override
    public void cancelCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        if (!cart.getCartStatus().equals(CartStatus.ACTIVE)) {
            throw new RuntimeException("Cart is already canceled.");
        }

        cart.setCartStatus(CartStatus.CANCELED);
        cartRepository.save(cart);
    }

    public void clearCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    @Override
    public Cart addOrUpdateProduct(Long cartId, Long productId, Integer quantity) {
        // Retrieve the cart
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (!cart.getCartStatus().equals(CartStatus.ACTIVE)) {
            throw new RuntimeException("Cannot modify a non-active cart.");
        }

        // Fetch product details from the product service
        ProductResponse productResponse;
        try {
            productResponse = restTemplate.getForObject(
                    "http://localhost:8083/api/v1/products/" + productId, ProductResponse.class);
            if (productResponse == null) {
                throw new RuntimeException("Product not found.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching product details: " + e.getMessage(), e);
        }

        // Check if the product already exists in the cart
        Optional<Cart.CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update existing item
            Cart.CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.setUnitPrice(productResponse.getPrice());
            item.setProductName(productResponse.getProductName());
        } else {
            // Add new item
            Cart.CartItem newItem = new Cart.CartItem();
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(productResponse.getPrice());
            newItem.setProductName(productResponse.getProductName());
            cart.getItems().add(newItem);
        }

        // Recalculate total amount
        cart.setTotalAmount(cart.getItems().stream()
                .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                .sum());

        // Save and return updated cart
        return cartRepository.save(cart);
    }



    @Override
    public Cart removeProduct(Long cartId, String productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (!cart.getCartStatus().equals(CartStatus.ACTIVE)) {
            throw new RuntimeException("Cannot modify a non-active cart.");
        }

        cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        double totalAmount = calculateTotalAmount(cart);
        cart.setTotalAmount(totalAmount);

        return cartRepository.save(cart);
    }

    private double calculateTotalAmount(Cart cart) {
        return cart.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();
    }

}

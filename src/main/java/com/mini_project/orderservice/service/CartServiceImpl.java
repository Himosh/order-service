package com.mini_project.orderservice.service;

import com.mini_project.orderservice.model.Cart;
import com.mini_project.orderservice.model.dto.ProductResponse;
import com.mini_project.orderservice.model.enums.CartStatus;
import com.mini_project.orderservice.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductResponseService productResponseListener;

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

    // Create a new cart if the user doesn't have an active one
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
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (!cart.getCartStatus().equals(CartStatus.ACTIVE)) {
            throw new RuntimeException("Cannot modify a non-active cart.");
        }

        // Fetch product details
        ProductResponse productDetails = productResponseListener.getProductDetails(productId).join();

        Optional<Cart.CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            Cart.CartItem newItem = new Cart.CartItem();
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        // Update total amount
        cart.setTotalAmount(cart.getItems().stream()
                .mapToDouble(item -> productDetails.getPrice() * item.getQuantity())
                .sum());

        return cartRepository.save(cart);
    }


    @Override
    public Cart removeProduct(Long cartId, String productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (!cart.getCartStatus().equals(CartStatus.ACTIVE)) {
            throw new RuntimeException("Cannot modify a non-active cart.");
        }

        // Remove the product from the cart
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        // Recalculate the total amount
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

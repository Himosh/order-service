package com.mini_project.orderservice.service;

import com.mini_project.orderservice.model.Cart;
import com.mini_project.orderservice.model.enums.CartStatus;
import com.mini_project.orderservice.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;


@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private CartRepository cartRepository;

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

    public Cart updateCart(Cart cart) {
        if (!cart.getCartStatus().equals(CartStatus.ACTIVE)) {
            throw new RuntimeException("Cannot update a non-active cart.");
        }
        return cartRepository.save(cart);
    }

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
}

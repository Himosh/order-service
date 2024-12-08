package com.mini_project.orderservice.service;

import com.mini_project.orderservice.model.Cart;

import java.util.List;

public interface CartService {
    List<Cart> getAllCarts();
    Cart getCartById(Long cartId);
    Cart getCartByUserId(String userId);
    Cart createCart(String userId);
    Cart updateCart(Cart cart);
    void cancelCart(Long cartId);
    void clearCart(Long cartId);
    Cart addOrUpdateProduct(Long cartId, String productId, Integer quantity);
    Cart removeProduct(Long cartId, String productId);
}

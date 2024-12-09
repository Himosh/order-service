package com.mini_project.controller;

import com.mini_project.model.Cart;
import com.mini_project.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/user")
    @Operation(summary = "Get cart by user ID")
    public ResponseEntity<Cart> getCartByUserId(@RequestParam String userId) {
        try {
            Cart cart = cartService.getCartByUserId(userId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving cart: " + e.getMessage(), e);
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Get all carts")
    public ResponseEntity<List<Cart>> getAllCarts() {
        try {
            List<Cart> carts = cartService.getAllCarts();
            return ResponseEntity.ok(carts);
        } catch (Exception e) {
//            log.error("Error retrieving carts", e);
            throw new RuntimeException("Error retrieving carts: " + e.getMessage(), e);
        }
    }

    @PostMapping("create-cart/{userId}")
    @Operation(summary = "Create cart")
    public ResponseEntity<Cart> createCart(@PathVariable String userId) {
        try {
            Cart newCart = cartService.createCart(userId);
            return new ResponseEntity<>(newCart, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating cart", e);
            throw new RuntimeException("Error creating cart: " + e.getMessage(), e);
        }
    }

    @PatchMapping("/{cartId}/add")
    @Operation(summary = "Add product to cart")
    public ResponseEntity<Cart> addProductToCart(
            @PathVariable Long cartId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        try {
            Cart updatedCart = cartService.addOrUpdateProduct(cartId, productId, quantity);
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Error adding product to cart: " + e.getMessage());
        }
    }

    @PatchMapping("/{cartId}/remove")
    @Operation(summary = "Remove product from cart")
    public ResponseEntity<Cart> removeProductFromCart(
            @PathVariable Long cartId,
            @RequestParam String productId) {
        try {
            Cart updatedCart = cartService.removeProduct(cartId, productId);
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Error removing product from cart: " + e.getMessage());
        }
    }

}

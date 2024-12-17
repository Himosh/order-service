package com.mini_project.controller;

import com.mini_project.model.Cart;
import com.mini_project.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/cart")
public class CartController extends AbstractBaseController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/user")
    @Operation(summary = "Get cart by user ID")
    public ResponseEntity<Cart> getCartByUserId(@RequestParam String userId) {
        return handleCustomResponse(
                () -> cartService.getCartByUserId(userId),
                HttpStatus.OK,
                "Retrieving cart for userId: " + userId
        );
    }

    @GetMapping("/all")
    @Operation(summary = "Get all carts")
    public ResponseEntity<List<Cart>> getAllCarts() {
        return handleCustomResponse(
                cartService::getAllCarts,
                HttpStatus.OK,
                "Retrieving all carts"
        );
    }

    @PostMapping("/create-cart/{userId}")
    @Operation(summary = "Create cart")
    public ResponseEntity<Cart> createCart(@PathVariable String userId) {
        return handleCustomResponse(
                () -> cartService.createCart(userId),
                HttpStatus.CREATED,
                "Creating a new cart for userId: " + userId
        );
    }

    @PatchMapping("/add-or-update/{cartId}")
    @Operation(summary = "Add product to cart")
    public ResponseEntity<Cart> addProductToCart(
            @PathVariable Long cartId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        return handleCustomResponse(
                () -> cartService.addOrUpdateProduct(cartId, productId, quantity),
                HttpStatus.OK,
                "Adding/updating product " + productId + " in cart " + cartId
        );
    }

    @PatchMapping("/{cartId}/remove")
    @Operation(summary = "Remove product from cart")
    public ResponseEntity<Cart> removeProductFromCart(
            @PathVariable Long cartId,
            @RequestParam String productId) {
        return handleCustomResponse(
                () -> cartService.removeProduct(cartId, productId),
                HttpStatus.OK,
                "Removing product " + productId + " from cart " + cartId
        );
    }
}

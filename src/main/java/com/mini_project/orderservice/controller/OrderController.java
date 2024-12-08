package com.mini_project.orderservice.controller;

import com.mini_project.orderservice.model.Cart;
import com.mini_project.orderservice.model.Order;
import com.mini_project.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/all")
    @Operation(summary = "Get all orders")
    public ResponseEntity<Page<Order>> getAllOrders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            Page<Order> orders = orderService.getAllOrders(PageRequest.of(page, size));
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error retrieving orders", e);
            throw new RuntimeException("Error retrieving orders: " + e.getMessage(), e);
        }
    }

    @GetMapping("/user")
    @Operation(summary = "Get orders by user ID")
    public ResponseEntity<Page<Order>> getOrdersByUserId(
            @RequestParam String userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            Page<Order> orders = orderService.getOrdersByUserId(userId, PageRequest.of(page, size));
            if (orders.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Page.empty());
            }
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error retrieving orders by user", e);
            throw new RuntimeException("Error retrieving orders by user: " + e.getMessage(), e);
        }
    }

    @GetMapping("/order")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<Order> getOrderById(@RequestParam Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Error retrieving order", e);
            throw new RuntimeException("Error retrieving order: " + e.getMessage(), e);
        }
    }

    @PostMapping("/create")
    @Operation(summary = "Create order from cart")
    public ResponseEntity<Order> createOrderFromCart(@RequestBody Cart cart) {
        try {
            Order order = orderService.createOrderFromCart(cart);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (Exception e) {
            log.error("Error creating order", e);
            throw new RuntimeException("Error creating order: " + e.getMessage(), e);
        }
    }
}

package com.mini_project.controller;

import com.mini_project.model.Order;
import com.mini_project.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/order")
public class OrderController extends AbstractBaseController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Order>> getAllOrders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return handleCustomResponse(
                () -> orderService.getAllOrders(PageRequest.of(page, size)),
                HttpStatus.OK,
                "Retrieving all orders with pagination"
        );
    }

    @GetMapping("/user")
    public ResponseEntity<Page<Order>> getOrdersByUserId(
            @RequestParam String userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return handleCustomResponse(
                () -> {
                    Page<Order> orders = orderService.getOrdersByUserId(userId, PageRequest.of(page, size));
                    if (orders.isEmpty()) {
                        log.warn("No orders found for user ID: {}", userId);
                        return Page.empty();
                    }
                    return orders;
                },
                HttpStatus.OK,
                "Retrieving orders for user ID: " + userId
        );
    }

    @GetMapping("/order")
    public ResponseEntity<Order> getOrderById(@RequestParam Long orderId) {
        return handleCustomResponse(
                () -> orderService.getOrderById(orderId),
                HttpStatus.OK,
                "Retrieving order with ID: " + orderId
        );
    }

    @PostMapping("/create-order-from-cart/{cartId}")
    public ResponseEntity<Order> createOrderFromCart(@PathVariable Long cartId) {
        return handleCustomResponse(
                () -> orderService.createOrderFromCart(cartId),
                HttpStatus.CREATED,
                "Creating order from cart ID: " + cartId
        );
    }
}

package com.mini_project.orderservice.service;

import com.mini_project.orderservice.model.Cart;
import com.mini_project.orderservice.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<Order> getAllOrders(Pageable pageable);
    Order getOrderById(Long orderId);
    Order createOrderFromCart(Cart cart);
    Page<Order> getOrdersByUserId(String userId, Pageable pageable);
}

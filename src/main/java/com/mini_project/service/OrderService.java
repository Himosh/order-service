package com.mini_project.service;

import com.mini_project.model.Cart;
import com.mini_project.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<Order> getAllOrders(Pageable pageable);
    Order getOrderById(Long orderId);
    Order createOrderFromCart(Long CartId);
    Page<Order> getOrdersByUserId(String userId, Pageable pageable);
}

package com.mini_project.repository;

import com.mini_project.model.Cart;
import com.mini_project.model.enums.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(String userId);
    Optional<Cart> findByUserIdAndCartStatus(String userId, CartStatus cartStatus);
}

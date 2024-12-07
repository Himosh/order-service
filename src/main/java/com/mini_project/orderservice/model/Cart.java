package com.mini_project.orderservice.model;

import com.mini_project.orderservice.model.enums.CartStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    private String userId;

    @ElementCollection
    private List<CartItem> items = new ArrayList<>();

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private CartStatus cartStatus;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class CartItem {
        private String productId;
        private Integer quantity;
    }

}

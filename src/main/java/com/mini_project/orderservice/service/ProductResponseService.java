package com.mini_project.orderservice.service;

import com.mini_project.orderservice.model.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductResponseService {

    @Autowired
    private ProductRequestService productRequestService;

    private Map<String, CompletableFuture<ProductResponse>> pendingRequests = new ConcurrentHashMap<>();

    private static final String RESPONSE_TOPIC = "product-response-topic";

    @KafkaListener(topics = RESPONSE_TOPIC, groupId = "order-service-group")
    public void consumeProductDetails(ProductResponse response) {
        System.out.println("Received product details: " + response.getProductName());
        // Resolve the CompletableFuture associated with the requestId
        CompletableFuture<ProductResponse> future = pendingRequests.remove(response.getRequestId());
        if (future != null) {
            future.complete(response);
        }
    }

    public CompletableFuture<ProductResponse> getProductDetails(Long productId) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<ProductResponse> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);

        new Thread(() -> {
            productRequestService.requestProductDetails(requestId, productId);
        }).start();

        return future;
    }
}


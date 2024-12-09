package com.mini_project.service;

import com.mini_project.model.dto.ProductRequest;
import com.mini_project.model.dto.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ProductResponseService {

    @Autowired
    private ProductRequestService productRequestService;

    private final Map<String, CompletableFuture<ProductResponse>> pendingRequests = new ConcurrentHashMap<>();

    private static final String RESPONSE_TOPIC = "product-response-topic-new";

    @KafkaListener(topics = RESPONSE_TOPIC, groupId = "order-service-group")
    public void consumeProductDetails(ProductResponse response) {
        System.out.println("Received product details: " + response.getProductName());
        log.info("Received product details: {}", response.getProductName());
        CompletableFuture<ProductResponse> future = pendingRequests.remove(response.getRequestId());
        if (future != null) {
            future.complete(response);
        }
    }

    public CompletableFuture<ProductResponse> getProductDetails(Long productId) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<ProductResponse> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);

        ProductRequest request = new ProductRequest();
        request.setRequestId(requestId);
        request.setProductId(productId);

        new Thread(() -> {
            productRequestService.requestProductDetails(request);
        }).start();

        return future;
    }
}


package com.mini_project.orderservice.service;

import com.mini_project.orderservice.model.dto.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductRequestService {

    @Autowired
    private KafkaTemplate<String, ProductRequest> kafkaTemplate;

    private static final String REQUEST_TOPIC = "product-request-topic";

    public void requestProductDetails(String requestId, Long productId) {
        ProductRequest request = new ProductRequest(requestId, productId);
        kafkaTemplate.send(REQUEST_TOPIC, request);
        System.out.println("Requested product details for productId: " + productId + " with requestId: " + requestId);
    }
}


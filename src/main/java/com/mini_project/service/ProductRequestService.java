package com.mini_project.service;

import com.mini_project.model.dto.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ProductRequestService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String REQUEST_TOPIC = "product-request-topic-new";

    public void requestProductDetails(ProductRequest request) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(REQUEST_TOPIC, request);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Sent message=[" + request.toString() +
                            "] with offset=[" + result.getRecordMetadata().offset() + "]");
                } else {
                    System.out.println("Unable to send message=[" +
                            request.toString() + "] due to : " + ex.getMessage());
                }
            });

        } catch (Exception ex) {
            System.out.println("ERROR : "+ ex.getMessage());
        }
    }
}


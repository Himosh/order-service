package com.mini_project.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("productId")
    private Long productId;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("productCategory")
    private String productCategory;

    @JsonProperty("supplierId")
    private String supplierId;
}

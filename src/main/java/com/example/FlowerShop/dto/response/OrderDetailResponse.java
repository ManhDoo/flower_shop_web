package com.example.FlowerShop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderDetailResponse {
    private Long productId;
    private String productName;
    private int quantity;
    private double price;
}

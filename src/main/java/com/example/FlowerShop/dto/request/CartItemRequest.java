package com.example.FlowerShop.dto.request;

import lombok.Data;

@Data
public class CartItemRequest {
    private Long cartItemId;
    private int quantity;
}
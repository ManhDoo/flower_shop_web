package com.example.FlowerShop.dto.response;

import com.example.FlowerShop.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private Long id;
    private Long userId;
    private List<CartItemResponse> cartItemResponses;
}

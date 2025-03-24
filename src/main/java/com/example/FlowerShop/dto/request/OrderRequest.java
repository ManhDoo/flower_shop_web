package com.example.FlowerShop.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private Long userId;

    @NotEmpty(message = "Order details list cannot be blank")
    @Valid
    private List<OrderDetailRequest> orderDetails;

    private String status;
}

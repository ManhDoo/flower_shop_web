package com.example.FlowerShop.dto.request;

import com.example.FlowerShop.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusRequest {
    @NotNull(message = "Status cannot be blank")
    private OrderStatus status;
}

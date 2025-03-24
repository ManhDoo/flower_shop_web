package com.example.FlowerShop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderDetailRequest {
    @NotNull(message = "Product ID cannot be blank")
    private Long productId;

    @NotNull(message = "Quantity cannot be blank")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;

    @Min(value = 0, message = "Price cannot be negative number")
    private Double price;
}

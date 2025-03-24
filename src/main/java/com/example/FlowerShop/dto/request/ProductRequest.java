package com.example.FlowerShop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name cannot be blank")
    @Size(max = 100, message = "Product name cannot be longer than 100 characters")
    private String name;

    @NotNull(message = "Product price cannot be blank")
    @PositiveOrZero(message = "Product price cannot be negative number")
    private double price;

    @Size(max = 500, message = "Description cannot be longer than 500 characters")
    private String description;

    @NotNull(message = "Inventory cannot be blank")
    @PositiveOrZero(message = "Inventory cannot be black")
    private int stock;
}

package com.example.FlowerShop.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private int phone;
    private String address;
    private String email;
    private String password;
    private String role;
}
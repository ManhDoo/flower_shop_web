package com.example.FlowerShop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private double total_price;
    private String name_of_receipt;
    private String place_of_receipt;
    private int phone_of_receipt;
    private LocalDateTime create_at;
    private String status;
    private Long userId;
    private String username;
    List<OrderDetailResponse>orderDetailResponsesList;
    private int total_quantity_order;
    private String payMethod;

    public OrderResponse() {
    }

    public OrderResponse(Long id, double total_price, LocalDateTime create_at, String status,
                         Long userId, String userName, String name_of_receipt, String place_of_receipt, int phone_of_receipt,
                         List<OrderDetailResponse> orderDetailResponsesList, int total_quantity_order, String payMethod) {
        this.id = id;
        this.total_price = total_price;
        this.create_at = create_at;
        this.status = status;
        this.userId = userId;
        this.username = userName;
        this.name_of_receipt = name_of_receipt;
        this.phone_of_receipt = phone_of_receipt;
        this.place_of_receipt = place_of_receipt;
        this.orderDetailResponsesList = orderDetailResponsesList;
        this.total_quantity_order = total_quantity_order;
        this.payMethod = payMethod;
    }

    public OrderResponse(Long id, double total_price, LocalDateTime create_at, String status,
                         Long userId, String userName, String name_of_receipt, String place_of_receipt, int phone_of_receipt, int total_quantity_order, String payMethod) {
        this.id = id;
        this.total_price = total_price;
        this.create_at = create_at;
        this.status = status;
        this.userId = userId;
        this.username = userName;
        this.name_of_receipt = name_of_receipt;
        this.place_of_receipt = place_of_receipt;
        this.phone_of_receipt = phone_of_receipt;
        this.total_quantity_order = total_quantity_order;
        this.payMethod = payMethod;
    }
}

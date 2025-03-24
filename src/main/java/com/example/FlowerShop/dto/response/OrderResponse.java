package com.example.FlowerShop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private double total_price;
    private LocalDateTime create_at;
    private String status;

    List<OrderDetailResponse>orderDetailResponsesList;

    public OrderResponse(Long id, double total_price, LocalDateTime create_at, String status, List<OrderDetailResponse> orderDetailResponsesList) {
        this.id = id;
        this.total_price = total_price;
        this.create_at = create_at;
        this.status = status;
        this.orderDetailResponsesList = orderDetailResponsesList;
    }

    public OrderResponse() {
    }

    public OrderResponse(Long id, double total_price, LocalDateTime create_at, String status) {
        this.id = id;
        this.total_price = total_price;
        this.create_at = create_at;
        this.status = status;
    }
}

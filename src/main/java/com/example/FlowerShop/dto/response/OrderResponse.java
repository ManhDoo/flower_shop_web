package com.example.FlowerShop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private double total_price;
    private String place_of_receipt;
    private int phone_of_receipt;
    private LocalDateTime create_at;
    private String status;
    private Long userId;
    private String username;
    List<OrderDetailResponse>orderDetailResponsesList;
    private int total_quantity_order;
    private String payMethod;

//    public OrderResponse(Long id, double total_price, LocalDateTime create_at, String status, List<OrderDetailResponse> orderDetailResponsesList) {
//        this.id = id;
//        this.total_price = total_price;
//        this.create_at = create_at;
//        this.status = status;
//        this.orderDetailResponsesList = orderDetailResponsesList;
//    }

    public OrderResponse() {
    }

    public OrderResponse(Long id, double total_price, LocalDateTime create_at, String status,
                         Long userId, String userName, String place_of_receipt, int phone_of_receipt,
                         List<OrderDetailResponse> orderDetailResponsesList, int total_quantity_order, String payMethod) {
        this.id = id;
        this.total_price = total_price;
        this.create_at = create_at;
        this.status = status;
        this.userId = userId;
        this.username = userName;
        this.phone_of_receipt = phone_of_receipt;
        this.place_of_receipt = place_of_receipt;
        this.orderDetailResponsesList = orderDetailResponsesList;
        this.total_quantity_order = total_quantity_order;
        this.payMethod = payMethod;
    }

    // Constructor không có orderDetailResponsesList (dùng trong trường hợp không cần chi tiết đơn hàng)
    public OrderResponse(Long id, double total_price, LocalDateTime create_at, String status,
                         Long userId, String userName, String place_of_receipt, int phone_of_receipt, int total_quantity_order, String payMethod) {
        this.id = id;
        this.total_price = total_price;
        this.create_at = create_at;
        this.status = status;
        this.userId = userId;
        this.username = userName;
        this.place_of_receipt = place_of_receipt;
        this.phone_of_receipt = phone_of_receipt;
        this.total_quantity_order = total_quantity_order;
        this.payMethod = payMethod;
    }
}

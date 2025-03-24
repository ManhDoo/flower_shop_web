package com.example.FlowerShop.model;

public enum OrderStatus {
    PENDING_CONFIRMATION, // Chờ xác nhận
    CONFIRMED,            // Đã xác nhận
    PREPARING_ORDER,      // Đang chuẩn bị hàng
    HANDED_OVER,          // Đã bàn giao cho đơn vị vận chuyển
    IN_TRANSIT,           // Đang vận chuyển
    ARRIVED,              // Đã đến điểm giao
    DELIVERED,            // Giao hàng thành công
    DELIVERY_FAILED,      // Giao hàng thất bại
    RETURNING,            // Đang hoàn hàng
    RETURN_COMPLETED,     // Hoàn tất hoàn hàng
    CANCELLED             // Đã hủy
}

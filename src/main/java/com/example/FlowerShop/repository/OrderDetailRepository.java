package com.example.FlowerShop.repository;

import com.example.FlowerShop.model.Order;
import com.example.FlowerShop.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    void deleteByOrder(Order order);
    List<OrderDetail> findAllByOrder(Order order);
}

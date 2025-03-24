package com.example.FlowerShop.repository;

import com.example.FlowerShop.model.Order;
import com.example.FlowerShop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUser(User user);
}

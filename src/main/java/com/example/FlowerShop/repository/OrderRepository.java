package com.example.FlowerShop.repository;

import com.example.FlowerShop.model.Order;
import com.example.FlowerShop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.user = :user ORDER BY o.create_at DESC")
    List<Order> findAllByUser(User user);
}

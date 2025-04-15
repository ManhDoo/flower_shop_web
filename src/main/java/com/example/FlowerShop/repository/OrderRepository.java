package com.example.FlowerShop.repository;

import com.example.FlowerShop.model.Order;
import com.example.FlowerShop.model.OrderStatus;
import com.example.FlowerShop.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.user = :user ORDER BY o.create_at DESC")
    Page<Order> findAllByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.create_at DESC")
    Page<Order> findByStatus(@Param("status") OrderStatus status, Pageable pageable);

    Page<Order> findAll(Pageable pageable);

}

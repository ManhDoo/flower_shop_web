package com.example.FlowerShop.repository;

import com.example.FlowerShop.model.Cart;
import com.example.FlowerShop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}

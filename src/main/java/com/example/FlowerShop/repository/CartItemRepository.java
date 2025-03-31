package com.example.FlowerShop.repository;

import com.example.FlowerShop.model.Cart;
import com.example.FlowerShop.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
}

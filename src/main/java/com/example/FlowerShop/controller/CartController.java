package com.example.FlowerShop.controller;

import com.example.FlowerShop.dto.response.CartResponse;
import com.example.FlowerShop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @RequestHeader("Authorization") String token,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        cartService.addToCart(token, productId, quantity);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Product added to cart"));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(
            @RequestHeader("Authorization") String token,
            @RequestParam Long cartItemId) {
        cartService.removeFromCart(token, cartItemId);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Product removed from cart"));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(@RequestHeader("Authorization") String token) {
        List<CartResponse> cartResponses = cartService.getCartItems(token);
        return ResponseEntity.ok(Map.of("status", "success", "data", cartResponses));
    }
}

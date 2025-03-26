package com.example.FlowerShop.controller;

import com.example.FlowerShop.config.JwtUtil;
import com.example.FlowerShop.dto.request.OrderRequest;
import com.example.FlowerShop.dto.response.OrderResponse;
import com.example.FlowerShop.exception.ResourceNotFoundException;
import com.example.FlowerShop.model.Order;
import com.example.FlowerShop.model.OrderStatus;
import com.example.FlowerShop.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody OrderRequest req, BindingResult result) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);
        req.setUserId(userId);
        OrderResponse order = orderService.createOrder(token, req);

        Map<String, Object> response = Map.of(
                "status", "success",
                "message", "Order successful",
                "data", order
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @GetMapping("/all/orders")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Get order successful",
                "data", orders
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Long id) {
        List<OrderResponse> orders = orderService.getOrderById(id);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Get order successful",
                "data", orders
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateOrder(@RequestHeader("Authorization") String token, @PathVariable Long id, @Valid @RequestBody OrderRequest req, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", result.getAllErrors().get(0).getDefaultMessage(),
                    "data", null
            ));
        }

        Order updatedOrder = orderService.updateOrder(token, id, req);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Update order successful",
                "data", updatedOrder
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Delete order successful",
                "data", null
        ));
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserCart(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserId(token.substring(7));
        List<OrderResponse> orders = orderService.getAllOrdersByUser(userId);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Get orders list successful",
                "data", orders
        ));
    }


    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus newStatus) {

        OrderResponse updatedOrder = orderService.updateOrderStatus(orderId, newStatus);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Order status updated successfully",
                "data", updatedOrder
        ));
    }
}

package com.example.FlowerShop.controller;

import com.example.FlowerShop.config.JwtUtil;
import com.example.FlowerShop.dto.request.OrderRequest;
import com.example.FlowerShop.dto.response.OrderResponse;
import com.example.FlowerShop.model.Order;
import com.example.FlowerShop.service.OrderService;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra lỗi validation
        if (result.hasErrors()) {
            List<String> errorMessages = result.getAllErrors().stream()
                    .map(error -> {
                        if (error instanceof FieldError) {
                            FieldError fieldError = (FieldError) error;
                            return fieldError.getField() + ": " + error.getDefaultMessage();
                        }
                        return error.getDefaultMessage();
                    })
                    .toList();
            response.put("status", "error");
            response.put("message", "Invalid data");
            response.put("errors", errorMessages); // Trả về danh sách lỗi
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.extractUserId(token);

            req.setUserId(userId);
            OrderResponse order = orderService.createOrder(token, req);
            response.put("status", "success");
            response.put("message", "Order successful");
            response.put("data", order);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all/orders")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllOrders() {
        Map<String, Object> response = new HashMap<>();
        List<Order> orders = orderService.getAllOrders();
        response.put("status", "success");
        response.put("message", "Get order successful");
        response.put("data", orders);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Order> order = orderService.getOrderById(id);
        if (order.isPresent()) {
            response.put("status", "success");
            response.put("message", "Get order successful");
            response.put("data", order.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Order not found");
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateOrder(@RequestHeader("Authorization") String token, @PathVariable Long id, @Valid @RequestBody OrderRequest req, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            response.put("status", "error");
            response.put("message", result.getAllErrors().get(0).getDefaultMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            Order updatedOrder = orderService.updateOrder(token, id, req);
            response.put("status", "success");
            response.put("message", "Update order successful");
            response.put("data", updatedOrder);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            orderService.deleteOrder(id);
            response.put("status", "success");
            response.put("message", "Delete order successful");
            response.put("data", null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", "Order not found");
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserCart(@RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String jwt = token.substring(7); // Bỏ "Bearer "
            Long userId = jwtUtil.extractUserId(jwt);
            List<OrderResponse> orders = orderService.getAllOrdersByUser(userId);
            response.put("status", "success");
            response.put("message", "Get orders list successful");
            response.put("data", orders);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}

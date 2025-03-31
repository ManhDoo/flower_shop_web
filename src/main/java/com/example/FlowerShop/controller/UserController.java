package com.example.FlowerShop.controller;

import com.example.FlowerShop.config.JwtUtil;
import com.example.FlowerShop.dto.response.UserResponse;
import com.example.FlowerShop.model.User;
import com.example.FlowerShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

//    @GetMapping
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
//    public ResponseEntity<List<User>> getAllUsers() {
//        return ResponseEntity.ok(userService.getAllUsers());
//    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserById(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserId(token.substring(7));
        List<UserResponse> userResponses = userService.getUserById(userId);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Get User information successful",
                "data", userResponses)
        );
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<UserResponse> updateUser(@RequestHeader("Authorization") String token, @RequestBody User updatedUser) {
        Long userId = jwtUtil.extractUserId(token.substring(7));
        UserResponse userResponse = userService.updateUser(userId, updatedUser);
        return ResponseEntity.ok(userResponse);
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        String result = userService.deleteUser(id);
        if ("User not found".equals(result)) {
            return ResponseEntity.status(404).body(result);
        }
        return ResponseEntity.ok(result);
    }
}

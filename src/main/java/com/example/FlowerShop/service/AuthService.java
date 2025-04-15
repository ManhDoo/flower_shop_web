package com.example.FlowerShop.service;

import com.example.FlowerShop.config.JwtUtil;
import com.example.FlowerShop.dto.request.LoginRequest;
import com.example.FlowerShop.dto.request.RegisterRequest;
import com.example.FlowerShop.dto.response.AuthResponse;
import com.example.FlowerShop.exception.EmailAlreadyExistsException;
import com.example.FlowerShop.exception.InvalidCredentialsException;
import com.example.FlowerShop.model.User;
import com.example.FlowerShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : "USER");
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token, "User registered successfully", user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        User user = userOpt.get();
        String token = jwtUtil.generateToken(user.getId() ,user.getEmail(), user.getRole());
        return new AuthResponse(token, "Login successful", user.getRole());
    }
}

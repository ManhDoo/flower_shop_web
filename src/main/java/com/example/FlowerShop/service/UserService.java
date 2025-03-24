package com.example.FlowerShop.service;

import com.example.FlowerShop.model.User;
import com.example.FlowerShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(Long id, User updatedUser) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOpt.get();
        user.setName(updatedUser.getName());
        user.setPhone(updatedUser.getPhone());
        user.setAddress(updatedUser.getAddress());
        user.setEmail(updatedUser.getEmail());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(updatedUser.getPassword()); // Nên mã hóa nếu cần
        }
        return userRepository.save(user);
    }

    public String deleteUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return "User not found";
        }
        userRepository.deleteById(id);
        return "User deleted successfully";
    }
}

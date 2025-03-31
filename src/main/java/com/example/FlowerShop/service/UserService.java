package com.example.FlowerShop.service;

import com.example.FlowerShop.dto.response.UserResponse;
import com.example.FlowerShop.model.User;
import com.example.FlowerShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

//    public Optional<User> getUserById(Long id) {
//
//        return userRepository.findById(id);
//    }

    public List<UserResponse> getUserById(Long id) {

        return userRepository.findById(id)
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getPhone(),
                        user.getAddress(),
                        user.getEmail()
                )).collect(Collectors.toList());
    }

    public UserResponse updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Chỉ cập nhật các trường name, phone, address
        user.setName(updatedUser.getName());
        user.setPhone(updatedUser.getPhone());
        user.setAddress(updatedUser.getAddress());

        userRepository.save(user);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getPhone(),
                user.getAddress(),
                user.getEmail()
        );
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

package com.example.FlowerShop.service;

import com.example.FlowerShop.config.JwtUtil;
import com.example.FlowerShop.dto.response.CartItemResponse;
import com.example.FlowerShop.dto.response.CartResponse;
import com.example.FlowerShop.model.Cart;
import com.example.FlowerShop.model.CartItem;
import com.example.FlowerShop.model.Product;
import com.example.FlowerShop.model.User;
import com.example.FlowerShop.repository.CartItemRepository;
import com.example.FlowerShop.repository.CartRepository;
import com.example.FlowerShop.repository.ProductRepository;
import com.example.FlowerShop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    @Autowired
    private JwtUtil jwtUtil;

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    public Cart getCartByUser(User user){
        return cartRepository.findByUser(user).orElseGet(()->{
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(new Cart());
        });
    }
    public Cart addToCart(String token, Long productId, int quantity) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.extractUserId(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = getCartByUser(user);
        if (cart == null) {
            cart = new Cart();

            cart.setItems(new ArrayList<>());
        }
        cart.setUser(user);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));


        CartItem cartItem = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (cartItem == null) {
            // Nếu chưa có, thêm mới
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cart.getItems().add(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        cartItemRepository.save(cartItem);
        cartRepository.save(cart);

        return cart;
    }

    public void removeFromCart(String token, Long cartItemId) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.extractUserId(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this item");
        }

        cartItemRepository.delete(cartItem);
    }

    public List<CartResponse> getCartItems(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartRepository.findByUser(user).stream()
                .map(cart -> new CartResponse(
                        cart.getId(),
                        cart.getUser().getId(),
                        cartItemRepository.findByCart(cart).stream()
                                .map(cartItem -> new CartItemResponse(
                                        cartItem.getId(),
                                        cartItem.getProduct().getId(),
                                        cartItem.getProduct().getName(),
                                        cartItem.getProduct().getImage(),
                                        cartItem.getProduct().getPrice(),
                                        cartItem.getQuantity()
                                )).collect(Collectors.toList())
                )).collect(Collectors.toList());
    }
}

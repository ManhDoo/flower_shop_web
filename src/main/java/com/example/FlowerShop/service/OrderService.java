package com.example.FlowerShop.service;

import com.example.FlowerShop.config.JwtUtil;
import com.example.FlowerShop.dto.request.OrderDetailRequest;
import com.example.FlowerShop.dto.request.OrderRequest;
import com.example.FlowerShop.dto.response.OrderDetailResponse;
import com.example.FlowerShop.dto.response.OrderResponse;
import com.example.FlowerShop.exception.ResourceNotFoundException;
import com.example.FlowerShop.model.*;
import com.example.FlowerShop.repository.OrderDetailRepository;
import com.example.FlowerShop.repository.OrderRepository;
import com.example.FlowerShop.repository.ProductRepository;
import com.example.FlowerShop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JwtUtil jwtUtil;



    @Transactional
    public OrderResponse createOrder(String token, OrderRequest req) {
        Long user_id = jwtUtil.extractUserId(token);

        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setCreate_at(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order = orderRepository.save(order);

        double totalPrice = processNewOrderDetails(order, req);

        order.setTotal_price(totalPrice);
        orderRepository.save(order);

        return new OrderResponse(order.getId(), order.getTotal_price(), order.getCreate_at(), order.getStatus().name(), order.getUser().getId(), order.getUser().getName());
    }
    private void restockProducts(Order order) {
        List<OrderDetail> oldOrderDetails = orderDetailRepository.findAllByOrder(order);
        for (OrderDetail detail : oldOrderDetails) {
            Product product = detail.getProduct();
            product.setStock(product.getStock() + detail.getQuantity());
            productRepository.save(product);
        }
    }

    private double processNewOrderDetails(Order order, OrderRequest req) {
        double totalPrice = 0;

        // Lấy danh sách Product một lần, tránh query DB nhiều lần
        Map<Long, Product> productMap = productRepository.findAllById(
                req.getOrderDetails().stream().map(OrderDetailRequest::getProductId).toList()
        ).stream().collect(Collectors.toMap(Product::getId, product -> product));

        for (OrderDetailRequest detailReq : req.getOrderDetails()) {
            Product product = productMap.get(detailReq.getProductId());

            if (product == null) {
                throw new RuntimeException("Product not found with ID: " + detailReq.getProductId());
            }

            if (product.getStock() < detailReq.getQuantity()) {
                throw new RuntimeException(product.getName() + " Insufficient stock. Remaining: " + product.getStock());
            }

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(detailReq.getQuantity());
            orderDetail.setPrice(product.getPrice());
            orderDetailRepository.save(orderDetail);

            // Trừ tồn kho
            product.setStock(product.getStock() - detailReq.getQuantity());
            productRepository.save(product);

            totalPrice += product.getPrice() * detailReq.getQuantity();
        }

        return totalPrice;
    }


    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> new OrderResponse(

                        order.getId(),
                        order.getTotal_price(),
                        order.getCreate_at(),
                        order.getStatus().name(),
                        order.getUser().getId(),
                        order.getUser().getName(),
                        orderDetailRepository.findAllByOrder(order).stream()
                                .map(detail -> new OrderDetailResponse(
                                        detail.getProduct().getId(),
                                        detail.getProduct().getName(),
                                        detail.getProduct().getImage(),
                                        detail.getQuantity(),
                                        detail.getPrice()
                                )).collect(Collectors.toList())
                )).collect(Collectors.toList());
    }

    public List<OrderResponse> getOrderById(Long id) {
        return orderRepository.findById(id)
                .stream()
                .map(order -> new OrderResponse(

                        order.getId(),
                        order.getTotal_price(),
                        order.getCreate_at(),
                        order.getStatus().name(),
                        order.getUser().getId(),
                        order.getUser().getName(),
                        orderDetailRepository.findAllByOrder(order).stream()
                                .map(detail -> new OrderDetailResponse(
                                        detail.getProduct().getId(),
                                        detail.getProduct().getName(),
                                        detail.getProduct().getImage(),
                                        detail.getQuantity(),
                                        detail.getPrice()
                                )).collect(Collectors.toList())
                )).collect(Collectors.toList());
    }


    public Order updateOrder(String token, Long id, OrderRequest req) {
        Long user_id = jwtUtil.extractUserId(token);
        // Lấy Order & User từ DB
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        order.setUser(user);

        restockProducts(order);

        orderDetailRepository.deleteByOrder(order);

        double totalPrice = processNewOrderDetails(order, req);

        order.setTotal_price(totalPrice);
        if (req.getStatus() != null) {
            try {
                order.setStatus(OrderStatus.valueOf(req.getStatus())); // Chuyển từ String → Enum
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid order status: " + req.getStatus());
            }
        }
        return orderRepository.save(order);
    }


    @Transactional
    public void deleteOrder(Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found");
        }
        Order order = orderOpt.get();
        List<OrderDetail> details = orderDetailRepository.findAllByOrder(order);
        for (OrderDetail detail : details) {
            Product product = detail.getProduct();
            product.setStock(product.getStock() + detail.getQuantity()); // Hoàn lại stock
            productRepository.save(product);
        }
        orderDetailRepository.deleteByOrder(order);
        orderRepository.delete(order);
    }
    public List<OrderResponse> getAllOrdersByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findAllByUser(user)
                .stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getTotal_price(),
                        order.getCreate_at(),
                        order.getStatus().name(),
                        order.getUser().getId(),
                        order.getUser().getName(),
                        orderDetailRepository.findAllByOrder(order).stream()
                                .map(detail -> new OrderDetailResponse(
                                        detail.getProduct().getId(),
                                        detail.getProduct().getName(),
                                        detail.getProduct().getImage(),
                                        detail.getQuantity(),
                                        detail.getPrice()
                                )).collect(Collectors.toList())
                )).collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        // Kiểm tra nếu trạng thái mới hợp lệ
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update a cancelled order!");
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        // Chuyển đổi sang OrderResponse giống getOrderById()
        return new OrderResponse(
                updatedOrder.getId(),
                updatedOrder.getTotal_price(),
                updatedOrder.getCreate_at(),
                updatedOrder.getStatus().name(),
                updatedOrder.getUser().getId(),
                updatedOrder.getUser().getName(),
                orderDetailRepository.findAllByOrder(updatedOrder).stream()
                        .map(detail -> new OrderDetailResponse(
                                detail.getProduct().getId(),
                                detail.getProduct().getName(),
                                detail.getProduct().getImage(),
                                detail.getQuantity(),
                                detail.getPrice()
                        )).collect(Collectors.toList())
        );
    }
}

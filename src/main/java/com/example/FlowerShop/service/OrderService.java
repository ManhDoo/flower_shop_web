package com.example.FlowerShop.service;

import com.example.FlowerShop.config.JwtUtil;
import com.example.FlowerShop.dto.request.OrderDetailRequest;
import com.example.FlowerShop.dto.request.OrderRequest;
import com.example.FlowerShop.dto.response.OrderDetailResponse;
import com.example.FlowerShop.dto.response.OrderResponse;
import com.example.FlowerShop.exception.ResourceNotFoundException;
import com.example.FlowerShop.model.*;
import com.example.FlowerShop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final CartService cartService;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;


//    @Transactional
//    public OrderResponse createOrder(String token, OrderRequest req) {
//        Long user_id = jwtUtil.extractUserId(token);
//
//        User user = userRepository.findById(user_id)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Order order = new Order();
//        order.setUser(user);
//        order.setCreate_at(LocalDateTime.now());
//        order.setPlace_of_receipt(req.getPlace_of_receipt());
//        order.setPhone_of_receipt(req.getPhone_of_receipt());
//        order.setStatus(OrderStatus.PENDING);
//        order.setPayMethod(req.getPayMethod());
//        order = orderRepository.save(order);
//
//        double totalPrice = processNewOrderDetails(order, req);
//
//        order.setTotal_price(totalPrice);
//        orderRepository.save(order);
//
//        return new OrderResponse(order.getId(), order.getTotal_price(), order.getCreate_at(), order.getStatus().name(), order.getUser().getId(), order.getUser().getName(), order.getPlace_of_receipt(), order.getPhone_of_receipt(), order.getPayMethod().name());
//    }

//    @Transactional
//    public OrderResponse createOrder(String token, OrderRequest req) {
//        Long user_id = jwtUtil.extractUserId(token);
//        User user = userRepository.findById(user_id)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Cart cart = cartService.getCartByUser(user);
//        if (cart.getItems().isEmpty()) {
//            throw new RuntimeException("Cart is empty");
//        }
//
//        Order order = new Order();
//        order.setUser(user);
//        order.setCreate_at(LocalDateTime.now());
//        order.setPlace_of_receipt(req.getPlace_of_receipt());
//        order.setPhone_of_receipt(req.getPhone_of_receipt());
//        order.setStatus(OrderStatus.PENDING);
//        order.setPayMethod(req.getPayMethod());
//        order = orderRepository.save(order);
//
//        double totalPrice = 0;
//        for (CartItem cartItem : cart.getItems()) {
//            OrderDetail orderDetail = new OrderDetail();
//            orderDetail.setOrder(order);
//            orderDetail.setProduct(cartItem.getProduct());
//            orderDetail.setQuantity(cartItem.getQuantity());
//            orderDetail.setPrice(cartItem.getProduct().getPrice());
//            orderDetailRepository.save(orderDetail);
//
//            totalPrice += cartItem.getProduct().getPrice() * cartItem.getQuantity();
//
//            // Giảm stock sản phẩm
//            Product product = cartItem.getProduct();
//            product.setStock(product.getStock() - cartItem.getQuantity());
//            productRepository.save(product);
//        }
//
//        order.setTotal_price(totalPrice);
//        orderRepository.save(order);
//
//        cartItemRepository.deleteAll(cart.getItems());
//
//        return new OrderResponse(order.getId(), order.getTotal_price(), order.getCreate_at(),
//                order.getStatus().name(), order.getUser().getId(), order.getUser().getName(),
//                order.getPlace_of_receipt(), order.getPhone_of_receipt(), order.getPayMethod().name());
//    }

    @Transactional
    public OrderResponse createOrder(String token, OrderRequest req) {
        Long userId = jwtUtil.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartService.getCartByUser(user);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<CartItem> selectedItems = cart.getItems().stream()
                .filter(cartItem -> req.getCartItemIds().contains(cartItem.getId()))
                .collect(Collectors.toList());

        if (selectedItems.isEmpty()) {
            throw new RuntimeException("No valid cart items selected for order");
        }

        // Tạo Order
        Order order = new Order();
        order.setUser(user);
        order.setCreate_at(LocalDateTime.now());
        order.setPlace_of_receipt(req.getPlace_of_receipt());
        order.setPhone_of_receipt(req.getPhone_of_receipt());
        order.setStatus(OrderStatus.PENDING);
        order.setPayMethod(req.getPayMethod());
        order = orderRepository.save(order);

        double totalPrice = 0;
        int totalQuantityOrder = 0;
        for (CartItem cartItem : selectedItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getProduct().getPrice());
            orderDetailRepository.save(orderDetail);

            totalPrice += cartItem.getProduct().getPrice() * cartItem.getQuantity();
            totalQuantityOrder += cartItem.getQuantity();

            // Giảm stock sản phẩm
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException(product.getName() + " Insufficient stock. Remaining: " + product.getStock());
            }
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

        }

        order.setTotal_price(totalPrice);
        order.setTotal_quantity_order(totalQuantityOrder);
        orderRepository.save(order);

        cart.getItems().removeAll(selectedItems); // Cập nhật danh sách items của Cart
        cartRepository.save(cart);

        return new OrderResponse(
                order.getId(),
                order.getTotal_price(),
                order.getCreate_at(),
                order.getStatus().name(),
                order.getUser().getId(),
                order.getUser().getName(),
                order.getPlace_of_receipt(),
                order.getPhone_of_receipt(),
                order.getTotal_quantity_order(),
                order.getPayMethod().name()
        );
    }
    private void restockProducts(Order order) {
        List<OrderDetail> oldOrderDetails = orderDetailRepository.findAllByOrder(order);
        for (OrderDetail detail : oldOrderDetails) {
            Product product = detail.getProduct();
            product.setStock(product.getStock() + detail.getQuantity());
            productRepository.save(product);
        }
    }

//    private double processNewOrderDetails(Order order, OrderRequest req) {
//        double totalPrice = 0;
//
//        // Lấy danh sách Product một lần, tránh query DB nhiều lần
//        Map<Long, Product> productMap = productRepository.findAllById(
//                req.getOrderDetails().stream().map(OrderDetailRequest::getProductId).toList()
//        ).stream().collect(Collectors.toMap(Product::getId, product -> product));
//
//        for (OrderDetailRequest detailReq : req.getOrderDetails()) {
//            Product product = productMap.get(detailReq.getProductId());
//
//            if (product == null) {
//                throw new RuntimeException("Product not found with ID: " + detailReq.getProductId());
//            }
//
//            if (product.getStock() < detailReq.getQuantity()) {
//                throw new RuntimeException(product.getName() + " Insufficient stock. Remaining: " + product.getStock());
//            }
//
//            OrderDetail orderDetail = new OrderDetail();
//            orderDetail.setOrder(order);
//            orderDetail.setProduct(product);
//            orderDetail.setQuantity(detailReq.getQuantity());
//            orderDetail.setPrice(product.getPrice());
//            orderDetailRepository.save(orderDetail);
//
//            // Trừ tồn kho
//            product.setStock(product.getStock() - detailReq.getQuantity());
//            productRepository.save(product);
//
//            totalPrice += product.getPrice() * detailReq.getQuantity();
//        }
//
//        return totalPrice;
//    }


    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(this::convertToOrderResponse).collect(Collectors.toList());

    }

    public List<OrderResponse> getOrderById(Long id) {
        return orderRepository.findById(id).stream().map(this::convertToOrderResponse).collect(Collectors.toList());

    }


//    public Order updateOrder(String token, Long id, OrderRequest req) {
//        Long user_id = jwtUtil.extractUserId(token);
//        // Lấy Order & User từ DB
//        Order order = orderRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        User user = userRepository.findById(user_id)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        order.setUser(user);
//
//        restockProducts(order);
//
//        orderDetailRepository.deleteByOrder(order);
//
//        double totalPrice = processNewOrderDetails(order, req);
//
//        order.setTotal_price(totalPrice);
//        if (req.getStatus() != null) {
//            try {
//                order.setStatus(OrderStatus.valueOf(req.getStatus())); // Chuyển từ String → Enum
//            } catch (IllegalArgumentException e) {
//                throw new RuntimeException("Invalid order status: " + req.getStatus());
//            }
//        }
//        return orderRepository.save(order);
//    }


    @Transactional
    public void cancelOrder(Long id, Long userId) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }

        Order order = orderOpt.get();

        // Kiểm tra quyền sở hữu: userId từ token phải khớp với userId của đơn hàng
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        // Kiểm tra quyền sở hữu và trạng thái
        if (!isAdmin) {
            if (!order.getUser().getId().equals(userId)) {
                throw new SecurityException("You are not authorized to delete this order");
            }
            if (order.getStatus() != OrderStatus.PENDING) {
                throw new IllegalStateException("Order can only be deleted when its status is PENDING");
            }
        }

        // Hoàn lại stock cho sản phẩm
        List<OrderDetail> details = orderDetailRepository.findAllByOrder(order);
        for (OrderDetail detail : details) {
            Product product = detail.getProduct();
            product.setStock(product.getStock() + detail.getQuantity());
            productRepository.save(product);
        }

        // Đưa trạng thái order về cancelled
        order.setStatus(OrderStatus.CANCELLED);

    }

    @Transactional
    public void deleteCancelledOrder(Long id, Long userId) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }

        Order order = orderOpt.get();

        // Kiểm tra quyền sở hữu
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            if (!order.getUser().getId().equals(userId)) {
                throw new SecurityException("You are not authorized to delete this order");
            }
        }

        // Kiểm tra trạng thái phải là CANCELLED
        if (order.getStatus() != OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order can only be deleted when its status is CANCELLED");
        }

        // Xóa chi tiết đơn hàng trước
        orderDetailRepository.deleteByOrder(order);
        // Xóa đơn hàng
        orderRepository.delete(order);
    }

    public List<OrderResponse> getAllOrdersByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findAllByUser(user).stream().map(this::convertToOrderResponse).collect(Collectors.toList());

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

        return convertToOrderResponse(orderRepository.save(order));
    }
    private OrderResponse convertToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTotal_price(),
                order.getCreate_at(),
                order.getStatus().name(),
                order.getUser().getId(),
                order.getUser().getName(),
                order.getPlace_of_receipt(),
                order.getPhone_of_receipt(),
                orderDetailRepository.findAllByOrder(order).stream()
                        .map(detail -> new OrderDetailResponse(
                                detail.getProduct().getId(),
                                detail.getProduct().getName(),
                                detail.getProduct().getImage(),
                                detail.getQuantity(),
                                detail.getPrice()
                        )).collect(Collectors.toList()),
                order.getTotal_quantity_order(),
                order.getPayMethod() != null ? order.getPayMethod().name() : "UNKNOWN"
        );
    }
}

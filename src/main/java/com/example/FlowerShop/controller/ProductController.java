package com.example.FlowerShop.controller;

import com.example.FlowerShop.dto.request.ProductRequest;
import com.example.FlowerShop.model.Product;
import com.example.FlowerShop.service.ProductService;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN được thêm sản phẩm
    public ResponseEntity<Map<String, Object>> createProduct(@Valid @RequestBody ProductRequest req, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            response.put("status", "error");
            response.put("message", result.getAllErrors().get(0).getDefaultMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
        try {
            Product saveProduct = productService.createProduct(req);
            response.put("status", "success");
            response.put("message", "Them san pham thanh cong");
            response.put("data", saveProduct);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch (RuntimeException e){
            if(e.getMessage().equals("Product already exists")){
                response.put("status", "error");
                response.put("message", "Tên sản phẩm đã tồn tại. Vui lòng chọn tên khác.");
                response.put("code", "DUPLICATE_PRODUCT_NAME");
                response.put("data", null);
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
            response.put("status", "error");
            response.put("message", "Đã xảy ra lỗi khi thêm sản phẩm.");
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')") // Cả USER và ADMIN đều được xem
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productService.getAllProducts();
        response.put("status", "success");
        response.put("message", "Lấy danh sách sản phẩm thành công");
        response.put("data", products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')") // Cả USER và ADMIN đều được xem
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            response.put("status", "success");
            response.put("message", "Lấy thông tin sản phẩm thành công");
            response.put("data", product.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Sản phẩm không tồn tại");
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN được cập nhật
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            response.put("status", "success");
            response.put("message", "Cập nhật sản phẩm thành công");
            response.put("data", updatedProduct);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", "Sản phẩm không tồn tại");
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN được xóa
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            productService.deleteProduct(id);
            response.put("status", "success");
            response.put("message", "Xóa sản phẩm thành công");
            response.put("data", null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", "Sản phẩm không tồn tại");
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }
}

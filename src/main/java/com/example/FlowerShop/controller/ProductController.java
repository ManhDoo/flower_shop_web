package com.example.FlowerShop.controller;

import com.example.FlowerShop.dto.request.ProductRequest;
import com.example.FlowerShop.exception.ResourceNotFoundException;
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
    public ResponseEntity<Map<String, Object>> createProduct(@Valid @RequestBody ProductRequest req, BindingResult result) {
        Product savedProduct = productService.createProduct(req);
        Map<String, Object> response = Map.of(
                "status", "success",
                "message", "Add product successfully",
                "data", savedProduct
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        Map<String, Object> response = Map.of(
                "status", "success",
                "message", "Get product list successfully",
                "data", products
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Map<String, Object> response = Map.of(
                "status", "success",
                "message", "Get product information successfully",
                "data", product
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        Map<String, Object> response = Map.of(
                "status", "success",
                "message", "Update product successfully",
                "data", updatedProduct
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        Map<String, Object> response = Map.of(
                "status", "success",
                "message", "Delete product successfully"
        );
        return ResponseEntity.ok(response);
    }
}

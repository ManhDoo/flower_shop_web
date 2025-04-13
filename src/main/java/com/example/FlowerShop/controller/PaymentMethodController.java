package com.example.FlowerShop.controller;

import com.example.FlowerShop.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PaymentMethodController {

    @Autowired
    private PaymentMethodService paymentMethodService;

    @GetMapping("/payment-methods")
    public ResponseEntity<Map<String, Object>> getPaymentMethods() {
        List<String> methods = paymentMethodService.getAllPaymentMethods();

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Get payment methods successful",
                "data", methods
        ));
    }
}

package com.example.FlowerShop.service;

import com.example.FlowerShop.model.PayMethodName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {
    public List<String> getAllPaymentMethods() {
        return List.of(PayMethodName.values()).stream()
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}

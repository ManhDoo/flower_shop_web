package com.example.FlowerShop.dto.request;

import com.example.FlowerShop.model.PayMethodName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private Long userId;

    private String name_of_receipt;
    private String place_of_receipt;
    private int phone_of_receipt;

//    @NotEmpty(message = "Order details list cannot be blank")
//    @Valid
//    private List<OrderDetailRequest> orderDetails;

    private List<Long> cartItemIds;

    private String status;

    private PayMethodName payMethod;
}

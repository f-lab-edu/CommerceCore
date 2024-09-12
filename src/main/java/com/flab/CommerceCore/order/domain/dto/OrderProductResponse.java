package com.flab.CommerceCore.order.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductResponse {
    private String productName;
    private BigDecimal totalPrice;
    private int quantity;
}

package com.flab.CommerceCore.order.domain.dto;

import com.flab.CommerceCore.common.enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponse {
    private Long orderId;
    private List<OrderProductResponse> orderProductResponses;
    private Status status;
    private BigDecimal totalAmount;
}

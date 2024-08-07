package com.flab.CommerceCore.order.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequest {
    private Long userId;
    private List<OrderProductRequest> orderProductRequests;
}

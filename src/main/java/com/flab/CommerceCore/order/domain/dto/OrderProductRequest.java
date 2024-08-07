package com.flab.CommerceCore.order.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderProductRequest {

    private Long productId;
    private int quantity;
}

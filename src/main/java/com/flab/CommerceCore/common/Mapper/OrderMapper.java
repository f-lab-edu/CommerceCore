package com.flab.CommerceCore.common.Mapper;

import com.flab.CommerceCore.order.domain.dto.OrderProductResponse;
import com.flab.CommerceCore.order.domain.dto.OrderResponse;
import com.flab.CommerceCore.order.domain.entity.Order;
import com.flab.CommerceCore.order.domain.entity.OrderProduct;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

  public OrderResponse convertEntityToResponse(Order order) {

    List<OrderProductResponse> orderProductResponses = order.getOrderProducts().stream()
        .map(orderProduct -> OrderProductResponse.builder()
            .productName(orderProduct.getProduct().getProductName())
            .quantity(orderProduct.getQuantity())
            .totalPrice(orderProduct.getTotalPrice())
            .build())
        .toList();

    BigDecimal totalAmount = order.getOrderProducts().stream()
        .map(OrderProduct::getTotalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return OrderResponse.builder()
        .orderId(order.getOrderId())
        .orderProductResponses(orderProductResponses)
        .totalAmount(totalAmount)
        .status(order.getStatus())
        .userId(order.getUser().getUserId())
        .build();
  }
}

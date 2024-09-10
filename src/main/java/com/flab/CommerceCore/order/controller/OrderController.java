package com.flab.CommerceCore.order.controller;

import com.flab.CommerceCore.order.domain.dto.OrderRequest;
import com.flab.CommerceCore.order.domain.dto.OrderResponse;
import com.flab.CommerceCore.order.service.OrderService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping("/order")
  public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
    OrderResponse orderResponse = orderService.createOrder(orderRequest);
    return ResponseEntity.status(201).body(orderResponse);
  }

  @GetMapping
  public ResponseEntity<OrderResponse> getOrder(@RequestParam(defaultValue = "0") Long orderId) {
    OrderResponse orderResponse = orderService.findOrderById(orderId);
    return ResponseEntity.ok(orderResponse);
  }

  @GetMapping("/orders/{page}")
  public ResponseEntity<List<OrderResponse>> getAllOrders(@PathVariable("page") int page) {
    List<OrderResponse> list = orderService.findAllOrders(page);
    return ResponseEntity.ok(list);
  }

  @DeleteMapping("/order/{orderId}")
  public ResponseEntity<OrderResponse> cancelOrder(@PathVariable("orderId") Long orderId) {
    OrderResponse orderResponse = orderService.cancelOrder(orderId);
    return ResponseEntity.ok(orderResponse);
  }


}

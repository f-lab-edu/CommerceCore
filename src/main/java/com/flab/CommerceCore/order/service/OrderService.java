package com.flab.CommerceCore.order.service;

import com.flab.CommerceCore.inventory.domain.entity.Inventory;
import com.flab.CommerceCore.inventory.repository.InventoryRepository;
import com.flab.CommerceCore.order.domain.dto.OrderProductRequest;
import com.flab.CommerceCore.order.domain.dto.OrderRequest;
import com.flab.CommerceCore.order.domain.entity.Order;
import com.flab.CommerceCore.order.domain.entity.Order.OrderBuilder;
import com.flab.CommerceCore.order.domain.entity.OrderProduct;
import com.flab.CommerceCore.order.repository.OrderProductRepository;
import com.flab.CommerceCore.order.repository.OrderRepository;
import com.flab.CommerceCore.payment.domain.dto.PaymentRequest;
import com.flab.CommerceCore.payment.domain.entity.Payment;
import com.flab.CommerceCore.payment.domain.service.PaymentService;
import com.flab.CommerceCore.product.domain.entity.Product;
import com.flab.CommerceCore.product.repository.ProductRepository;
import com.flab.CommerceCore.user.domain.entity.User;
import com.flab.CommerceCore.user.repository.UserRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {


    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;



    @Autowired
    public OrderService(UserRepository userRepository, ProductRepository productRepository,
        InventoryRepository inventoryRepository, PaymentService paymentService,
        OrderRepository orderRepository, OrderProductRepository orderProductRepository){
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.paymentService = paymentService;
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
    }


    @Transactional
    public Long order(@RequestBody OrderRequest orderRequest){

        // 알맞은 유저인지 검사
        User user = userRepository.findById(orderRequest.getUserId());


        List<OrderProduct> orderProductList = new ArrayList<>();

        for(OrderProductRequest orderProductRequest : orderRequest.getOrderProductRequests()){
            // 알맞은 상품인지 검사
            Product product = productRepository.findById(orderProductRequest.getProductId());

            // 재고에 등록된 상품인지 검사
            Inventory inventory = inventoryRepository.findByProductId(product.getProductId());

            // 수량 체크 후 재고 감소
            inventory.reduceQuantity(orderProductRequest.getQuantity());

            // orderProduct 생성
            OrderProduct orderProduct = OrderProduct.OrderProduct(product,orderProductRequest.getQuantity());

            // orderProduct 영속화
            orderProductRepository.save(orderProduct);
            orderProductList.add(orderProduct);


        }
        // payment 생성
        Payment payment = paymentService.payment(new PaymentRequest(getTotalAmount(orderProductList)));

        // order 생성
        Order order = Order.builder()
            .user(user)
            .orderProducts(orderProductList)
            .payment(payment)
            .build();

        // order 영속화
        orderRepository.save(order);

        return order.getOrderId();

    }

    public BigDecimal getTotalAmount(List<OrderProduct> orderProducts){

        BigDecimal totalAmount = BigDecimal.ZERO;

        for(OrderProduct orderProduct : orderProducts){
            totalAmount = totalAmount.add(orderProduct.getTotalPrice());
        }

        return totalAmount;
    }


}

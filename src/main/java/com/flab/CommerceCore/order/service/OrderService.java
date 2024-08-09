package com.flab.CommerceCore.order.service;

import com.flab.CommerceCore.inventory.domain.entity.Inventory;
import com.flab.CommerceCore.inventory.repository.InventoryRepository;
import com.flab.CommerceCore.order.domain.dto.OrderProductRequest;
import com.flab.CommerceCore.order.domain.dto.OrderRequest;
import com.flab.CommerceCore.order.domain.entity.Order;
import com.flab.CommerceCore.order.domain.entity.OrderProduct;
import com.flab.CommerceCore.order.repository.OrderProductRepository;
import com.flab.CommerceCore.order.repository.OrderRepository;
import com.flab.CommerceCore.payment.domain.entity.Payment;
import com.flab.CommerceCore.payment.service.PaymentService;
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
        User user = validateUser(orderRequest.getUserId());

        List<OrderProduct> orderProductList = createOrderProducts(orderRequest.getOrderProductRequests());

        // payment process
        Payment payment = processPayment(orderProductList);
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

    private User validateUser(Long userId){
        return userRepository.findById(userId);
    }

    private Inventory validateInventory(Long productId){
        return inventoryRepository.findByProductId(productId);
    }

    private List<OrderProduct> createOrderProducts(List<OrderProductRequest> orderProductRequests){
        List<OrderProduct> orderProductList = new ArrayList<>();

        for(OrderProductRequest orderProductRequest : orderProductRequests){

            // 재고에 등록된 상품인지 검사
            Inventory inventory = validateInventory(orderProductRequest.getProductId());

            // 수량 체크 후 재고 감소
            inventory.reduceQuantity(orderProductRequest.getQuantity());


            Product product = productRepository.findById(orderProductRequest.getProductId());

            OrderProduct orderProduct = OrderProduct.builder()
                .product(product)
                .quantity(orderProductRequest.getQuantity())
                .build();
            // orderProduct 영속화
            orderProductRepository.save(orderProduct);

            orderProductList.add(orderProduct);
        }

        return orderProductList;
    }

    private Payment processPayment(List<OrderProduct> orderProductList){
        BigDecimal totalAmount = getTotalAmount(orderProductList);
        return paymentService.payment(totalAmount);
    }


    private BigDecimal getTotalAmount(List<OrderProduct> orderProducts){

        BigDecimal totalAmount = BigDecimal.ZERO;

        for(OrderProduct orderProduct : orderProducts){
            totalAmount = totalAmount.add(orderProduct.getTotalPrice());
        }

        return totalAmount;
    }


}

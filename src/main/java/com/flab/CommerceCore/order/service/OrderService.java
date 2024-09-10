package com.flab.CommerceCore.order.service;

import com.flab.CommerceCore.common.Mapper.OrderMapper;
import com.flab.CommerceCore.common.enums.InventoryOperation;
import com.flab.CommerceCore.common.enums.Status;
import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.common.exceptions.ErrorCode;
import com.flab.CommerceCore.inventory.domain.entity.Inventory;
import com.flab.CommerceCore.inventory.repository.InventoryRepository;
import com.flab.CommerceCore.order.domain.dto.OrderProductRequest;
import com.flab.CommerceCore.order.domain.dto.OrderRequest;
import com.flab.CommerceCore.order.domain.dto.OrderResponse;
import com.flab.CommerceCore.order.domain.entity.Order;
import com.flab.CommerceCore.order.domain.entity.OrderProduct;
import com.flab.CommerceCore.order.repository.OrderProductRepository;
import com.flab.CommerceCore.order.repository.OrderRepository;
import com.flab.CommerceCore.payment.service.PaymentService;
import com.flab.CommerceCore.product.domain.entity.Product;
import com.flab.CommerceCore.product.repository.ProductRepository;
import com.flab.CommerceCore.user.domain.entity.User;
import com.flab.CommerceCore.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@Service
@Slf4j
public class OrderService {


    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderMapper mapper;



    @Autowired
    public OrderService(UserRepository userRepository, ProductRepository productRepository,
        InventoryRepository inventoryRepository, PaymentService paymentService,
        OrderRepository orderRepository, OrderProductRepository orderProductRepository,
        OrderMapper mapper){
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.paymentService = paymentService;
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.mapper = mapper;
    }


    /**
     * 사용자의 주문을 생성하는 메서드
     * 유저의 유효성을 검사하고, 주문 상품 및 재고를 확인한 뒤 주문을 생성
     * 결제까지 완료한 후, 주문 정보를 반환
     *
     * @param orderRequest 주문 생성에 필요한 요청 정보
     * @return 생성된 주문의 정보
     * @throws BusinessException 유효하지 않은 유저 ID 또는 상품/재고 문제 시 발생
     */
    @Transactional
    public OrderResponse createOrder(@RequestBody OrderRequest orderRequest){

        // 알맞은 유저인지 검사
        User user = validateUser(orderRequest.getUserId());
        log.info("유저 검증 성공 userId: {}", user.getUserId());

        // 주문할 상품들 및 재고 정보 생성 및 검증
        List<OrderProduct> orderProductList = createOrderProducts(orderRequest.getOrderProductRequests());
        log.info("주문 상품 생성 성공 userId: {}", user.getUserId());

        // 주문 상품 리스트를 일괄 저장
        orderProductRepository.saveAll(orderProductList); // 일괄 저장
        log.info("주문 상품 DB 저장 성공 userId: {}", user.getUserId());

        // 주문 상품들의 총 금액 계산
        BigDecimal totalAmount = getTotalAmount(orderProductList);

        // order 생성
        Order order = Order.builder()
            .user(user)
            .orderProducts(orderProductList)
            .payment(paymentService.payment(totalAmount)) // 결제 처리
            .build();

        // order 영속화
        orderRepository.save(order);
        log.info("주문 생성 완료 orderId : {}", order.getOrderId());

        // 주문 생성 후 주문 조회 페이지로 리다이렉트할 URL 반환
        return mapper.convertEntityToResponse(order);
    }

    /**
     * 주문 ID로 주문 정보를 조회하는 메서드
     *
     * @param orderId 조회할 주문의 ID
     * @return OrderResponse 주문에 대한 응답 정보
     * @throws BusinessException 주문이 존재하지 않을 경우 예외 발생
     */
    @Transactional(readOnly = true)
    public OrderResponse findOrderById(Long orderId){
        Order order = orderRepository.findByOrderId(orderId);
        if(order == null){
            log.error(ErrorCode.ORDER_NOT_FOUND.getDetail(),orderId);
            throw BusinessException.create(ErrorCode.ORDER_NOT_FOUND);
        }
        return mapper.convertEntityToResponse(order);
    }

    /**
     * 페이징 처리된 모든 주문 목록을 조회하는 메서드
     *
     * @param page 조회할 페이지 번호
     * @return List<OrderResponse> 주문에 대한 응답 리스트
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> findAllOrders(int page) {
        int pageSize = 10;  // 기본 10개씩
        Pageable pageable = PageRequest.of(page, pageSize);  // 페이지 요청 생성

        // 주문 페이지를 조회
        Page<Order> orderPage = orderRepository.findAll(pageable);

        // 조회한 주문들을 OrderResponse로 변환하여 리스트로 반환
        return orderPage.getContent().stream()
            .map(mapper::convertEntityToResponse)
            .toList();
    }


    /**
     * 주문을 취소하는 메서드
     *
     * @param orderId 취소할 주문 ID
     * @return OrderResponse 취소된 주문에 대한 응답 객체
     * @throws BusinessException 주문이 존재하지 않거나 취소할 수 없는 상태일 때 발생
     */
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        // 주문 조회
        Order order = orderRepository.findByOrderId(orderId);
        if(order == null){
            log.error(ErrorCode.ORDER_NOT_FOUND.getDetail(),orderId);
            throw BusinessException.create(ErrorCode.ORDER_NOT_FOUND);
        }

        // 이미 취소된 주문은 다시 취소할 수 없음
        if (order.getStatus() == Status.CANCEL) {
            log.error(ErrorCode.ORDER_ALREADY_CANCELED.getDetail(),orderId);
            throw BusinessException.create(ErrorCode.ORDER_ALREADY_CANCELED);
        }

        // 주문 상태를 CANCEL 로 변경
        order.cancelOrder(Status.CANCEL);

        // 취소된 주문 정보를 반환
        return mapper.convertEntityToResponse(order);
    }


    /**
     * 주문 상품 목록을 생성하고, 각 상품의 재고를 수정하는 메서드
     * 상품 ID와 수량을 확인하여 주문 상품 객체를 생성합
     *
     * @param orderProductRequests 주문할 상품 요청 리스트
     * @return 생성된 주문 상품 리스트
     * @throws BusinessException 재고가 부족하거나 상품이 없을 경우 예외 발생
     */
    private List<OrderProduct> createOrderProducts(List<OrderProductRequest> orderProductRequests){

        // 요청된 productId 들을 추출
        List<Long> productIds = orderProductRequests.stream()
            .map(OrderProductRequest::getProductId)
            .toList();

        // 등록된 상품 및 재고 정보 확인
        Map<Long, Product> products = validateProducts(productIds);
        Map<Long, Inventory> inventories = validateInventories(productIds);

        // 각 주문 상품 및 재고를 확인 후 주문 상품 객체 생성
        return orderProductRequests.stream()
            .map(request -> {
                Inventory inventory = inventories.get(request.getProductId());
                Product product = products.get(request.getProductId());

                // 재고 감소
                inventory.modifyQuantity(request.getQuantity(), InventoryOperation.DECREASE);

                // OrderProduct 생성
                return OrderProduct.builder()
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            }).toList();

    }


    /**
     * 유저 ID가 유효한지 검증하는 메서드
     *
     * @param userId 검증할 유저의 ID
     * @return 유효한 유저 객체
     * @throws BusinessException 유저가 존재하지 않을 경우 예외 발생
     */
    private User validateUser(Long userId){
        User user = userRepository.findByUserId(userId);

        if(user == null){
          log.error(ErrorCode.USERID_NOT_FOUND.getDetail(),userId);
          throw BusinessException.create(ErrorCode.USERID_NOT_FOUND);
        }
        return user;
    }

    /**
     * 주문할 상품들의 재고 정보를 검증하는 메서드
     * 요청된 상품 ID 리스트에 해당하는 재고가 있는지 확인
     *
     * @param productIds 상품 ID 리스트
     * @return 유효한 재고 정보가 담긴 Map
     * @throws BusinessException 재고가 없거나 상품이 유효하지 않은 경우 예외 발생
     */
    private Map<Long, Inventory> validateInventories(List<Long> productIds){

        List<Inventory> inventories = inventoryRepository.findAllByProductId(productIds);

        // 조회된 재고 개수와 요청한 productIds 의 개수가 맞지 않으면 예외 발생
        if(inventories.size() != productIds.size()){
          log.error(ErrorCode.PRODUCT_NOT_FOUND.getDetail(),productIds);
          throw BusinessException.create(ErrorCode.PRODUCT_NOT_FOUND);
        }

        // 재고 리스트를 Map 형태로 변환하여 반환
        Map<Long, Inventory> inventoryMap = new HashMap<>();
        for(Inventory inventory : inventories){
            inventoryMap.put(inventory.getInventoryId(), inventory);
        }
        return inventoryMap;
    }

    /**
     * 상품 ID 리스트를 기반으로 유효한 상품들을 검증하는 메서드
     *
     * @param productIds 상품 ID 리스트
     * @return 유효한 상품 정보가 담긴 Map
     * @throws BusinessException 상품이 없거나 유효하지 않은 경우 예외 발생
     */
    private Map<Long, Product> validateProducts(List<Long> productIds){
        List<Product> products = productRepository.findAllByProductId(productIds);

        // 조회된 상품 개수와 요청한 productIds의 개수가 맞지 않으면 예외 발생
        if(products.size() != productIds.size()){
            log.error(ErrorCode.PRODUCT_NOT_FOUND.getDetail(),productIds);
            throw BusinessException.create(ErrorCode.PRODUCT_NOT_FOUND);
        }

        // 상품 리스트를 Map 형태로 변환하여 반환
        Map<Long, Product> productMap = new HashMap<>();
        for(Product product : products){
            productMap.put(product.getProductId(), product);
        }

        return productMap;
    }


    private BigDecimal getTotalAmount(List<OrderProduct> orderProducts){

        BigDecimal totalAmount = BigDecimal.ZERO;

        for(OrderProduct orderProduct : orderProducts){
            totalAmount = totalAmount.add(orderProduct.getTotalPrice());
        }

        return totalAmount;
    }


}

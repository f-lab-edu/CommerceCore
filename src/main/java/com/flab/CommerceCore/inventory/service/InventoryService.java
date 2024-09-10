package com.flab.CommerceCore.inventory.service;

import com.flab.CommerceCore.common.Mapper.InventoryMapper;
import com.flab.CommerceCore.common.enums.InventoryOperation;
import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.common.exceptions.ErrorCode;
import com.flab.CommerceCore.inventory.domain.dto.InventoryResponse;
import com.flab.CommerceCore.inventory.domain.entity.Inventory;
import com.flab.CommerceCore.inventory.repository.InventoryRepository;
import com.flab.CommerceCore.product.domain.entity.Product;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InventoryService {

  private final InventoryRepository inventoryRepository;
  private final InventoryMapper mapper;


  public InventoryService(InventoryRepository inventoryRepository, InventoryMapper mapper) {
    this.inventoryRepository = inventoryRepository;
    this.mapper = mapper;
  }


  /**
   * 재고를 생성하는 메서드
   * @param product 생성할 재고에 대한 상품 정보
   * @param quantity 생성할 재고 수량
   * @return 재고 수량 반환
   * @throws BusinessException 중복된 상품이 있을 경우 발생
   */
  public int createInventory(Product product, int quantity) {

    validateDuplicateProduct(product);

    Inventory inventory = mapper.toEntity(product, quantity);

    Inventory savedInventory = inventoryRepository.save(inventory);

    return savedInventory.getQuantity();
  }


  /**
   * 상품에 대한 재고 수량 조회 메서드
   *
   * @param productId 조회할 상품 ID
   * @return 상품에 대한 재고 수량
   */
  public int findQuantityByProductId(Long productId) {
    Inventory inventory = getInventoryOrThrowByProductId(productId);
    return inventory.getQuantity();
  }


  /**
   * 모든 재고 목록을 조회하는 메서드
   *
   * @return 재고 목록
   */
  public List<Inventory> findAllInventory() {
    return inventoryRepository.findAll();
  }


  /**
   * 재고를 삭제하는 메서드
   *
   * @param inventoryId 삭제할 재고의 ID
   * @throws BusinessException 재고가 존재하지 않을 경우 발생
   */
  public void deleteInventory(Long inventoryId) {
    getInventoryOrThrow(inventoryId);
    inventoryRepository.deleteById(inventoryId);
    log.info("Inventory deleted successfully for inventoryId: {}", inventoryId);
  }


  /**
   * 재고를 감소시키는 메서드
   *
   * @param productId 감소시킬 상품 ID
   * @param quantity 감소시킬 수량
   * @return 감소된 재고 정보를 담은 InventoryResponse 객체
   * @throws BusinessException 해당 상품이 존재하지 않을 경우
   */
  @Transactional
  public InventoryResponse reduceQuantity(Long productId, int quantity) {
    Inventory inventory = getInventoryOrThrowByProductId(productId);
    inventory.modifyQuantity(quantity, InventoryOperation.DECREASE);

    return mapper.convertEntityToResponse(inventory);
  }


  /**
   * 재고를 증가 시키는 메서드
   *
   * @param inventoryId 증가 시킬 재고 ID
   * @param quantity 증가시킬 수량
   * @return 증가된 재고 정보를 담은 InventoryResponse 객체
   * @throws BusinessException 재고가 존재하지 않을 경우 발생
   */
  @Transactional
  public InventoryResponse increaseQuantity(Long inventoryId, int quantity) {
    Inventory inventory = getInventoryOrThrow(inventoryId);
    inventory.modifyQuantity(quantity, InventoryOperation.INCREASE);

    return mapper.convertEntityToResponse(inventory);
  }


  /**
   * 재고 수량을 업데이트하는 메서드
   *
   * @param productId 업데이트 시킬 상품 ID
   * @param quantity 업데이트시킬 수량
   * @return 업데이트된 재고 정보를 담은 InventoryResponse 객체
   * @throws BusinessException 재고가 존재하지 않을 경우 발생
   */
  @Transactional
  public InventoryResponse updateQuantity(Long productId, int quantity){
    Inventory inventory = getInventoryOrThrowByProductId(productId);
    inventory.modifyQuantity(quantity, InventoryOperation.UPDATE);


    return mapper.convertEntityToResponse(inventory);
  }


  /**
   * 중복된 상품이 있는지 확인하는 메서드
   *
   * @param product 중복 여부를 확인할 상품
   * @throws BusinessException 중복된 상품이 있을 경우 발생
   */
  private void validateDuplicateProduct(Product product) {
    if (inventoryRepository.findByProductId(product.getProductId()) != null) {
      log.error("이미 등록된 상품입니다.: {}", product.getProductName());
      throw new BusinessException(ErrorCode.DUPLICATED_PRODUCT);
    }
  }


  /**
   * 주어진 재고 ID로 재고 정보를 검색
   *
   * @param inventoryId 검색할 재고의 ID
   * @return 검색된 재고 정보
   * @throws BusinessException 재고가 존재하지 않을 경우
   */
  private Inventory getInventoryOrThrow(Long inventoryId) {
    Inventory inventory = inventoryRepository.findByInventoryId(inventoryId);
    if(inventory==null){
      log.error(ErrorCode.INVENTORY_NOT_FOUND.getDetail(), inventoryId);
      throw new BusinessException(ErrorCode.INVENTORY_NOT_FOUND);
    }

    return inventory;
  }

  /**
   * 주어진 상품 ID로 재고 정보 검색
   *
   * @param productId 검색할 상품 ID
   * @return 검색된 재고 정보
   * @throws BusinessException 상품이 존재하지 않을 경우 발생
   */
  private Inventory getInventoryOrThrowByProductId(Long productId) {
    Inventory inventory = inventoryRepository.findByProductId(productId);
    if(inventory == null) {
      log.error(ErrorCode.PRODUCT_NOT_FOUND.getMessage(), productId);
      throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
    }
    return inventory;
  }














}

package com.flab.CommerceCore.inventory.repository;

import static com.flab.CommerceCore.common.query.QueryConstant.FIND_ALL_INVENTORY_BY_PRODUCT_ID;
import static com.flab.CommerceCore.common.query.QueryConstant.FIND_INVENTORY_BY_PRODUCT_ID;

import com.flab.CommerceCore.inventory.domain.entity.Inventory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query(FIND_INVENTORY_BY_PRODUCT_ID)
    Inventory findByProductId(@Param("productId") Long productId);

    Inventory findByInventoryId(Long inventoryId);

    @Query(FIND_ALL_INVENTORY_BY_PRODUCT_ID)
    List<Inventory> findAllByProductId(@Param("productIds") List<Long> productIds);
}
package com.flab.CommerceCore.inventory.repository;

import static com.flab.CommerceCore.common.query.QueryConstant.FIND_INVENTORY_BY_PRODUCT_ID;

import com.flab.CommerceCore.inventory.domain.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query(FIND_INVENTORY_BY_PRODUCT_ID)
    Inventory findByProductId(Long productId);
}
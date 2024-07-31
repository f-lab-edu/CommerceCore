package com.flab.CommerceCore.inventory.repository;

import com.flab.CommerceCore.inventory.domain.entity.Inventory;
import com.flab.CommerceCore.product.domain.entity.Product;
import com.flab.CommerceCore.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class InventoryRepositoryTest {

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Test
    public void 수량체크(){
        //given

        Product product = new Product();

        productRepository.save(product);

        Inventory inventory = new Inventory(product,30);
        inventoryRepository.save(inventory);


        //when
        Integer quantity = inventoryRepository.getQuantity(product.getProductId());

        //then
        assertEquals(30,quantity);
    }



}
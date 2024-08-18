package com.flab.CommerceCore.inventory.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flab.CommerceCore.common.exceptions.BusinessException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryTest {

  private Inventory inventory;

  @BeforeEach
  void setUp() {
     inventory = Inventory.builder()
        .quantity(100)
        .build();
  }


  @Test
  void testReduceQuantityConcurrency() throws InterruptedException {

    int reduceAmount = 10;
    int threadCount = 10;

    List<Thread> threads = new ArrayList<>();

    // threadCount만큼 스레드를 생성하여 리스트에 추가
    for (int i = 0; i < threadCount; i++) {
      Thread thread = new Thread(() -> {
        try {
          Thread.sleep(50);  // 다른 스레드가 이 시점에 개입할 수 있도록 약간의 지연을 줌
          inventory.reduceQuantity(reduceAmount);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      });
      threads.add(thread);
    }

    // 모든 스레드를 시작
    for (Thread thread : threads) {
      thread.start();
    }

    // 모든 스레드가 완료될 때까지 기다림
    for (Thread thread : threads) {
      thread.join();
    }

    // 최종 수량이 예상대로인지 확인
    assertEquals(0, inventory.getQuantity(), "최종 수량이 예상과 다릅니다.");

  }

  @Test
  void testQuantityException() {
    int reduceAmount = 101;
    assertThrows(BusinessException.class,()->inventory.reduceQuantity(reduceAmount));
  }

}
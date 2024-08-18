package com.flab.CommerceCore.inventory.domain.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flab.CommerceCore.common.exceptions.BusinessException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
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
    // given
    int reduceCount = 2;
    int threadCount = 100;

    CountDownLatch countDownLatch = new CountDownLatch(threadCount);
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

    AtomicInteger success = new AtomicInteger();
    AtomicInteger failure = new AtomicInteger();

    // when
    for (int i = 0; i < threadCount; i++) {
      executorService.submit(() -> {
        try{
          inventory.reduceQuantity(reduceCount);
          success.incrementAndGet();
        }catch (BusinessException e){
          failure.incrementAndGet();
        }finally {
          countDownLatch.countDown();
        }
      });
    }

    countDownLatch.await();
    executorService.shutdown();

    // then
    assertAll(
        ()->assertThat(success.get()).isEqualTo(50),
        ()->assertThat(failure.get()).isEqualTo(50)
    );
  }




}
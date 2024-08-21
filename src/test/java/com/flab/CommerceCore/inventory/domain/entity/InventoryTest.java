package com.flab.CommerceCore.inventory.domain.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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

    CountDownLatch readyLatch = new CountDownLatch(threadCount); // 100개의 스레드가 준비되었음을 알림
    CountDownLatch startLatch = new CountDownLatch(1);           // 모든 스레드가 동시에 시작되도록 대기
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

    AtomicInteger success = new AtomicInteger();
    AtomicInteger failure = new AtomicInteger();

    // when
    for (int i = 0; i < threadCount; i++) {
      executorService.submit(() -> {
        try{
          readyLatch.countDown();  // 100개의 스레드가 준비됨을 알림(메인 스레드는 제외)
          startLatch.await();      // 100개의 스레드 대기
          inventory.reduceQuantity(reduceCount);
          success.incrementAndGet();
        }catch (BusinessException e){
          failure.incrementAndGet();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      });
    }

    readyLatch.await(); // 메인 스레드는 모든 스레드가 준비될 때까지 대기
    startLatch.countDown(); // 100개의 스레드가 준비되면 startLatch를 통해 reduceQuantity 동시에 시작

    executorService.shutdown(); // 제출된 작업들은 모두 완료하고 스레드 풀 종료

    // then
    assertAll(
        ()->assertThat(success.get()).isEqualTo(50),
        ()->assertThat(failure.get()).isEqualTo(50)
    );
  }




}
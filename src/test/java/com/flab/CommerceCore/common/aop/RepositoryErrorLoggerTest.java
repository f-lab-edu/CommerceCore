package com.flab.CommerceCore.common.aop;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;


class RepositoryErrorLoggerTest {

  @InjectMocks
  private RepositoryErrorLogger repositoryErrorLogger;

  @Mock
  private ProceedingJoinPoint proceedingJoinPoint;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testDoLogWithRetrySuccess() throws Throwable {
    // given: 첫 번째 시도에서 DataAccessException이 발생하고, 두 번째 시도에서는 성공적으로 완료됩니다.
    when(proceedingJoinPoint.proceed())
        .thenThrow(new DataAccessException("DB Error") {})
        .thenReturn("Success");

    // when
    Object result = repositoryErrorLogger.doLog(proceedingJoinPoint);

    // then: 결과가 "Success"여야 하며, 로그 메서드가 두 번 호출되었음을 확인합니다.
    assertEquals("Success", result);
    verify(proceedingJoinPoint, times(2)).proceed();
  }

  @Test
  void testDoLogWithRetryFailure() throws Throwable {
    // given: 모든 시도가 DataAccessException을 던지는 상황을 시뮬레이션합니다.
    when(proceedingJoinPoint.proceed())
        .thenThrow(new DataAccessException("DB Error") {});

    // when & then: 예외가 발생했는지 확인하고, 최대 시도 횟수만큼 재시도했는지 확인합니다.
    DataAccessException exception = assertThrows(DataAccessException.class, () -> {
      repositoryErrorLogger.doLog(proceedingJoinPoint);
    });

    assertEquals("DB Error", exception.getMessage());
    verify(proceedingJoinPoint, times(5)).proceed();
  }
}
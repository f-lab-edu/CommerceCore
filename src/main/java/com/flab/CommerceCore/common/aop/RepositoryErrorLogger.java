package com.flab.CommerceCore.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RepositoryErrorLogger {

  @Around("@within(com.flab.CommerceCore.common.annotation.LogRepositoryError)")
  public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
    int maxAttempt = 5;
    int attempt = 1;

    while (true) {
      try {
        // 메서드 실행
        return joinPoint.proceed();
      } catch (DataAccessException error) {
        log.error("DataAccess 재시도 {}/{}",maxAttempt,attempt);
        attempt++;
        if (attempt > maxAttempt) {
          log.error("[Exception in {}] - Message: {}", joinPoint.getSignature(), error.getMessage());
          throw error; // 예외 재발생시켜 예외 처리기로 전달
        }
      }
    }
  }

}

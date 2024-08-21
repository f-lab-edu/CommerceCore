package com.flab.CommerceCore.common.exceptions;


import static com.flab.CommerceCore.common.exceptions.ErrorCode.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * 데이터베이스 리소스 접근 실패 예외 처리 메서드.
   * @param e
   * @return 데이터베이스 리소스에 접근할 수 없을 때의 오류 응답과 HTTP 상태 코드 503
   */
  @ExceptionHandler(DataAccessResourceFailureException.class)
  public ResponseEntity<ErrorCode> handleDataAccessResourceFailureException(DataAccessResourceFailureException e){
    log.error("데이터베이스 리소스에 접근할 수 없습니다.", e);
    return new ResponseEntity<>(DATA_ACCESS_RESOURCE_FAILURE, HttpStatus.SERVICE_UNAVAILABLE);
  }

  /**
   * 데이터 무결성 제약 조건 위반 예외 처리 메서드.
   * @param e
   * @return 데이터 무결성 위반 시의 오류 응답과 HTTP 상태 코드 400
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorCode> handleDataIntegrityViolationException(DataIntegrityViolationException e){
    log.error("데이터 무결성 제약 조건 위반",e);
    return new ResponseEntity<>(DATA_INTEGRITY_VIOLATION,HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<ErrorCode> handleDataAccessException(DataAccessException e){
    return new ResponseEntity<>(DATA_ACCESS_EXCEPTION,HttpStatus.INTERNAL_SERVER_ERROR);
  }



  /**
   * 비즈니스 계층에서 발생하는 예외 처리 메서드
   * @param e
   * @return
   */
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorCode> handleBusinessException(BusinessException e){
    return new ResponseEntity<>(e.getErrorCode(), HttpStatus.BAD_REQUEST);
  }


}

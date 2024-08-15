package com.flab.CommerceCore.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // Data Access 계층 예외
  DATA_ACCESS_RESOURCE_FAILURE(HttpStatus.SERVICE_UNAVAILABLE, "데이터베이스 리소스에 접근할 수 없습니다.", ""),
  DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "데이터 무결성 제약 조건 위반", ""),

  // Business 계층 예외
  USERID_NOT_FOUND(HttpStatus.NOT_FOUND,"유저 정보를 찾을 수 없습니다.","유저 ID[{}]로 찾을수 없습니다."),
  PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 정보를 찾을 수 없습니다.","상품 ID[{}]로 찾을 수 없습니다."),
  INSUFFICIENT_INVENTORY(HttpStatus.BAD_REQUEST,"재고가 부족합니다.","재고 : [{}]개 ,요청 : [{}]개"),
  PAYMENT_FAILED(HttpStatus.PAYMENT_REQUIRED, "결제에 실패했습니다.","");


  private final HttpStatus httpStatus;
  private final String message;
  private final String detail;

}

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



  private final HttpStatus httpStatus;
  private final String message;
  private final String detail;

}

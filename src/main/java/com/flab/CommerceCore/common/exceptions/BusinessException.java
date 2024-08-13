package com.flab.CommerceCore.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class BusinessException extends RuntimeException{

  private final ErrorCode errorCode;

  public static BusinessException create(ErrorCode errorCode) {
    return BusinessException
        .builder().errorCode(errorCode).build();
  }

}

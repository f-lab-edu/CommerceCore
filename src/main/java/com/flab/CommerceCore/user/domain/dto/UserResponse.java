package com.flab.CommerceCore.user.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

  private Long userId;
  private String name;
  private String email;
  private String phoneNum;
  private String address;


}

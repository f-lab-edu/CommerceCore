package com.flab.CommerceCore.common.security.dto;

import lombok.Getter;

@Getter
public class AuthenticationResponse {
  private String jwt;

  public AuthenticationResponse(String jwt) {
    this.jwt = jwt;
  }

}

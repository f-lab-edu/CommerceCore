package com.flab.CommerceCore.user.domain.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드를 받는 생성자 추가
public class UserRequest {

  @NotBlank(message = "이름은 필수 입력 항목입니다.")
  private String name;

  @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
  private String password;

  @Email(message = "유효한 이메일 주소를 입력하세요.")
  @NotBlank(message = "이메일은 필수 입력 항목입니다.")
  private String email;

  @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
  private String phoneNum;

  @NotNull(message = "주소는 필수 입력 항목입니다.")
  private String address;


}

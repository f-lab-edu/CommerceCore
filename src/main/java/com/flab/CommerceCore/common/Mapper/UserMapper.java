package com.flab.CommerceCore.common.Mapper;

import com.flab.CommerceCore.user.domain.dto.UserRequest;
import com.flab.CommerceCore.user.domain.dto.UserResponse;
import com.flab.CommerceCore.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  /**
   * UserRequest DTO를 User 엔티티로 변환하는 메서드
   *
   * @param userRequest 사용자 요청 정보
   * @return 변환된 User 엔티티
   */
  public User convertRequestToEntity(UserRequest userRequest) {
    return User.builder()
        .name(userRequest.getName())
        .email(userRequest.getEmail())
        .password(userRequest.getPassword())
        .phoneNum(userRequest.getPhoneNum())
        .address(userRequest.getAddress())
        .build();
  }

  /**
   * User 엔티티를 UserResponse DTO로 변환하는 메서드
   *
   * @param user 변환할 User 엔티티
   * @return 변환된 UserResponse DTO
   */
  public UserResponse convertEntityToResponse(User user) {
    return UserResponse.builder()
        .userId(user.getUserId())
        .name(user.getName())
        .email(user.getEmail())
        .phoneNum(user.getPhoneNum())
        .address(user.getAddress())
        .build();
  }
}

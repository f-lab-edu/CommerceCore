package com.flab.CommerceCore.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.user.domain.dto.UserRequest;
import com.flab.CommerceCore.user.domain.dto.UserResponse;
import com.flab.CommerceCore.user.domain.entity.User;
import com.flab.CommerceCore.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  UserRepository userRepository;

  @InjectMocks
  UserService userService;

  private User user;
  private UserRequest userRequest;

  private static final String NAME = "kim";
  private static final String EMAIL = "kim@gmail.com";
  private static final String PASSWORD = "1234";
  private static final String PHONE_NUM = "123456789";
  private static final String ADDRESS = "main street";

  @BeforeEach
  void setUp() {
    user = User.builder()
        .name(NAME)
        .email(EMAIL)
        .password(PASSWORD)
        .phoneNum(PHONE_NUM)
        .address(ADDRESS)
        .build();

    userRequest = UserRequest.builder()
        .name(NAME)
        .email(EMAIL)
        .password(PASSWORD)
        .phoneNum(PHONE_NUM)
        .address(ADDRESS)
        .build();
  }

  @Test
  void createUser_Fail_duplicated(){
    // given: 이미 존재하는 사용자가 있을 경우
    when(userRepository.findByEmail(EMAIL)).thenReturn(user);

    // when & then: 중복된 이메일로 인해 예외가 발생해야 함
    assertThrows(BusinessException.class, ()->{
      userService.createUser(userRequest);
    });

    // then: 새로운 사용자는 저장되지 않아야 함
    verify(userRepository, never()).save(any(User.class));

  }

  @Test
  void createUser_Success(){
    // given: 이메일이 중복되지 않은 경우
    when(userRepository.findByEmail(EMAIL)).thenReturn(null);
    when(userRepository.save(any(User.class))).thenReturn(user);

    // when: 새로운 User 를 생성
    UserResponse userResponse = userService.createUser(userRequest);

    // then: 사용자가 정상적으로 생성되고 저장되었는지 확인
    assertNotNull(userResponse);
    assertEquals(EMAIL, userResponse.getEmail());
    assertEquals(NAME, userResponse.getName());

    verify(userRepository, times(1)).findByEmail(EMAIL);
    verify(userRepository, times(1)).save(any(User.class));

  }

  @Test
  void findUser_Fail_null(){
    // given: 사용자가 존재하지 않을 경우
    when(userRepository.findByUserId(1L)).thenReturn(null);

    // when & then: 사용자가 없으면 예외가 발생해야 함
    assertThrows(BusinessException.class, ()->{
      userService.findUserByUserId(1L);
    });

    // then: 올바른 메서드가 호출되었는지 확인
    verify(userRepository, times(1)).findByUserId(1L);

  }

  @Test
  void findUser_Success(){
    // given: 사용자가 존재하는 경우
    when(userRepository.findByUserId(1L)).thenReturn(user);

    // when: 사용자를 조회
    UserResponse userResponse = userService.findUserByUserId(1L);

    // then: 조회된 사용자가 정상적인지 확인
    assertNotNull(userResponse);
    assertEquals(user.getEmail(), userResponse.getEmail());

    verify(userRepository, times(1)).findByUserId(1L);
  }

  @Test
  void updateUser_Success(){
    // given: 기존 사용자가 존재하고 업데이트 요청이 주어진 경우
    UserRequest updateRequestUser = UserRequest.builder()
        .name("park")
        .email("park@gmail.com")
        .password("q1w2e3")
        .phoneNum("123-456-789")
        .address("계양구")
        .build();

    when(userRepository.findByUserId(1L)).thenReturn(user);

    // when: 사용자를 업데이트
    UserResponse userResponse = userService.updateUser(1L, updateRequestUser);

    // then: 업데이트된 사용자의 정보가 일치하는지 확인
    assertNotNull(userResponse);
    assertEquals(updateRequestUser.getEmail(), userResponse.getEmail());

    verify(userRepository, times(1)).findByUserId(1L);
  }



}
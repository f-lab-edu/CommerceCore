package com.flab.CommerceCore.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.common.exceptions.ErrorCode;
import com.flab.CommerceCore.common.exceptions.GlobalExceptionHandler;
import com.flab.CommerceCore.user.domain.dto.UserRequest;
import com.flab.CommerceCore.user.domain.dto.UserResponse;
import com.flab.CommerceCore.user.service.UserService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @InjectMocks
  private UserController userController;

  @Mock
  private UserService userService;

  private MockMvc mockMvc;
  private Gson gson;

  private static final String URL = "/user";
  private static final String NAME = "kim";
  private static final String EMAIL = "kim@gmail.com";
  private static final String PASSWORD = "1234";
  private static final String PHONE_NUM = "01012345678";
  private static final String ADDRESS = "incheon";

  @BeforeEach
  public void init() {
    gson = new Gson();
    mockMvc = MockMvcBuilders.standaloneSetup(userController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  /**
   * 필드가 null인 경우, Bad Request(400) 상태 코드를 반환하는지 테스트
   */
  @Test
  void createUser_Fail_null() throws Exception {
    UserRequest userRequest = userRequest(null, null, null, null, null);

    ResultActions resultActions = performPostRequest(URL, userRequest);

    resultActions.andExpect(status().isBadRequest());
  }

  /**
   * 이메일이 중복된 경우, BusinessException이 발생하고 Bad Request(400) 상태 코드를 반환하는지 테스트
   */
  @Test
  void createUser_Fail_Duplicated() throws Exception {
    UserRequest userRequest = userRequest(NAME,EMAIL,PASSWORD,PHONE_NUM,ADDRESS);

    doThrow(new BusinessException(ErrorCode.DUPLICATED_USER_EMAIL))
        .when(userService)
        .createUser(any(UserRequest.class));

    ResultActions resultActions = performPostRequest(URL, userRequest);

    resultActions.andExpect(status().isBadRequest());
  }

  /**
   * DB 오류가 발생한 경우, Internal Server Error(500) 상태 코드를 반환하는지 테스트
   */
  @Test
  void createUser_Fail_DB_Exception() throws Exception {

    UserRequest userRequest = userRequest(NAME,EMAIL,PASSWORD,PHONE_NUM,ADDRESS);

    doThrow(new DataAccessException("DB Error") {})
        .when(userService)
        .createUser(any(UserRequest.class));

    ResultActions resultActions = performPostRequest(URL, userRequest);

    resultActions.andExpect(status().isInternalServerError());
  }

  /**
   * 사용자가 성공적으로 생성된 경우, Created(201) 상태 코드를 반환하는지 테스트
   */
  @Test
  void createUser_Success() throws Exception {

    UserRequest userRequest = userRequest(NAME,EMAIL,PASSWORD,PHONE_NUM,ADDRESS);
    UserResponse expectedResponse = userResponse(NAME, EMAIL, PHONE_NUM, ADDRESS);

    doReturn(expectedResponse).when(userService).createUser(any(UserRequest.class));

    ResultActions resultActions = performPostRequest(URL, userRequest);
    resultActions.andExpect(status().isCreated());

    UserResponse actualResponse = gson.fromJson(
        resultActions.andReturn().getResponse().getContentAsString(), UserResponse.class);

    assertUserResponse(expectedResponse, actualResponse);

    verify(userService, times(1)).createUser(any(UserRequest.class));
  }

  /**
   * 존재하지 않는 사용자 조회 시 Not Found(404) 상태 코드를 반환하는지 테스트
   */
  @Test
  void findUser_Fail_null() throws Exception {

    doThrow(new BusinessException(ErrorCode.USERID_NOT_FOUND))
        .when(userService)
        .findUserByUserId(1L);

    ResultActions resultActions = performGetRequest(URL+"/1");
    resultActions.andExpect(status().isNotFound());
  }

  /**
   * 사용자가 성공적으로 조회된 경우, OK(200) 상태 코드를 반환하는지 테스트
   */
  @Test
  void findUser_success() throws Exception {

    UserResponse expectedResponse = userResponse(NAME, EMAIL, PHONE_NUM, ADDRESS);

    doReturn(expectedResponse).when(userService).findUserByUserId(1L);

    ResultActions resultActions = performGetRequest(URL+"/1");
    resultActions.andExpect(status().isOk());

    UserResponse actualResponse = gson.fromJson(
        resultActions.andReturn().getResponse().getContentAsString(), UserResponse.class
    );

    assertUserResponse(expectedResponse, actualResponse);

    verify(userService, times(1)).findUserByUserId(1L);
  }

  /**
   * 존재하지 않는 사용자 정보 업데이트 시 Not Found(404) 상태 코드를 반환하는지 테스트
   */
  @Test
  void updateUser_Fail_null() throws Exception {

    UserRequest userRequest = userRequest(NAME, EMAIL, PASSWORD, PHONE_NUM, ADDRESS);

    doThrow(new BusinessException(ErrorCode.USERID_NOT_FOUND))
        .when(userService)
        .updateUser(eq(1L),any(UserRequest.class));

    ResultActions resultActions = performPatchRequest(URL+"/1", userRequest);
    resultActions.andExpect(status().isNotFound());
  }

  /**
   * 사용자 정보가 성공적으로 업데이트된 경우, OK(200) 상태 코드를 반환하는지 테스트
   */
  @Test
  void updateUser_Success() throws Exception {

    UserRequest userRequest = userRequest(NAME, EMAIL, PASSWORD, PHONE_NUM, ADDRESS);
    UserResponse expectedResponse = userResponse(NAME, EMAIL, PHONE_NUM, ADDRESS);

    doReturn(expectedResponse).when(userService).updateUser(eq(1L),any(UserRequest.class));

    ResultActions resultActions = performPatchRequest(URL+"/1", userRequest);

    resultActions.andExpect(status().isOk());

    UserResponse actualResponse = gson.fromJson(resultActions.andReturn().getResponse().getContentAsString(), UserResponse.class);
    assertUserResponse(expectedResponse, actualResponse);
  }

  // 공통 메서드: UserRequest 객체 생성
  private UserRequest userRequest(String name, String email, String password, String phoneNum, String address) {
    return UserRequest.builder()
        .name(name)
        .email(email)
        .password(password)
        .phoneNum(phoneNum)
        .address(address)
        .build();
  }

  // 공통 메서드: UserResponse 객체 생성
  private UserResponse userResponse(String name, String email, String phoneNum, String address) {
    return UserResponse.builder()
        .name(name)
        .email(email)
        .phoneNum(phoneNum)
        .address(address)
        .build();
  }

  // 공통 메서드: POST 요청 수행
  private ResultActions performPostRequest(String url, Object request) throws Exception {
    return mockMvc.perform(
        MockMvcRequestBuilders.post(url)
            .content(gson.toJson(request))
            .contentType(MediaType.APPLICATION_JSON)
    );
  }

  // 공통 메서드: GET 요청 수행
  private ResultActions performGetRequest(String url) throws Exception {
    return mockMvc.perform(
        MockMvcRequestBuilders.get(url)
    );
  }

  // 공통 메서드: PATCH 요청 수행
  private ResultActions performPatchRequest(String url, Object request) throws Exception {
    return mockMvc.perform(
        MockMvcRequestBuilders.patch(url)
            .content(gson.toJson(request))
            .contentType(MediaType.APPLICATION_JSON)
    );
  }

  // 공통 메서드: UserResponse 검증
  private void assertUserResponse(UserResponse expected, UserResponse actual) {
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getEmail(), actual.getEmail());
    assertEquals(expected.getPhoneNum(), actual.getPhoneNum());
    assertEquals(expected.getAddress(), actual.getAddress());
  }
}
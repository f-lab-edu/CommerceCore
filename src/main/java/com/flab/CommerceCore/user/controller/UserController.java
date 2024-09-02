package com.flab.CommerceCore.user.controller;

import com.flab.CommerceCore.user.domain.dto.UserRequest;
import com.flab.CommerceCore.user.domain.dto.UserResponse;
import com.flab.CommerceCore.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * 새로운 사용자를 생성합니다.
   *
   * @param userRequest 사용자 생성 요청 정보
   * @return 생성된 사용자 정보와 상태 코드 201(Created)
   */
  @PostMapping("/user")
  public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserRequest userRequest) {
    UserResponse userResponse = userService.createUser(userRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
  }

  /**
   * 사용자 ID로 사용자를 조회
   *
   * @param userId 조회할 사용자 ID
   * @return 조회된 사용자 정보와 상태 코드 200(OK)
   */
  @GetMapping("/user/{userId}")
  public ResponseEntity<UserResponse> getUser(@PathVariable("userId") Long userId) {
    UserResponse userResponse = userService.findUserByUserId(userId);
    return ResponseEntity.ok(userResponse);
  }

  /**
   * 사용자 정보를 업데이트합니다.
   *
   * @param userId 업데이트할 사용자 ID
   * @param updateUserRequest 업데이트할 사용자 정보
   * @return 업데이트된 사용자 정보와 상태 코드 200(OK)
   */
  @PatchMapping("/user/{userId}")
  public ResponseEntity<UserResponse> updateUser(@PathVariable("userId") Long userId, @RequestBody @Valid UserRequest updateUserRequest) {
    UserResponse userResponse = userService.updateUser(userId, updateUserRequest);
    return ResponseEntity.ok(userResponse);
  }

}

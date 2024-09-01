package com.flab.CommerceCore.user.service;

import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.common.exceptions.ErrorCode;
import com.flab.CommerceCore.user.domain.dto.UserRequest;
import com.flab.CommerceCore.user.domain.dto.UserResponse;
import com.flab.CommerceCore.user.domain.entity.User;
import com.flab.CommerceCore.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  public UserService(UserRepository userRepository){
    this.userRepository = userRepository;
  }

  private final UserRepository userRepository;

  /**
   * User 생성 메서드
   *
   * @param userRequest 사용자 생성 요청 정보
   * @return 생성된 사용자 정보를 포함한 UserResponse DTO
   * @throws BusinessException 이메일이 중복된 경우
   */
  @Transactional
  public UserResponse createUser(UserRequest userRequest){
    if(userRepository.findByEmail(userRequest.getEmail()) != null){
        throw new BusinessException(ErrorCode.DUPLICATED_USER);
    }

    User user = convertRequestToEntity(userRequest);
    User saveUser = userRepository.save(user);

    return convertEntityToResponse(saveUser);
  }

  /**
   * User 단건 조회 메서드
   *
   * @param userId 조회할 사용자 ID
   * @return 조회된 사용자 정보를 포함한 UserResponse DTO
   * @throws BusinessException 사용자 ID를 찾을 수 없는 경우
   */
  @Transactional
  public UserResponse findUserByUserId(Long userId){

    User findUser = userRepository.findByUserId(userId);

    if(findUser == null){
      throw new BusinessException(ErrorCode.USERID_NOT_FOUND);
    }

    return convertEntityToResponse(findUser);
  }

  /**
   * User 정보 수정 메서드
   *
   * @param userId        수정할 사용자 ID
   * @param userRequest   수정할 사용자 정보
   * @return 수정된 사용자 정보를 포함한 UserResponse DTO
   * @throws BusinessException 사용자 ID를 찾을 수 없는 경우
   */
  @Transactional
  public UserResponse updateUser(Long userId, UserRequest userRequest){
    User findUser = userRepository.findByUserId(userId);
    if(findUser == null){
      throw new BusinessException(ErrorCode.USERID_NOT_FOUND);
    }

    findUser.updateUser(userRequest);
    return convertEntityToResponse(findUser);
  }

  /**
   * UserRequest DTO를 User 엔티티로 변환하는 메서드
   *
   * @param userRequest 사용자 요청 정보
   * @return 변환된 User 엔티티
   */
  private User convertRequestToEntity(UserRequest userRequest) {
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
  private UserResponse convertEntityToResponse(User user) {
    return UserResponse.builder()
        .userId(user.getUserId())
        .name(user.getName())
        .email(user.getEmail())
        .phoneNum(user.getPhoneNum())
        .address(user.getAddress())
        .build();
  }
}

package com.flab.CommerceCore.user.service;

import com.flab.CommerceCore.common.Mapper.UserMapper;
import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.common.exceptions.ErrorCode;
import com.flab.CommerceCore.user.domain.dto.UserRequest;
import com.flab.CommerceCore.user.domain.dto.UserResponse;
import com.flab.CommerceCore.user.domain.entity.User;
import com.flab.CommerceCore.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

  @Autowired
  public UserService(UserRepository userRepository, UserMapper mapper){
    this.userRepository = userRepository;
    this.mapper = mapper;
  }

  private final UserRepository userRepository;
  private final UserMapper mapper;

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
      log.error(ErrorCode.DUPLICATED_USER_EMAIL.getDetail(),userRequest.getEmail());
        throw new BusinessException(ErrorCode.DUPLICATED_USER_EMAIL);
    }

    User user = mapper.convertRequestToEntity(userRequest);
    User saveUser = userRepository.save(user);

    return mapper.convertEntityToResponse(saveUser);
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
      log.error(ErrorCode.USERID_NOT_FOUND.getDetail(),userId);
      throw new BusinessException(ErrorCode.USERID_NOT_FOUND);
    }

    return mapper.convertEntityToResponse(findUser);
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
      log.error(ErrorCode.USERID_NOT_FOUND.getDetail(),userId);
      throw new BusinessException(ErrorCode.USERID_NOT_FOUND);
    }

    return mapper.convertEntityToResponse(findUser.updateUser(userRequest));
  }

}

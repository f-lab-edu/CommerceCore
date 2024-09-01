package com.flab.CommerceCore.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.flab.CommerceCore.user.domain.dto.UserRequest;
import com.flab.CommerceCore.user.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  private User user;

  private static final String NAME = "kim";
  private static final String EMAIL = "kim@gmail.com";
  private static final String PASSWORD = "q1w2e3";
  private static final String PHONE_NUM = "123-456-789";
  private static final String ADDRESS = "강동구";

  @BeforeEach
  void setUp(){
    user = User.builder()
        .name(NAME)
        .email(EMAIL)
        .password(PASSWORD)
        .phoneNum(PHONE_NUM)
        .address(ADDRESS)
        .build();
  }

  @Test
  void saveAndFindUserByEmail(){

    // when
    userRepository.save(user);
    User findUser = userRepository.findByEmail(user.getEmail());

    //then
    assertUser(findUser,NAME,EMAIL,PASSWORD,PHONE_NUM,ADDRESS);
  }

  @Test
  void findUserByUserId(){

    // given
    userRepository.save(user);

    // when
    User findUser = userRepository.findByUserId(user.getUserId());

    // then
    assertUser(findUser,NAME,EMAIL,PASSWORD,PHONE_NUM,ADDRESS);
  }


  @Test
  void updateUserByUserId(){
    // given
    UserRequest updateRequestUser = UserRequest.builder()
        .name("park")
        .email("park@gmail.com")
        .password("q1w2e3")
        .phoneNum("123-456-789")
        .address("계양구")
        .build();

    User saveUser = userRepository.save(user);

    // when
    saveUser.updateUser(updateRequestUser);
    User updateUser = userRepository.findByUserId(saveUser.getUserId());

    // then
    assertUser(updateUser, updateRequestUser.getName(), updateRequestUser.getEmail(),
        updateRequestUser.getPassword(),updateRequestUser.getPhoneNum(), updateRequestUser.getAddress());
  }


  private void assertUser(User user, String name, String email,
      String password, String phoneNum, String address) {
    assertNotNull(user);
    assertEquals(name, user.getName());
    assertEquals(email, user.getEmail());
    assertEquals(password, user.getPassword());
    assertEquals(phoneNum, user.getPhoneNum());
    assertEquals(address, user.getAddress());
  }

}
package com.flab.CommerceCore.user.domain.entity;

import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.common.exceptions.ErrorCode;
import com.flab.CommerceCore.order.domain.entity.Order;
import com.flab.CommerceCore.user.domain.dto.UserRequest;
import jakarta.persistence.*;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Users")
public class User {

    private static final Logger log = LoggerFactory.getLogger(User.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNum;

    @Column(nullable = false)
    private String address;

    private LocalDateTime createTime;

    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

    @Builder
    public User(String name, String email, String password, String phoneNum, String address) {

        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNum = phoneNum;
        this.address = address;
        this.createTime = LocalDateTime.now();
    }

    public User updateUser(UserRequest user){
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.phoneNum = user.getPhoneNum();
        this.address = user.getAddress();
        return this;
    }
}

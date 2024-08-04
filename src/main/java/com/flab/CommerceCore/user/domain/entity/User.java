package com.flab.CommerceCore.user.domain.entity;

import com.flab.CommerceCore.order.domain.entity.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String userName;
    private String userEmail;
    private String password;
    private String phoneNum;
    private String address;
    private LocalDateTime createTime;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;


}

package com.example.shopping.domain.user.entity;

import com.example.shopping.common.entity.TimeStamped;
import com.example.shopping.domain.user.role.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "users")
public class User extends TimeStamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String password;
    private String name;
    private String address;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}

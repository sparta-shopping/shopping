package com.example.shopping.common.dto;

import com.example.shopping.domain.user.role.UserRole;
import lombok.Getter;

import java.util.List;

@Getter
public class AuthUser {

    private final Long id;
    private final String email;
    private final List<UserRole> authority;

    public AuthUser(Long id, String email, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.authority = List.of(userRole);
    }
}

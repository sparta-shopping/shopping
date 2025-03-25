package com.example.shopping.domain.user.repository;

import com.example.shopping.domain.user.entity.User;

import java.util.Optional;

public interface UserRepositoryQuery {

    Optional<User> findUserById(Long userId);

}

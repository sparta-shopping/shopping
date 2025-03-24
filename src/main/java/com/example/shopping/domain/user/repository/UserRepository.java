package com.example.shopping.domain.user.repository;

import com.example.shopping.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryQuery {
    boolean existsUserByEmail(String email);
}

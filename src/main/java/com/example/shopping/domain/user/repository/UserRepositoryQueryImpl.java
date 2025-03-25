package com.example.shopping.domain.user.repository;

import com.example.shopping.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.example.shopping.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryQueryImpl implements UserRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<User> findUserById(Long userId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(user)
                .where(user.id.eq(userId)
                        .and(user.deletedAt.isNull()))
                .fetchOne());
    }


}

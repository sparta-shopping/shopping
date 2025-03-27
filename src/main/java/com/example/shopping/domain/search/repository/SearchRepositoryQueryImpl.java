package com.example.shopping.domain.search.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class SearchRepositoryQueryImpl implements SearchRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;


}

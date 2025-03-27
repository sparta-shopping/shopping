package com.example.shopping.domain.search.repository;

import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.dto.response.ProductResponseDto;
import com.example.shopping.domain.search.dto.response.SearchResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.example.shopping.domain.product.entity.QProduct.product;


@RequiredArgsConstructor
public class SearchRepositoryQueryImpl implements SearchRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<SearchResponseDto> findProductsByKeyword(
            String keyword, Pageable pageable
    ) {
        List<SearchResponseDto> content = jpaQueryFactory
                .select(Projections.constructor(
                        SearchResponseDto.class,
                        product.id,
                        product.name // keyword 대신 product.name 사용
                ))
                .from(product)
                .where(product.name.contains(keyword)) // keyword로 검색
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .selectFrom(product)
                .where(product.name.contains(keyword))
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

}

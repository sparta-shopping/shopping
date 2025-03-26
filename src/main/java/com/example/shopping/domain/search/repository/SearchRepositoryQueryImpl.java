package com.example.shopping.domain.search.repository;

import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.search.dto.response.SearchResponseDto;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.shopping.domain.product.entity.QProduct.product;


@RequiredArgsConstructor
public class SearchRepositoryQueryImpl implements SearchRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<SearchResponseDto> findByNameContaining(String keyword, Pageable pageable) {
        if (keyword == null) {
            throw new IllegalArgumentException("키워드 값 입력은 필수 입니다.");
        }

        keyword = keyword.trim(); // 검색어 앞뒤 공백 제거

        JPQLQuery<Product> query = jpaQueryFactory.selectFrom(product)
                .where(product.name.contains(keyword));

        List<Product> products = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        List<SearchResponseDto> dtos = products.stream()
                .map(product -> new SearchResponseDto(
                        product.getId(),
                        product.getName(),
                        product.getCategory(),
                        product.getPrice(),
                        product.getReviewCount(),
                        product.getStock(),
                        product.getAverageRating(),
                        product.getImageUrl()
                ))
                .toList();

        long total = query.fetchCount();

        return new PageImpl<>(dtos, pageable, total);
    }
}

package com.example.shopping.domain.product.repository;

import com.example.shopping.domain.product.entity.Product;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.example.shopping.domain.product.entity.QProduct.product;

@RequiredArgsConstructor
public class ProductRepositoryQueryImpl implements ProductRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Product> findProductById(Long productId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(product)
                .where(product.id.eq(productId)
                        .and(product.deletedAt.isNull()))
                .fetchOne());
    }
}

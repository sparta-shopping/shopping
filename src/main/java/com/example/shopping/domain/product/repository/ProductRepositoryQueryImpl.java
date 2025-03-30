package com.example.shopping.domain.product.repository;

import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.dto.response.ProductResponseDto;
import com.example.shopping.domain.product.entity.Product;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.dml.SQLInsertClause;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static com.example.shopping.domain.product.entity.QProduct.product;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class ProductRepositoryQueryImpl implements ProductRepositoryQuery {

	private final JPAQueryFactory jpaQueryFactory;
	private final EntityManager entityManager;

	@Override
	public Optional<Product> findProductById(Long productId) {
		return Optional.ofNullable(jpaQueryFactory.selectFrom(product)
			.where(product.id.eq(productId)
				.and(product.deletedAt.isNull()))
			.fetchOne());
	}

	@Override
	public Page<ProductResponseDto> findProductsByCategoryAndKeyword(
		Category category, String keyword, Pageable pageable
	) {
		List<ProductResponseDto> products = jpaQueryFactory
			.select(
				Projections.constructor(
					ProductResponseDto.class,
					product.id,
					product.name,
					product.category,
					product.price,
					product.reviewCount,
					product.stock,
					product.averageRating,
					product.imageUrl,
					product.createdAt,
					product.updatedAt,
					product.deletedAt
				)
			)
			.from(product)
			.where(
				product.category.eq(category),
				product.name.containsIgnoreCase(keyword)
			)
			.orderBy(product.averageRating.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(product.countDistinct())
			.from(product)
			.where(
				product.category.eq(category),
				product.name.containsIgnoreCase(keyword)
			)
			.fetchOne();

		return new PageImpl<>(products, pageable, total == null ? 0L : total);
	}

}

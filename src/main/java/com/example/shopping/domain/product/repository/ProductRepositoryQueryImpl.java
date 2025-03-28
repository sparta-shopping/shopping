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

	@Override
	@Transactional
	public void bulkInsert(List<String> names) {
		// 1. SQLTemplates 초기화 (MySQL 8.x 기준)
		SQLTemplates templates = MySQLTemplates.builder()
				.quote()
				.build();

		// 2. SQL Table 메타데이터 정의
		RelationalPathBase<Object> productTable = new RelationalPathBase<>(
				Object.class,
				"products",  // 실제 테이블 이름
				"public",     // 스키마
				"products"    // 별칭
		);

		// 3. 컬럼 매핑 (실제 DB 컬럼명과 일치해야 함)
		StringPath nameColumn = Expressions.stringPath(productTable, "name");

		// 4. 배치 삽입 실행
		try (Connection conn = entityManager.unwrap(Connection.class)) {
			SQLInsertClause insert = new SQLInsertClause(
					conn,
					new Configuration(templates),
					productTable
			);

			for (String name : names) {
				insert.set(nameColumn, name)
						.addBatch();
			}

			// 500개 단위로 배치 실행
			insert.setBatchToBulk(true);
			insert.execute();
		} catch (SQLException e) {
			throw new RuntimeException("배치 삽입 실패", e);
		}
	}


}

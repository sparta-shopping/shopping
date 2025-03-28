package com.example.shopping.domain.coupon.repository;

import com.example.shopping.domain.coupon.dto.response.CouponResponseDto;
import com.example.shopping.domain.coupon.entity.Coupon;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.example.shopping.domain.coupon.entity.QCoupon.coupon;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;

@RequiredArgsConstructor
public class CouponRepositoryQueryImpl implements CouponRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Coupon> findCouponById(Long couponId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(coupon)
                .where(coupon.id.eq(couponId)
                        .and(coupon.deletedAt.isNull()))
                .fetchOne());
    }

    @Override
    public Page<CouponResponseDto> findAllCoupons(Pageable pageable) {
        List<CouponResponseDto> coupons = jpaQueryFactory
            .select(
                Projections.constructor(
                    CouponResponseDto.class,
                    coupon.id,
                    coupon.name,
                    coupon.discountAmount,
                    coupon.stock,
                    coupon.createdAt,
                    coupon.updatedAt,
                    coupon.deletedAt
                )
            )
            .from(coupon)
            .orderBy(coupon.updatedAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = jpaQueryFactory
            .select(coupon.countDistinct())
            .from(coupon)
            .fetchOne();

        return new PageImpl<>(coupons, pageable, total == null ? 0L : total);
    }
}

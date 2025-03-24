package com.example.shopping.domain.coupon.repository;

import com.example.shopping.domain.coupon.entity.Coupon;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.example.shopping.domain.coupon.entity.QCoupon.coupon;

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
}

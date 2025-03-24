package com.example.shopping.domain.order.repository;

import com.example.shopping.domain.order.entity.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.example.shopping.domain.order.entity.QOrder.order;

@RequiredArgsConstructor
public class OrderRepositoryQueryImpl implements OrderRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Order> findOrderById(Long orderId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(order)
                .where(order.id.eq(orderId)
                        .and(order.deletedAt.isNull()))
                .fetchOne());
    }
}

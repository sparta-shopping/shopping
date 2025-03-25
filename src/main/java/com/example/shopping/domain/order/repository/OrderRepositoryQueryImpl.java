package com.example.shopping.domain.order.repository;

import com.example.shopping.domain.order.entity.Order;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
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
    
    @Override
    public Page<Order> findAllByUserId(Long userId, Pageable pageable) {
        JPAQuery<Order> orders = jpaQueryFactory.selectFrom(order)
            .where(order.user.id.eq(userId)
                .and(order.user.deletedAt.isNull()))
            .orderBy(order.updatedAt.desc());
        
        Long totalCount = Optional.ofNullable(
            jpaQueryFactory.select(order.count())
            .from(order)
            .where(order.user.id.eq(userId)
                .and(order.user.deletedAt.isNull()))
            .fetchOne())
            .orElse(0L);
        
        List<Order> orderList = orders.offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
        
        return PageableExecutionUtils.getPage(orderList, pageable, () -> totalCount);
    }
}

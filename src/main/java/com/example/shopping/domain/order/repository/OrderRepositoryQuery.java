package com.example.shopping.domain.order.repository;

import com.example.shopping.domain.order.entity.Order;

import java.util.Optional;

public interface OrderRepositoryQuery {

    Optional<Order> findOrderById(Long orderId);
}

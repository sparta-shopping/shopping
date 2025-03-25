package com.example.shopping.domain.order.repository;

import com.example.shopping.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderRepositoryQuery {

    Optional<Order> findOrderById(Long orderId);
	
	Page<Order> findAllByUserId(Long userId, Pageable pageable);
}

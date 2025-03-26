package com.example.shopping.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "order_item")
public class OrderItem {
	
	@Id @GeneratedValue(strategy =  GenerationType.IDENTITY)
	private Long id;
	private Long productId;
	private Integer quantity;
	private Integer price;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;
	
	public OrderItem(Long productId, Integer quantity, Integer price) {
		this.productId = productId;
		this.quantity = quantity;
		this.price = price;
	}
	
	public void setOrder(Order order) {
		this.order = order;
		if (!order.getOrderItems().contains(this)) {
			order.getOrderItems().add(this);
		}
	}
}

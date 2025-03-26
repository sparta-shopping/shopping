package com.example.shopping.domain.order.entity;

import com.example.shopping.common.entity.TimeStamped;
import com.example.shopping.domain.coupon.entity.Coupon;
import com.example.shopping.domain.order.state.OrderState;
import com.example.shopping.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "orders")
public class Order extends TimeStamped {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Setter
	@Enumerated(EnumType.STRING)
	private OrderState state;
    
    @Setter
    private Integer totalPrice;
	
	@BatchSize(size = 10)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
    
    @Setter
    @BatchSize(size = 10)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;
    
    public Order(OrderState state, Integer totalPrice, User user, Coupon coupon) {
        this.state = state;
        this.totalPrice = totalPrice;
        this.user = user;
        this.coupon = coupon;
        this.orderItems = new ArrayList<>();
    }
    
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
}
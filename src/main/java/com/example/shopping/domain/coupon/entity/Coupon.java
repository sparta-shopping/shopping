package com.example.shopping.domain.coupon.entity;

import com.example.shopping.common.entity.TimeStamped;
import com.example.shopping.domain.coupon.dto.request.CouponRequestDto;
import com.example.shopping.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "coupons")
public class Coupon extends TimeStamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer discountAmount;

    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Coupon(String name, Integer discountAmount, Integer stock, User user) {
        this.name = name;
        this.discountAmount = discountAmount;
        this.stock = stock;
        this.user = user;
    }

    public void publishCoupon(){ this.stock = stock - 1; }

    public void updateCoupon(CouponRequestDto dto){
        this.name = dto.getCouponName();
        this.discountAmount = dto.getDiscountAmount();
        this.stock = dto.getStock();
    }
}
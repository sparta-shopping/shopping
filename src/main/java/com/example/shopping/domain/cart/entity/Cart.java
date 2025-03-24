package com.example.shopping.domain.cart.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@RedisHash("carts")
public class Cart {

    @Id
    private Long id;

//    private Map<Long, Integer> products = new HashMap<>();
}
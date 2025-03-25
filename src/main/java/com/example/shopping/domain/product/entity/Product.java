package com.example.shopping.domain.product.entity;

import com.example.shopping.common.entity.TimeStamped;
import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.dto.request.ProductRequestDto;
import com.example.shopping.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "products")
public class Product extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private Category category;
	private Double averageRating;
	private Integer price;
	private Integer reviewCount;
	private Integer stock;
	private String imageUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public Product(String name, Category category, Integer price, Integer stock, String imageUrl, User user) {
		this.name = name;
		this.category = category;
		this.price = price;
		this.stock = stock;
		this.imageUrl = imageUrl;
		this.user = user;
	}

	public void updateProduct(ProductRequestDto dto){
		this.name = dto.getName();
		this.category = dto.getCategory();
		this.price = dto.getPrice();
		this.stock = dto.getStock();
	}
}

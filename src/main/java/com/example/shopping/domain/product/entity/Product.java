package com.example.shopping.domain.product.entity;

import com.example.shopping.common.entity.TimeStamped;
import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.dto.request.ProductCreateRequestDto;
import com.example.shopping.domain.product.dto.request.ProductUpdateRequestDto;

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

	public Product(String name, Category category, Integer price, Integer stock, String imageUrl) {
		this.name = name;
		this.category = category;
		this.price = price;
		this.stock = stock;
		this.imageUrl = imageUrl;
	}

	public void updateProduct(ProductUpdateRequestDto dto) {
		this.name = dto.getName();
		this.category = dto.getCategory();
		this.price = dto.getPrice();
		this.stock = dto.getStock();
	}
}

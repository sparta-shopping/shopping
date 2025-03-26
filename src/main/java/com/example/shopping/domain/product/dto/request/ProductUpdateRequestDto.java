package com.example.shopping.domain.product.dto.request;

import com.example.shopping.domain.product.category.Category;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductUpdateRequestDto {

	@NotBlank(message = "제품명은 필수값입니다.")
	private final String name;

	@NotBlank(message = "카테고리는 필수값입니다.")
	private final Category category;

	@NotBlank(message = "가격은 필수값입니다.")
	private final Integer price;

	@NotBlank(message = "재고는 필수값입니다.")
	private final Integer stock;
}

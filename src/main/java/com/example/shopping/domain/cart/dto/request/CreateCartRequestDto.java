package com.example.shopping.domain.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateCartRequestDto {
	
	@NotNull(message = "개수를 선택해주세요.")
	private Integer quantity;
}

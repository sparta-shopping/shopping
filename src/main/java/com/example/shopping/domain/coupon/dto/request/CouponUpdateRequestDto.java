package com.example.shopping.domain.coupon.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponUpdateRequestDto {

	@NotBlank(message = "쿠폰명은 필수값입니다.")
	private final String CouponName;

	@NotBlank(message = "할인가격은 필수값입니다.")
	private final Integer discountAmount;

	@NotBlank(message = "쿠폰재고는 필수값입니다.")
	private final Integer stock;
}

package com.example.shopping.common.dto;

import java.util.List;

import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
public class PageResponseDto<T> {

	// 제네릭으로 자동 형 고정.
	private List<T> content;
	private int page;
	private int size;
	private long totalElements;
	private int totalPages;

	public PageResponseDto(Page<T> entity) {
		this.content = entity.getContent();
		this.page = entity.getNumber();
		this.size = entity.getSize();
		this.totalElements = entity.getTotalElements();
		this.totalPages = entity.getTotalPages();
	}
}

package com.example.shopping.common.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class PageResponseDto<T> {

	// 제네릭으로 자동 형 고정.
	private final List<T> content;
	private final int page;
	private final int size;
	private final long totalElements;
	private final int totalPages;

	public PageResponseDto(Page<T> entity) {
		this.content = entity.getContent();
		this.page = entity.getNumber();
		this.size = entity.getSize();
		this.totalElements = entity.getTotalElements();
		this.totalPages = entity.getTotalPages();
	}
}

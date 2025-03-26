package com.example.shopping.domain.search.repository;

import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.search.dto.response.SearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;


public interface SearchRepositoryQuery {

    Page<SearchResponseDto> findByNameContaining(@Param("keyword") String keyword, Pageable pageable);
}

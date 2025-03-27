package com.example.shopping.domain.search.repository;


import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.dto.response.ProductResponseDto;
import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.search.dto.response.SearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchRepositoryQuery {

    Page<SearchResponseDto> findProductsByKeyword(
            String keyword, Pageable pageable
    );
}

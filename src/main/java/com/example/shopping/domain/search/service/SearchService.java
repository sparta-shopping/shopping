package com.example.shopping.domain.search.service;

import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.product.dto.response.ProductResponseDto;
import com.example.shopping.domain.product.repository.ProductRepository;
import com.example.shopping.domain.search.dto.response.SearchResponseDto;
import com.example.shopping.domain.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;

    @Transactional(readOnly = true)
    public PageResponseDto<SearchResponseDto> findProducts(String keyword, Pageable pageable) {
        Page<SearchResponseDto> products = searchRepository.findProductsByKeyword(
                keyword, pageable
        );

        return new PageResponseDto<>(products);
    }
}

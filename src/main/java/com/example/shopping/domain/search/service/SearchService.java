package com.example.shopping.domain.search.service;

import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.product.repository.ProductRepository;
import com.example.shopping.domain.search.dto.response.SearchResponseDto;
import com.example.shopping.domain.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {

    private final ProductRepository productRepository;
    private final SearchRepository searchRepository;

    @Transactional(readOnly = true)
    public Page<SearchResponseDto> searchProduct(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return searchRepository.findByNameContaining(keyword, pageable);
    }

}

package com.example.shopping.domain.search.controller;

import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.product.dto.response.ProductResponseDto;
import com.example.shopping.domain.search.dto.response.SearchResponseDto;
import com.example.shopping.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<PageResponseDto<SearchResponseDto>> searchProducts(
            @RequestParam String keyword,
            @PageableDefault(page = 1, size = 10) Pageable pageable
    ) {
        Pageable convertPageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
        return ResponseEntity.ok(searchService.findProducts( keyword, convertPageable));
    }
}

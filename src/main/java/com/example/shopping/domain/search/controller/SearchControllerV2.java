package com.example.shopping.domain.search.controller;


import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.search.dto.response.SearchResponseDto;
import com.example.shopping.domain.search.service.SearchServiceV2;
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
@RequestMapping("/api/v2/search")
public class SearchControllerV2 {

    private final SearchServiceV2 searchServiceV2;

    @GetMapping("/api/v2/search")
    public ResponseEntity<PageResponseDto<SearchResponseDto>> searchProductsV2(
            @RequestParam String keyword,
            @PageableDefault(page = 1, size = 10) Pageable pageable
    ) {
        Pageable convertPageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
        return ResponseEntity.ok(searchServiceV2.findProductsV2( keyword, convertPageable));
    }
}

package com.example.shopping.domain.search.service;


import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.search.dto.response.SearchResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
public class SearchServicePerformanceTest {

    @Autowired
    private SearchService searchService;

    @Autowired
    private SearchServiceV2 searchServiceV2;

    private static final String TEST_KEYWORD = "testKeyword";

    @Test
    public void testPerformanceComparison() {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(0, pageSize);

        // V1 성능 측정
        long startTimeV1 = System.currentTimeMillis();
        PageResponseDto<SearchResponseDto> resultV1 = searchService.findProducts(TEST_KEYWORD, pageable);
        long endTimeV1 = System.currentTimeMillis();
        long durationV1 = endTimeV1 - startTimeV1;

        System.out.println("V1 처리 시간: " + durationV1 + "ms");

        // V2 성능 측정
        long startTimeV2 = System.currentTimeMillis();
        PageResponseDto<SearchResponseDto> resultV2 = searchServiceV2.findProductsV2(TEST_KEYWORD, pageable);
        long endTimeV2 = System.currentTimeMillis();
        long durationV2 = endTimeV2 - startTimeV2;

        System.out.println("V2 처리 시간: " + durationV2 + "ms");

        // 결과 비교
        System.out.println("성능 차이: " + (durationV1 - durationV2) + "ms");
    }
}
package com.example.shopping.domain.search.service;

import com.example.shopping.domain.search.dto.response.SearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {

    private static final int MAXIMUM_SAVED_VALUE = 5;
    private static final String SEARCH_KEY = "search::";
    private static final String SEARCH_COUNT_KEY = "search_count::";
    private static final String POPULAR_KEYWORDS_KEY = "popular_keywords";



}

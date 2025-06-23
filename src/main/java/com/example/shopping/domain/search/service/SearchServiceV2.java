package com.example.shopping.domain.search.service;

import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.search.dto.response.SearchResponseDto;
import com.example.shopping.domain.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchServiceV2 {

    private final SearchRepository searchRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    //고려해야할 상황: 캐시만 적용할게 아니라, 다른 부분도 고려해야함.
    @Cacheable(value = "searchResults", key = "#keyword", cacheManager = "redisCacheManager")
    @Transactional(readOnly = true)
    public PageResponseDto<SearchResponseDto> findProductsV2(String keyword, Pageable pageable) {
        Page<SearchResponseDto> products = searchRepository.findProductsByKeyword(keyword,pageable);

        return new PageResponseDto<>(products);
    }

}

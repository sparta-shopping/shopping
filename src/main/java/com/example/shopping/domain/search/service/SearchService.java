package com.example.shopping.domain.search.service;

import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.search.dto.response.SearchResponseDto;
import com.example.shopping.domain.search.entity.Search;
import com.example.shopping.domain.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public void saveSearchKeyword(String keyword) {
        Search search = searchRepository.findByKeyword(keyword)
                .orElse(new Search(keyword));

        if (search.getId() == null) {
            searchRepository.save(search);
        } else {
            search.incrementCount();
            searchRepository.save(search);
        }
    }

    @Transactional(readOnly = true)
    public List<Search> findPopularSearches(int size) {
        return searchRepository.findAllByOrderByCountDesc()
                .stream()
                .limit(size)
                .toList();
    }
}

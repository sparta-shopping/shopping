package com.example.shopping.domain.search.dto.response;

import com.example.shopping.domain.search.entity.Search;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PopularSearchResponseDto {

    private final String keyword;
    private final Integer count;

    public static PopularSearchResponseDto of(Search search) {
        return new PopularSearchResponseDto(
                search.getKeyword(),
                search.getCount()
        );
    }
}

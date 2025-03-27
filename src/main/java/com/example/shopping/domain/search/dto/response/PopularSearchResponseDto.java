package com.example.shopping.domain.search.dto.response;

import com.example.shopping.domain.search.entity.Search;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PopularSearchResponseDto {

    private final Long productId;
    private final String keyword;

    public static PopularSearchResponseDto of(Search search) {
        return new PopularSearchResponseDto(
                search.getId(),
                search.getKeyword()
        );
    }
}

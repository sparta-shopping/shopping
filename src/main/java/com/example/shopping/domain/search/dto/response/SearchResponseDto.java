package com.example.shopping.domain.search.dto.response;

import com.example.shopping.domain.search.entity.Search;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SearchResponseDto {

    private final Long productId;
    private final String keyword;

    public static SearchResponseDto of(Search search) {
        return new SearchResponseDto(
                search.getId(),
                search.getKeyword()
        );
    }
}

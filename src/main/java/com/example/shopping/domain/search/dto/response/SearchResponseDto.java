package com.example.shopping.domain.search.dto.response;

import com.example.shopping.domain.search.entity.Search;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponseDto {

    private Long productId;
    private String keyword;

    public static SearchResponseDto of(Search search) {
        return new SearchResponseDto(
                search.getId(),
                search.getKeyword()
        );
    }
}

package com.example.shopping.domain.search.dto.response;

import com.example.shopping.domain.product.category.Category;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SearchResponseDto {

    private final Long productId;
    private final String name;
    private final Category category;
    private final Integer price;
    private final Integer reviewCount;
    private final Integer stock;
    private final Double averageRating;
    private final String imageUrl;


}

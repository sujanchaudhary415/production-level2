package com.productionPractice.level2.dto.response;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class CategoryResponse {
    private  Long categoryId;
    private  String categoryName;
    private String description;
}

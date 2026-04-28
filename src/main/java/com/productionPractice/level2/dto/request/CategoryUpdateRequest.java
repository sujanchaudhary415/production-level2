package com.productionPractice.level2.dto.request;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryUpdateRequest {

    @Size(min = 6, max = 50, message = "Category name must be 6–50 characters")
    @Pattern(
            regexp = "^[A-Za-z ]+$"
    )
    private String categoryName;

    @Size(min = 6, max = 50, message = "Category name must be 6–50 characters")
    private String description;
}

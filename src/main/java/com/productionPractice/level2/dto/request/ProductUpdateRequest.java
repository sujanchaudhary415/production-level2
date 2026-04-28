package com.productionPractice.level2.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductUpdateRequest {

    private String productName;
    private String image;
    private String description;

    @Positive
    private BigDecimal price;

    @Min(0)
    private Integer quantity;
}

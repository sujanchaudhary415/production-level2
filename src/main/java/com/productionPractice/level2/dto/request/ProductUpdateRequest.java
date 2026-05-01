package com.productionPractice.level2.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductUpdateRequest {

    @Size(min = 6, max = 50, message = "Product name must be 6–50 characters")
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "Only letters and spaces allowed"
    )
    private String productName;
    private String image;
    private String description;

    @Positive
    private BigDecimal price;

    @Min(0)
    private Integer quantity;
}

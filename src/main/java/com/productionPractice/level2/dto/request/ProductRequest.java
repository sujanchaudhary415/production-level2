package com.productionPractice.level2.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {
    @NotBlank(message="Product name must not be blank")
    private String productName;

    @NotBlank(message = "Image must not be blank")
    private String image;

    @NotBlank(message = "Description must not be blank")
    @Size(min = 10,max=35)
    private String description;

    @NotNull(message = "Price must not be empty")
    @Positive
    private BigDecimal price;

    @Min(0)
    private Integer quantity;

}

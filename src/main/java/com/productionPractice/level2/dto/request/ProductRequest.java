package com.productionPractice.level2.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {
    @NotBlank(message = "Product name must not be blank")
    @Size(min = 6, max = 50, message = "Product name must be 6–50 characters")
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "Only letters and spaces allowed"
    )
    private String productName;

    @NotBlank(message = "Image must not be blank")
    private String imageUrl;

    @NotBlank(message = "Description must not be blank")
    @Size(min = 10,max=35)
    private String description;

    @NotNull(message = "Price must not be empty")
    @Positive
    private BigDecimal price;

    @Min(0)
    private Integer quantity;

}

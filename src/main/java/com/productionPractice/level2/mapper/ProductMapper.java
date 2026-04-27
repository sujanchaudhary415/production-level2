package com.productionPractice.level2.mapper;

import com.productionPractice.level2.dto.request.ProductRequest;
import com.productionPractice.level2.dto.response.ProductResponse;
import com.productionPractice.level2.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductRequest request);

    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "category.categoryName", target = "categoryName")
    ProductResponse toResponse(Product product);
}

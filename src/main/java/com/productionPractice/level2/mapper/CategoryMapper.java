package com.productionPractice.level2.mapper;

import com.productionPractice.level2.dto.request.CategoryRequest;
import com.productionPractice.level2.dto.response.CategoryResponse;
import com.productionPractice.level2.entity.Category;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryRequest request);
    CategoryResponse toResponse(Category category);

    // 🔥 Partial update method
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "categoryId", ignore = true)
    void updateCategoryFromDto(CategoryRequest dto, @MappingTarget Category entity);

}

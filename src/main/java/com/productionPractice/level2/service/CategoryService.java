package com.productionPractice.level2.service;

import com.productionPractice.level2.dto.request.CategoryRequest;
import com.productionPractice.level2.dto.request.CategoryUpdateRequest;
import com.productionPractice.level2.dto.response.CategoryResponse;
import com.productionPractice.level2.wrapper.PagedResponse;



public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    PagedResponse<CategoryResponse> getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
    CategoryResponse getCategoryById(Long categoryId);
    CategoryResponse updateCategoryById(Long categoryId, CategoryUpdateRequest request);
    void deleteCategoryById(Long categoryId);
}

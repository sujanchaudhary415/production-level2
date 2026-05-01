package com.productionPractice.level2.service.Impl;

import com.productionPractice.level2.dto.request.CategoryRequest;
import com.productionPractice.level2.dto.request.CategoryUpdateRequest;
import com.productionPractice.level2.dto.response.CategoryResponse;
import com.productionPractice.level2.entity.Category;
import com.productionPractice.level2.exception.BusinessRuleException;
import com.productionPractice.level2.mapper.CategoryMapper;
import com.productionPractice.level2.repository.CategoryRepository;
import com.productionPractice.level2.service.CategoryService;
import com.productionPractice.level2.service.helper.CategoryHelper;
import com.productionPractice.level2.service.helper.CommonHelper;
import com.productionPractice.level2.util.PageableUtil;
import com.productionPractice.level2.util.PaginationUtil;
import com.productionPractice.level2.wrapper.PagedResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"categories", "categoriesPage"})
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryHelper categoryHelper;
    private final CommonHelper commonhelper;

    @Transactional
    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        String normalizedName= commonhelper.normalize(request.getCategoryName());
        categoryHelper.validateDuplicateName(normalizedName,null);

        Category category = categoryMapper.toEntity(request);
        category.setCategoryName(normalizedName);


        Category saved = categoryRepository.save(category);

        return categoryMapper.toResponse(saved);
    }

    @Override
    @Cacheable(
            cacheNames = "categoriesPage",
            key = "#pageNumber + '_' + #pageSize + '_' + #sortBy + '_' + #sortDir"
    )
    public PagedResponse<CategoryResponse> getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortDir
    ) {

        Pageable pageable = PageableUtil.create(pageNumber, pageSize, sortBy, sortDir);

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        List<CategoryResponse> content = categoryPage.getContent()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();

        return PaginationUtil.build(categoryPage, content);
    }


    @Override
    @Cacheable(cacheNames = "categories", key = "#categoryId")
    public CategoryResponse getCategoryById(Long categoryId) {

       Category category= categoryHelper.getCategoryOrThrow(categoryId);
       return categoryMapper.toResponse(category);
    }


    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "categories", key = "#categoryId"),
            @CacheEvict(cacheNames = "categoriesPage", allEntries = true)
    })
    public CategoryResponse updateCategoryById(Long categoryId, CategoryUpdateRequest request) {

        Category category = categoryHelper.getCategoryOrThrow(categoryId);

        if (commonhelper.isNotBlank(request.getCategoryName())) {
            String trimmedName=commonhelper.normalize(request.getCategoryName());
            categoryHelper.validateDuplicateName(trimmedName,categoryId);
        }

        categoryMapper.updateCategoryFromDto(request, category);
        return categoryMapper.toResponse(category);
    }


    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "categories", key = "#categoryId"),
            @CacheEvict(cacheNames = "categoriesPage", allEntries = true)
    })
    public void deleteCategoryById(Long categoryId) {

       Category category=categoryHelper.getCategoryOrThrow(categoryId);

        if (!category.getProducts().isEmpty()) {
            throw new BusinessRuleException("Cannot delete category with products");
        }

        categoryRepository.delete(category);
    }
}
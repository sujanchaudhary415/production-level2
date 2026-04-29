package com.productionPractice.level2.service;

import com.productionPractice.level2.dto.request.CategoryRequest;
import com.productionPractice.level2.dto.request.CategoryUpdateRequest;
import com.productionPractice.level2.dto.response.CategoryResponse;
import com.productionPractice.level2.entity.Category;
import com.productionPractice.level2.exception.BusinessRuleException;
import com.productionPractice.level2.mapper.CategoryMapper;
import com.productionPractice.level2.repository.CategoryRepository;
import com.productionPractice.level2.service.helper.CategoryHelper;
import com.productionPractice.level2.util.PageableUtil;
import com.productionPractice.level2.util.PaginationUtil;
import com.productionPractice.level2.wrapper.PagedResponse;

import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@CacheConfig(cacheNames = {"categories", "categoriesPage"})
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryHelper categoryHelper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper,CategoryHelper categoryHelper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.categoryHelper=categoryHelper;
    }

    // CREATE
    @Transactional
    @Override
    public CategoryResponse createCategory(CategoryRequest request) {

        String name = categoryHelper.normalize(request.getCategoryName());
        categoryHelper.validateDuplicateName(name,null);

        Category category = categoryMapper.toEntity(request);
        category.setCategoryName(name);

        Category saved = categoryRepository.save(category);

        return categoryMapper.toResponse(saved);
    }

    // GET ALL (PAGE CACHE)
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

    // GET BY ID (CACHE FIXED)
    @Override
    @Cacheable(cacheNames = "categories", key = "#categoryId")
    public CategoryResponse getCategoryById(Long categoryId) {

       Category category= categoryHelper.getCategoryOrThrow(categoryId);
       return categoryMapper.toResponse(category);
    }

    // UPDATE (FIXED CACHE CONSISTENCY)
    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "categories", key = "#categoryId"),
            @CacheEvict(cacheNames = "categoriesPage", allEntries = true)
    })
    public CategoryResponse updateCategoryById(Long categoryId, CategoryUpdateRequest request) {

        Category category = categoryHelper.getCategoryOrThrow(categoryId);

        if (categoryHelper.isNotBlank(request.getCategoryName())) {

            String trimmedName=categoryHelper.normalize(request.getCategoryName());
            categoryHelper.validateDuplicateName(trimmedName,categoryId);
        }

        categoryMapper.updateCategoryFromDto(request, category);
        return categoryMapper.toResponse(category);
    }

    // DELETE (CACHE SAFE)
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
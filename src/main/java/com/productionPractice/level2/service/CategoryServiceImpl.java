package com.productionPractice.level2.service;

import com.productionPractice.level2.dto.request.CategoryRequest;
import com.productionPractice.level2.dto.response.CategoryResponse;
import com.productionPractice.level2.entity.Category;
import com.productionPractice.level2.exception.DuplicateErrorException;
import com.productionPractice.level2.exception.ResourceNotFoundException;
import com.productionPractice.level2.mapper.CategoryMapper;
import com.productionPractice.level2.repository.CategoryRepository;
import com.productionPractice.level2.util.PaginationUtil;
import com.productionPractice.level2.wrapper.PagedResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper)
    {
        this.categoryRepository=categoryRepository;
        this.categoryMapper=categoryMapper;
    }

    @Transactional
    @Override
    public CategoryResponse createCategory(CategoryRequest request) {

        String name=request.getCategoryName().trim();

        if(categoryRepository.existsByCategoryNameIgnoreCase(name))
        {
            throw new DuplicateErrorException("category with name: "+name+" already exist");
        }

        //convert DTO to Entity
        Category category= categoryMapper.toEntity(request);
        category.setCategoryName(name);
        Category saved= categoryRepository.save(category);

        //convert Entity to Response
        return categoryMapper.toResponse(saved);
    }

    @Override
    public PagedResponse<CategoryResponse>getAllCategories(Integer pageNumber,Integer pageSize, String sortBy, String sortDir) {
        Sort sort=sortDir.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        Pageable pageable= PageRequest.of(pageNumber,pageSize,sort);
        Page<Category>categoryPage=categoryRepository.findAll(pageable);

        List<CategoryResponse>content= categoryPage.getContent().stream().map(categoryMapper::toResponse).toList();


        return PaginationUtil.build(categoryPage,content);
    }

    @Override
    public CategoryResponse getCategoryById(Long categoryId) {
        Category category=categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));
        return categoryMapper.toResponse(category);
    }

    @Transactional
    @Override
    public CategoryResponse updateCategoryById(Long categoryId, CategoryRequest request) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        String name = request.getCategoryName();

        if (name != null) {
            String trimmedName = name.trim();

            if (!trimmedName.equalsIgnoreCase(category.getCategoryName()) &&
                    categoryRepository.existsByCategoryNameIgnoreCase(trimmedName)) {

                throw new DuplicateErrorException("Category name already exists");
            }

            request.setCategoryName(trimmedName);
        }

        categoryMapper.updateCategoryFromDto(request, category);

        return categoryMapper.toResponse(category);
    }
}

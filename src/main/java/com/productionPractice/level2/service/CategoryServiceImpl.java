package com.productionPractice.level2.service;

import com.productionPractice.level2.dto.request.CategoryRequest;
import com.productionPractice.level2.dto.response.CategoryResponse;
import com.productionPractice.level2.entity.Category;
import com.productionPractice.level2.exception.DuplicateErrorException;
import com.productionPractice.level2.mapper.CategoryMapper;
import com.productionPractice.level2.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
}

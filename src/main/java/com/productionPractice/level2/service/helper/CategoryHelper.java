package com.productionPractice.level2.service.helper;

import com.productionPractice.level2.entity.Category;
import com.productionPractice.level2.exception.DuplicateErrorException;
import com.productionPractice.level2.exception.ResourceNotFoundException;
import com.productionPractice.level2.repository.CategoryRepository;
import org.springframework.stereotype.Component;


@Component
public class CategoryHelper {

    private  final CategoryRepository categoryRepository;

    public CategoryHelper(CategoryRepository categoryRepository)
    {
        this.categoryRepository=categoryRepository;
    }

    public  String normalize(String name){
        return name.trim().replaceAll("\\s+"," ");
    }

    public  void validateDuplicateName(String name,Long excludeId)
    {
        boolean exists=(excludeId==null)
                       ?categoryRepository.existsByCategoryNameIgnoreCase(name)
                       :categoryRepository.existsByCategoryNameIgnoreCaseAndCategoryIdNot(name,excludeId);

        if(exists)
        {
            throw new DuplicateErrorException("Category name already Exist");
        }
    }

    public Category getCategoryOrThrow(Long categoryId)
    {
        return categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));
    }
    public boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}

package com.productionPractice.level2.service.helper;

import com.productionPractice.level2.entity.Category;
import com.productionPractice.level2.entity.Product;
import com.productionPractice.level2.exception.DuplicateErrorException;
import com.productionPractice.level2.exception.ResourceNotFoundException;
import com.productionPractice.level2.repository.CategoryRepository;
import com.productionPractice.level2.repository.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class ProductHelper {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductHelper(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public void validateDuplicateName(String name, Long excludeId) {
        boolean exists = (excludeId == null)
                ? productRepository.existsByProductNameIgnoreCase(name)
                : productRepository.existsByProductNameIgnoreCaseAndProductIdNot(name, excludeId);

        if (exists) {
            throw new DuplicateErrorException("Product already exist");
        }
    }


    public Product getProductOrThrow(Long productID) {
        return productRepository.findById(productID).orElseThrow(
                () -> new ResourceNotFoundException("Product", "ProductId", productID));
    }

    public Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
    }
}

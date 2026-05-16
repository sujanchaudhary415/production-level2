package com.productionPractice.level2.service.helper;


import com.productionPractice.level2.entity.Product;
import com.productionPractice.level2.exception.DuplicateErrorException;
import com.productionPractice.level2.exception.ResourceNotFoundException;
import com.productionPractice.level2.repository.CategoryRepository;
import com.productionPractice.level2.repository.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class ProductHelper {
    private final ProductRepository productRepository;

    public ProductHelper(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;

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

}

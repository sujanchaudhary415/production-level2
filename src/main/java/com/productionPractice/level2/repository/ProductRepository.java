package com.productionPractice.level2.repository;

import com.productionPractice.level2.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByProductName(String productName);
    Page<Product> findByCategory_CategoryId(Long categoryId, Pageable pageable);
    boolean existsByProductNameIgnoreCaseAndProductIdNot(String name, Long productId);
    Page<Product> findByProductNameContainingIgnoreCase(String keyword, Pageable pageable);
}

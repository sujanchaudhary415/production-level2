package com.productionPractice.level2.service;

import com.productionPractice.level2.dto.request.ProductRequest;
import com.productionPractice.level2.dto.response.ProductResponse;
import com.productionPractice.level2.wrapper.PagedResponse;

public interface ProductService {
    ProductResponse addProduct(Long categoryId, ProductRequest request);
    PagedResponse<ProductResponse> getAllProducts(Integer pageNumber,Integer pageSize,String sortBy,String sortDir);
    PagedResponse<ProductResponse> getProductsByCategory(Long categoryId,Integer pageNumber,Integer pageSize,String sortBy,String sortDir);
    ProductResponse getProductById(Long productId);
    ProductResponse updateProductById(Long productId,ProductRequest request);
}

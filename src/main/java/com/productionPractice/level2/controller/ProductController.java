package com.productionPractice.level2.controller;

import com.productionPractice.level2.constant.AppConstant;
import com.productionPractice.level2.dto.request.ProductRequest;
import com.productionPractice.level2.dto.response.ProductResponse;
import com.productionPractice.level2.service.ProductService;
import com.productionPractice.level2.wrapper.ApiResponse;
import com.productionPractice.level2.wrapper.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService)
    {
        this.productService=productService;
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ApiResponse<ProductResponse>> addProduct(@PathVariable Long categoryId,@Valid @RequestBody ProductRequest request)
    {
        ProductResponse response= productService.addProduct(categoryId,request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response,"Product added successfully"));
    }

    @GetMapping("/public/products")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> getAllCategories(@RequestParam(name="pageNumber",defaultValue = AppConstant.PAGE_NUMBER, required = false)Integer pageNumber,
                                                                                        @RequestParam(name="pageSize",defaultValue=AppConstant.PAGE_SIZE,required = false)Integer pageSize,
                                                                                        @RequestParam(name="sortBy",defaultValue = AppConstant.SORT_BY_PRODUCT,required = false)String sortBy,
                                                                                        @RequestParam(name = "sortDir",defaultValue = AppConstant.SORT_DIR,required=false) String sortDir)
    {
        PagedResponse<ProductResponse>response=productService.getAllProducts(pageNumber,pageSize,sortBy,sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response,"All product fetched successfully"));
    }

    @GetMapping("/public/products/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long productId)
    {
        ProductResponse response=productService.getProductById(productId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response,"Product with id: "+productId+ " fetched successfully"));
    }

}

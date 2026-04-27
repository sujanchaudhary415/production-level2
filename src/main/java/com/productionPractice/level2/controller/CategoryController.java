package com.productionPractice.level2.controller;

import com.productionPractice.level2.constant.AppConstant;
import com.productionPractice.level2.dto.request.CategoryRequest;
import com.productionPractice.level2.dto.response.CategoryResponse;
import com.productionPractice.level2.service.CategoryService;
import com.productionPractice.level2.wrapper.ApiResponse;
import com.productionPractice.level2.wrapper.PagedResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")

public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService)
    {
        this.categoryService=categoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@RequestBody @Valid CategoryRequest request)
    {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response,"Category added successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<CategoryResponse>>> getAllCategories( @RequestParam(name="pageNumber",defaultValue = AppConstant.PAGE_NUMBER, required = false)Integer pageNumber,
                                                                                          @RequestParam(name="pageSize",defaultValue=AppConstant.PAGE_SIZE,required = false)Integer pageSize,
                                                                                          @RequestParam(name="sortBy",defaultValue = AppConstant.SORT_BY_CATEGORY,required = false)String sortBy,
                                                                                          @RequestParam(name = "sortDir",defaultValue = AppConstant.SORT_DIR,required=false) String sortDir)
    {
        PagedResponse<CategoryResponse> response = categoryService.getAllCategories(pageNumber,pageSize,sortBy,sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response,"Category Fetched successfully"));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable @Positive Long categoryId)
    {
        CategoryResponse response=categoryService.getCategoryById(categoryId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response,"Category fetched successfully"));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategoryById(@PathVariable @Positive Long categoryId,@RequestBody CategoryRequest request)
    {
        CategoryResponse response=categoryService.updateCategoryById(categoryId,request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response,"Category Updated successfully"));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategoryById (@PathVariable @Positive Long categoryId)
    {
        categoryService.deleteCategoryById(categoryId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Category deleted successfully"));
    }
}

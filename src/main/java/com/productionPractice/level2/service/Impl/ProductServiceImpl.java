package com.productionPractice.level2.service.Impl;

import com.productionPractice.level2.dto.request.ProductRequest;
import com.productionPractice.level2.dto.request.ProductUpdateRequest;
import com.productionPractice.level2.dto.response.ProductResponse;
import com.productionPractice.level2.entity.Category;
import com.productionPractice.level2.entity.Product;
import com.productionPractice.level2.enums.ProductSortType;
import com.productionPractice.level2.exception.BusinessRuleException;
import com.productionPractice.level2.exception.DuplicateErrorException;
import com.productionPractice.level2.exception.ResourceNotFoundException;
import com.productionPractice.level2.mapper.ProductMapper;
import com.productionPractice.level2.repository.CategoryRepository;
import com.productionPractice.level2.repository.ProductRepository;
import com.productionPractice.level2.service.ProductService;
import com.productionPractice.level2.service.helper.CommonHelper;
import com.productionPractice.level2.service.helper.ProductHelper;
import com.productionPractice.level2.util.PageableUtil;
import com.productionPractice.level2.util.PaginationUtil;
import com.productionPractice.level2.wrapper.PagedResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"products", "productsPage"})
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ProductHelper productHelper;
    private final CommonHelper commonHelper;

    @Transactional
    @Override
    public ProductResponse addProduct(Long categoryId, ProductRequest request) {

       String normalizedName= commonHelper.normalize(request.getProductName());
       productHelper.validateDuplicateName(normalizedName,null);
       Category category=productHelper.getCategoryOrThrow(categoryId);

       Product product=productMapper.toEntity(request);
       product.setProductName(normalizedName);
       product.setCategory(category);

       // optional but recommended (bidirectional sync) // if you have List<Product> in Category
        category.getProducts().add(product);

        Product savedProduct=productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }
    @Override
    @Cacheable(
            cacheNames = "productsPage",
            key = "#pageNumber + '_' + #pageSize + '_' + #sortBy + '_' + #sortDir"
    )
    public PagedResponse<ProductResponse> getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {

        Pageable pageable= PageableUtil.create(pageNumber,pageSize,sortBy,sortDir);
        Page<Product>productPage=productRepository.findAll(pageable);
        List<ProductResponse> content=productPage.getContent().stream().map(productMapper::toResponse).toList();
        return PaginationUtil.build(productPage,content);
    }


    @Override
    @Cacheable(cacheNames = "products", key = "#productId")
    public ProductResponse getProductById(Long productId) {
        Product product=productHelper.getProductOrThrow(productId);
        return productMapper.toResponse(product);
    }

    @Override
    public PagedResponse<ProductResponse> getProductsByCategory(Long categoryId,Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {

        Pageable pageable=PageableUtil.create(pageNumber,pageSize,sortBy,sortDir);
        Category category=categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));
        Page<Product>productPage=productRepository.findByCategory_CategoryId(category.getCategoryId(),pageable);
        List<ProductResponse>content=productPage.getContent().stream().map(productMapper::toResponse).toList();

        return PaginationUtil.build(productPage,content);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "products", key = "#productId"),
            @CacheEvict(cacheNames = "productsPage", allEntries = true)
    })
    public ProductResponse updateProductById(Long productId, ProductUpdateRequest request) {

        Product product = productHelper.getProductOrThrow(productId);

        String trimmedName = null;
        if (commonHelper.isNotBlank(request.getProductName())) {

            trimmedName = commonHelper.normalize(request.getProductName());
            productHelper.validateDuplicateName(trimmedName, productId);
        }

        productMapper.updateProductFromDto(request, product);
        product.setProductName(trimmedName);

        return productMapper.toResponse(product);
    }

    @Override
    public PagedResponse<ProductResponse> getProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy) {

        if (commonHelper.isNotBlank(keyword)) {
            throw new BusinessRuleException("Keyword must not be empty");
        }

        String cleanedKeyword = commonHelper.normalize(keyword);

        ProductSortType sortType = ProductSortType.from(sortBy);
        Sort sort = sortType.toSort();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> productPage = productRepository.findByProductNameContainingIgnoreCase(cleanedKeyword, pageable);
        List<ProductResponse> content = productPage.getContent()
                .stream()
                .map(productMapper::toResponse)
                .toList();

        return PaginationUtil.build(productPage, content);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "products", key = "#productId"),
            @CacheEvict(cacheNames = "productsPage", allEntries = true)
    })
    public void deleteProductById(Long productId) {
        Product product=productHelper.getProductOrThrow(productId);
        productRepository.delete(product);
    }
}

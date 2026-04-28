package com.productionPractice.level2.service;

import com.productionPractice.level2.dto.request.ProductRequest;
import com.productionPractice.level2.dto.response.ProductResponse;
import com.productionPractice.level2.entity.Category;
import com.productionPractice.level2.entity.Product;
import com.productionPractice.level2.exception.DuplicateErrorException;
import com.productionPractice.level2.exception.ResourceNotFoundException;
import com.productionPractice.level2.mapper.ProductMapper;
import com.productionPractice.level2.repository.CategoryRepository;
import com.productionPractice.level2.repository.ProductRepository;
import com.productionPractice.level2.util.PaginationUtil;
import com.productionPractice.level2.wrapper.PagedResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ProductMapper productMapper)
    {
        this.productRepository=productRepository;
        this.categoryRepository=categoryRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    @Override
    public ProductResponse addProduct(Long categoryId, ProductRequest request) {
        if(productRepository.existsByProductName(request.getProductName()))
        {
            throw new DuplicateErrorException("Product with name "+request.getProductName()+" already exist");
        }

        Category category=categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","CategoryId",categoryId));

        //DTO to Entity
        Product product=productMapper.toEntity(request);
        //Establish relation
        product.setCategory(category);

        // optional but recommended (bidirectional sync) // if you have List<Product> in Category
        category.getProducts().add(product);

        Product savedProduct=productRepository.save(product);

        //Entity to response
        return productMapper.toResponse(savedProduct);
    }

    @Override
    public PagedResponse<ProductResponse> getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sort= sortDir.equalsIgnoreCase("asc")
                           ?Sort.by(sortBy).ascending()
                           :Sort.by(sortBy).descending();
        Pageable pageable= PageRequest.of(pageNumber,pageSize,sort);
        Page<Product>productPage=productRepository.findAll(pageable);

        List<ProductResponse> content=productPage.getContent().stream().map(productMapper::toResponse).toList();

        return PaginationUtil.build(productPage,content);
    }

    @Transactional
    @Override
    public ProductResponse getProductById(Long productId) {
        Product product=productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));
        return productMapper.toResponse(product);
    }

    @Override
    public PagedResponse<ProductResponse> getProductsByCategory(Long categoryId,Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sort=sortDir.equalsIgnoreCase("asc")
                          ? Sort.by(sortBy).ascending()
                          : Sort.by(sortBy).descending();
        Pageable pageable=PageRequest.of(pageNumber,pageSize,sort);
        Category category=categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));
        Page<Product>productPage=productRepository.findByCategory_CategoryId(category.getCategoryId(),pageable);
        List<ProductResponse>content=productPage.getContent().stream().map(productMapper::toResponse).toList();

        return PaginationUtil.build(productPage,content);
    }

    @Transactional
    @Override
    public ProductResponse updateProductById(Long productId, ProductRequest request) {

        // 1. Fetch existing product
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product", "productId", productId));

        // 2. Validate product name (if provided)
        if (request.getProductName() != null) {

            String trimmedName = request.getProductName().trim();

            boolean exists = productRepository.existsByProductNameIgnoreCaseAndProductIdNot(trimmedName, productId);

            if (exists) {
                throw new DuplicateErrorException("Product name already exists");
            }
        }

        // 3. Map all fields (partial update supported)
        productMapper.updateProductFromDto(request, product);

        // 5. Return response
        return productMapper.toResponse(product);
    }
}

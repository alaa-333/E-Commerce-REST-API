package com.ecommerce.api.service;


import com.ecommerce.api.dto.request.category.CreateCategoryRequest;
import com.ecommerce.api.dto.response.CategoryResponse;
import com.ecommerce.api.dto.response.PagedResponse;
import com.ecommerce.api.entity.Category;
import com.ecommerce.api.exception.EcommerceAppException;
import com.ecommerce.api.exception.ErrorCode;
import com.ecommerce.api.mapper.CategoryMapper;
import com.ecommerce.api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse createCategory(CreateCategoryRequest request) {

        if (categoryRepository.existsByName(request.getName())) {
            throw new EcommerceAppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        var category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .active(true)
                .productCount(0)
                .build();

        try {
            var savedCategory = categoryRepository.save(category);
            return categoryMapper.toCategoryResponse(savedCategory);
        } catch (DataIntegrityViolationException ex) {
            throw new EcommerceAppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }


    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public CategoryResponse getCategoryById(Long id) {

            var category = categoryRepository.findById(id)
                    .orElseThrow(() -> new EcommerceAppException(ErrorCode.CATEGORY_NOT_FOUND));

            return categoryMapper.toCategoryResponse(category);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public PagedResponse<CategoryResponse> getCategories(int page, int size) {

        var pageRequest = PageRequest.of(page, size);
        var pageResponse = categoryRepository.findAll(pageRequest)
                .map(categoryMapper::toCategoryResponse);

        return PagedResponse.from(pageResponse);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteCategory(Long id) {

        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new EcommerceAppException(ErrorCode.CATEGORY_NOT_FOUND));

        if (category.getProductCount() > 0) {
            throw new EcommerceAppException(ErrorCode.CATEGORY_DELETE_FAILED);
        }

        int effectedRows = categoryRepository.deleteCategoryById(id);

        return effectedRows > 0;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public boolean updateCategory(Long categoryId, String name, String description) {



        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EcommerceAppException(ErrorCode.CATEGORY_NOT_FOUND));

        if (name != null) {
            if(category.getName().equals(name)) {
                throw new EcommerceAppException(ErrorCode.CATEGORY_ALREADY_EXISTS, "the new category name is the same as the current one");
            }

            if (categoryRepository.existsByName(name)) {
                throw new EcommerceAppException(ErrorCode.CATEGORY_ALREADY_EXISTS, "the new category name is already taken by another category");
            }

            category.setName(name);
        }


        if (description != null) {
            category.setDescription(description);
        }

        int effectedRows = categoryRepository.updateCategory(categoryId , category.getName(), category.getDescription());

        return effectedRows > 0;


    }
}

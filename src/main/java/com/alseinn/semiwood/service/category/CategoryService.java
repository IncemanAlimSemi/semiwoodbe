package com.alseinn.semiwood.service.category;

import com.alseinn.semiwood.request.category.CreateOrUpdateCategoryRequest;
import com.alseinn.semiwood.response.category.*;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    GeneralInformationResponse createCategory(CreateOrUpdateCategoryRequest request);
    GeneralInformationResponse updateCategory(CreateOrUpdateCategoryRequest request);
    PageableCategoryResponse getAllCategories(Pageable pageable);
    SetCategoryResponse getAllCategoriesByIsActiveTrue();
    CategoryResponse getCategoryById(Long id);
    CategoryWithPageableProductResponse getCategoryWithPageableProduct(Long id, Pageable pageable);
    GeneralInformationResponse deleteCategoryById(Long id);
}

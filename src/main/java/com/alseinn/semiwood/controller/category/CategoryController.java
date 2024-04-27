package com.alseinn.semiwood.controller.category;

import com.alseinn.semiwood.request.category.CreateOrUpdateCategoryRequest;
import com.alseinn.semiwood.response.category.CategoryResponse;
import com.alseinn.semiwood.response.category.CategoryWithPageableProductResponse;
import com.alseinn.semiwood.response.category.PageableCategoryResponse;
import com.alseinn.semiwood.response.category.SetCategoryResponse;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${default.api.path}/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping()
    public GeneralInformationResponse createCategory(@RequestBody CreateOrUpdateCategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @PutMapping()
    public GeneralInformationResponse updateCategory(@RequestBody CreateOrUpdateCategoryRequest request) {
        return categoryService.updateCategory(request);
    }

    @GetMapping()
    public PageableCategoryResponse getAllCategories(@RequestParam(name = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("name").ascending());
        return categoryService.getAllCategories(pageable);
    }

    @GetMapping("/active")
    public SetCategoryResponse getAllCategoriesByIsActiveTrue() {
        return categoryService.getAllCategoriesByIsActiveTrue();
    }

    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/{id}/{page}")
    public CategoryWithPageableProductResponse getCategoryWithPageableProduct(@PathVariable Long id, @PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("name").ascending());
        return categoryService.getCategoryWithPageableProduct(id, pageable);
    }

    @DeleteMapping("/{id}")
    public GeneralInformationResponse deleteCategoryById(@PathVariable Long id) {
        return categoryService.deleteCategoryById(id);
    }
}
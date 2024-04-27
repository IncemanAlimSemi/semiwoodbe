package com.alseinn.semiwood.service.category.impl;

import com.alseinn.semiwood.dao.category.CategoryRepository;
import com.alseinn.semiwood.dao.product.ProductRepository;
import com.alseinn.semiwood.entity.category.Category;
import com.alseinn.semiwood.entity.product.Product;
import com.alseinn.semiwood.entity.user.User;
import com.alseinn.semiwood.request.category.CreateOrUpdateCategoryRequest;
import com.alseinn.semiwood.response.category.*;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.response.product.ProductDetail;
import com.alseinn.semiwood.service.category.CategoryService;
import com.alseinn.semiwood.utils.ResponseUtils;
import com.alseinn.semiwood.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserUtils userUtils;
    private final ResponseUtils responseUtils;

    private static final Logger LOG = Logger.getLogger(CategoryServiceImpl.class.getName());

    @Override
    public GeneralInformationResponse createCategory(CreateOrUpdateCategoryRequest request) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            Optional<Category> optionalCategory = categoryRepository.findByName(request.getName());

            if (optionalCategory.isPresent()) {
                LOG.info(responseUtils.getMessage("category.exists.message") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("category.exists.message"));
            }

            List<Product> products = Objects.nonNull(request.getProductsId()) ? productRepository.findAllById(request.getProductsId()) : Collections.emptyList();

            Category category = createCategoryModel(request);

            if (!products.isEmpty()) {
                category.setProducts(new HashSet<>(products));
                products.forEach(product -> product.getCategories().add(category));
            }

            categoryRepository.save(category);

            LOG.info(responseUtils.getMessage("category.created.with.success") + "- Request: " + request);
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("category.created.with.success"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("category.create.error") + "- Request: " + request + "- Exception: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("category.create.error"));
        }
    }

    @Override
    public GeneralInformationResponse updateCategory(CreateOrUpdateCategoryRequest request) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Request: " + request.getId());
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            Optional<Category> optionalCategory = categoryRepository.findById(request.getId());

            if (optionalCategory.isEmpty()) {
                LOG.info(responseUtils.getMessage("category.not.exists.message") + "- Request: " + request.getId());
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("category.not.exists.message"));
            }

            List<Product> products = Objects.nonNull(request.getProductsId()) ? productRepository.findAllById(request.getProductsId()) : Collections.emptyList();

            if (products.isEmpty()) {
                LOG.info(responseUtils.getMessage("product.not.found") + "- Request: " + request.getId());
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("product.not.found"));
            }

            Category category = optionalCategory.get();
            setProductsCategory(category, products);
            category.setDescription(request.getDescription());
            category.setIsActive(request.getIsActive());
            category.setProducts(new HashSet<>(products));

            categoryRepository.save(category);

            LOG.info(responseUtils.getMessage("category.updated.with.success") + "- Request: " + request);
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("category.updated.with.success"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("category.update.error") + "- Request: " + request + "- Exception: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("category.update.error"));
        }
    }

    @Override
    public PageableCategoryResponse getAllCategories(Pageable pageable) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Pageable: " + pageable);
                return createPageableCategoryResponse(false, responseUtils.getMessage("invalid.request"), null);
            }

            Page<Category> categories = categoryRepository.findAll(pageable);

            if (categories.isEmpty()) {
                LOG.info(responseUtils.getMessage("category.not.found") + "- Pageable: " + pageable);
                return createPageableCategoryResponse(false, responseUtils.getMessage("category.not.found"), null);
            }

            LOG.info(responseUtils.getMessage("categories.fetched.success") + "- Pageable: " + pageable);
            return createPageableCategoryResponse(true, responseUtils.getMessage("categories.fetched.success"), categories);
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("categories.fetch.error") + "- Pageable: " + pageable + "- Exception: " + e.getMessage());
            return createPageableCategoryResponse(false, responseUtils.getMessage("categories.fetch.error"), null);
        }
    }

    @Override
    public SetCategoryResponse getAllCategoriesByIsActiveTrue() {
        try {
            List<Category> categories = categoryRepository.findAllByIsActive(true);

            if (categories.isEmpty()) {
                LOG.info(responseUtils.getMessage("active.category.not.found"));
                return createSetCategoryResponse(false, responseUtils.getMessage("active.category.not.found"), null);
            }

            LOG.info(responseUtils.getMessage("active.categories.fetched.success"));
            return createSetCategoryResponse(true, responseUtils.getMessage("active.categories.fetched.success"), categories);
        } catch (Exception e) {
            LOG.info(responseUtils.getMessage("active.categories.fetched.success") + "- Exception: " + e.getMessage());
            return createSetCategoryResponse(false, responseUtils.getMessage("active.categories.fetched.success"), null);
        }
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Id: " + id);
                return createCategoryResponse(false, responseUtils.getMessage("invalid.request"), null);
            }

            Optional<Category> category = categoryRepository.findById(id);

            if (category.isEmpty()) {
                LOG.info(responseUtils.getMessage("category.not.found") + "- Id: " + id);
                return createCategoryResponse(false, responseUtils.getMessage("category.not.found"), null);
            }

            LOG.info(responseUtils.getMessage("category.fetch.success") + "- Id: " + id);
            return createCategoryResponse(true, responseUtils.getMessage("category.fetch.success"), category.get());
        } catch (Exception e) {
            LOG.info(responseUtils.getMessage("category.fetch.error") + "- Id: " + id + "- Exception: " + e.getMessage());
            return createCategoryResponse(false, responseUtils.getMessage("category.fetch.error"), null);
        }
    }

    @Override
    public CategoryWithPageableProductResponse getCategoryWithPageableProduct(Long id, Pageable pageable) {
        try {
            Optional<Category> optionalCategory = categoryRepository.findByIdAndIsActive(id, true);

            if (optionalCategory.isEmpty()) {
                LOG.info(responseUtils.getMessage("category.not.found") + "- Id: " + id);
                return createCategoryWithPageableProductResponse(false, responseUtils.getMessage("category.not.found"), null, null);
            }

            Category category = optionalCategory.get();
            Page<Product> products = productRepository.findAllByCategoriesInAndIsActive(new HashSet<>(List.of(category)), true, pageable);

            LOG.info(responseUtils.getMessage("category.fetch.success") + "- Id: " + id);
            return createCategoryWithPageableProductResponse(true, responseUtils.getMessage("category.fetch.success"), category, products);
        } catch (Exception e) {
            LOG.info(responseUtils.getMessage("category.fetch.error") + "- Id: " + id);
            return createCategoryWithPageableProductResponse(true, responseUtils.getMessage("category.fetch.error"), null, null);
        }
    }

    @Override
    public GeneralInformationResponse deleteCategoryById(Long id) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Id: " + id);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            Optional<Category> optionalCategory = categoryRepository.findById(id);

            if (optionalCategory.isEmpty()) {
                LOG.info(responseUtils.getMessage("category.not.exists.message") + "- Id: " + id);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("category.not.exists.message"));
            }

            Category category = optionalCategory.get();
            Set<Product> products = category.getProducts();

            if (!products.isEmpty()) {
                products.forEach(product -> product.getCategories().remove(category));
            }

            categoryRepository.delete(optionalCategory.get());

            LOG.info(responseUtils.getMessage("category.delete.success") + "- Id: " + id);
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("category.delete.success"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("category.delete.error") + "- Id: " + id + "- Error: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("category.delete.error"));
        }
    }

    private Category createCategoryModel(CreateOrUpdateCategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isActive(request.getIsActive())
                .timeCreated(new Date())
                .timeModified(new Date())
                .build();
    }

    private void setProductsCategory(Category category, List<Product> products) {
        Set<Product> currentCategoryProducts = category.getProducts().stream().filter(product -> !products.contains(product)).collect(Collectors.toSet());
        List<Product> filteredProducts = products.stream().filter(product -> !category.getProducts().contains(product)).toList();

        if (!currentCategoryProducts.isEmpty()) {
            currentCategoryProducts.forEach(product -> {
                Set<Category> categories = product.getCategories();
                categories.remove(category);
                product.setCategories(categories);
            });
        }

        if (!filteredProducts.isEmpty()) {
            filteredProducts.forEach(product -> {
                Set<Category> categories = product.getCategories();
                if (!categories.contains(category)) {
                    categories.add(category);
                    product.setCategories(categories);
                }
            });
        }
    }

    private PageableCategoryResponse createPageableCategoryResponse(Boolean isSuccess, String message, Page<Category> categories) {
        return PageableCategoryResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .categories(Objects.nonNull(categories) ? createPageableCategoryDetail(categories) : null)
                .build();
    }

    private Page<CategoryDetail> createPageableCategoryDetail(Page<Category> categories) {
        return categories.map(category -> CategoryDetail.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .products(createProductsDetail(category.getProducts()))
                .build());
    }

    private Set<ProductDetail> createProductsDetail(Set<Product> products) {
        Set<ProductDetail> productDetails = new HashSet<>();
        products.forEach(product -> productDetails.add(ProductDetail.builder()
                .id(product.getId())
                .name(product.getName())
                .build()));

        return productDetails;
    }

    private CategoryResponse createCategoryResponse(Boolean isSuccess, String message, Category category) {
        return CategoryResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .category(Objects.nonNull(category) ? createCategoryDetail(category) : null)
                .build();
    }

    private CategoryDetail createCategoryDetail(Category category) {
        return CategoryDetail.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .products(createProductsDetail(category.getProducts()))
                .build();
    }

    private SetCategoryResponse createSetCategoryResponse(Boolean isSuccess, String message, List<Category> categories) {
        return SetCategoryResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .categories(!CollectionUtils.isEmpty(categories) ? createSetCategoryDetail(categories) : null)
                .build();
    }

    private Set<CategoryDetail> createSetCategoryDetail(List<Category> categories) {
        return categories.stream().filter(category -> !category.getProducts().isEmpty()
                && !category.getProducts().stream().filter(Product::getIsActive).collect(Collectors.toSet()).isEmpty()).map(category -> CategoryDetail.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .products(createProductDetailsForActiveProducts(category.getProducts()))
                .build()
        ).collect(Collectors.toSet());
    }

    private Set<ProductDetail> createProductDetailsForActiveProducts(Set<Product> products) {
        return products.stream().filter(Product::getIsActive).map(product -> ProductDetail.builder()
                .id(product.getId())
                .name(product.getName())
                .build()).collect(Collectors.toSet());
    }

    private CategoryWithPageableProductResponse createCategoryWithPageableProductResponse(Boolean isSuccess, String message,
                                                                                          Category category, Page<Product> products) {
        return CategoryWithPageableProductResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .category(Objects.nonNull(category) ? createCategoryDetailWithPageableProduct(category, products) : null)
                .build();
    }

    private CategoryDetailWithPageableProduct createCategoryDetailWithPageableProduct(Category category, Page<Product> products) {
        return CategoryDetailWithPageableProduct.builder()
                .id(category.getId())
                .name(category.getName())
                .products(createProductDetailsForActiveProducts(products))
                .build();
    }

    private Page<ProductDetail> createProductDetailsForActiveProducts(Page<Product> products) {
        return products.map(product -> ProductDetail.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoriesId(product.getCategories().stream().filter(Category::getIsActive).map(Category::getId).collect(Collectors.toSet()))
                .similarProductsId(products.stream().filter(Product::getIsActive).map(Product::getId).collect(Collectors.toSet()))
                .isActive(product.getIsActive())
                .cdnLinks(product.getCdnLinks())
                .build());
    }

}
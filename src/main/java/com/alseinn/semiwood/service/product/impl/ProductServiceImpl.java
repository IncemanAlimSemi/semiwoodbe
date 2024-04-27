package com.alseinn.semiwood.service.product.impl;

import com.alseinn.semiwood.dao.category.CategoryRepository;
import com.alseinn.semiwood.dao.product.ProductRepository;
import com.alseinn.semiwood.entity.category.Category;
import com.alseinn.semiwood.entity.product.Product;
import com.alseinn.semiwood.entity.user.User;
import com.alseinn.semiwood.request.product.CreateAndUpdateProductRequest;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.response.product.*;
import com.alseinn.semiwood.service.product.ProductService;
import com.alseinn.semiwood.service.storage.ImageService;
import com.alseinn.semiwood.utils.ResponseUtils;
import com.alseinn.semiwood.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;
    private final ResponseUtils responseUtils;
    private final UserUtils userUtils;

    private static final Logger LOG = Logger.getLogger(ProductServiceImpl.class.getName());

    @Override
    public GeneralInformationResponse createProduct(CreateAndUpdateProductRequest request) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Created Product Name: " + request.getName());
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            Optional<Product> optionalProduct = productRepository.findByName(request.getName());

            if (optionalProduct.isPresent()) {
                LOG.info(responseUtils.getMessage("product.name.exists") + "- Created Product Name: " + request.getName());
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("product.name.exists"));
            }

            Set<Category> categories = getCategories(request.getCategoryId());

            if (categories.isEmpty()) {
                LOG.info(responseUtils.getMessage("category.not.exists.message") + "- Created Product Name: " + request.getName());
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("category.not.exists.message"));
            }

            Set<Product> similarProducts = getSimilarProducts(request.getSimilarProducts());

            if (similarProducts.size() != 12 && false) {
                LOG.info(responseUtils.getMessage("product.not.found") + "- Created Product Name: " + request.getName());
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("product.not.found"));
            }

            Product product = createProductModel(request, categories, similarProducts);
            categories.forEach(category -> category.getProducts().add(product));

            productRepository.save(product);

            LOG.info(responseUtils.getMessage("product.create.success") + "- Created Product Name: " + request.getName());
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("product.create.success"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("product.create.error") + "- Created Product Name: " + request.getName() + "- Exception: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("product.create.error"));
        }
    }

    @Override
    public GeneralInformationResponse updateProducts(CreateAndUpdateProductRequest request) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Updated Product Id: " + request.getId());
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            Optional<Product> optionalProduct = productRepository.findById(request.getId());

            if (optionalProduct.isEmpty()) {
                LOG.info(responseUtils.getMessage("product.not.found") + "- Updated Product Id: " + request.getId());
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("product.not.found"));
            }

            Set<Category> categories = getCategories(request.getCategoryId());

            if (categories.isEmpty()) {
                LOG.info(responseUtils.getMessage("category.not.exists.message") + "- Updated Product Id: " + request.getId());
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("category.not.exists.message"));
            }

            Set<Product> similarProducts = getSimilarProducts(request.getSimilarProducts());

            if (similarProducts.size() != 12) {
                LOG.info(responseUtils.getMessage("product.not.found") + "- Updated Product Id: " + request.getId());
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("product.not.found"));
            }

            Product product = optionalProduct.get();
            setProductFields(product, request, categories, similarProducts);
            categories.forEach(category -> category.getProducts().remove(product));

            productRepository.save(product);

            LOG.info(responseUtils.getMessage("product.update.success") + "- Updated Product Id: " + request.getId());
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("product.update.success"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("product.create.error") + "- Updated Product Id: " + request.getId() + "- Exception: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("product.create.error"));
        }
    }

    @Override
    public PageableProductResponse getAllProducts(Pageable pageable) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Request: " + pageable);
                return createPageableProductResponse(false, responseUtils.getMessage("invalid.request"), null);
            }

            Page<Product> products = productRepository.findAll(pageable);

            return getPageableProductResponse(pageable, products);
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("products.fetch.error") + "- Request: " + pageable + "- Exception: " + e.getMessage());
            return createPageableProductResponse(false, responseUtils.getMessage("products.fetch.error"), null);
        }
    }

    @Override
    public PageableProductResponse getAllProductsByIsActiveTrue(Pageable pageable) {
        // Not using
        try {
            Page<Product> products = productRepository.findAllByIsActive(true, pageable);

            return getPageableProductResponse(pageable, products);
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("products.fetch.error") + "- Request: " + pageable + "- Exception: " + e.getMessage());
            return createPageableProductResponse(false, responseUtils.getMessage("products.fetch.error"), null);
        }
    }

    @Override
    public PageableProductResponse getAllProductsByNameContainingIgnoreCaseOrIdAndIsActive(String query, Pageable pageable) {
        try {
            Long id = checkQuery(query);

            Page<Product> products = productRepository.findByNameContainingIgnoreCaseOrIdAndIsActive(query, id, pageable);

            if (products.isEmpty()) {
                LOG.info(responseUtils.getMessage("product.not.found.name") + "- Request: " + pageable);
                return createPageableProductResponse(false, responseUtils.getMessage("product.not.found.name"), null);
            }

            LOG.info(responseUtils.getMessage("products.fetch.success") + "- Request: " + pageable);
            return createPageableProductResponse(true, responseUtils.getMessage("products.fetch.success"), products);
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("products.fetch.error") + "- Request: " + pageable + "- Exception: " + e.getMessage());
            return createPageableProductResponse(false, responseUtils.getMessage("products.fetch.error"), null);
        }
    }

    @Override
    public ListProductResponse getRandomProducts() {
        try {
            List<Product> products = productRepository.findRandomProducts();

            if (products.isEmpty()) {
                LOG.info(responseUtils.getMessage("product.not.found"));
                return createListProductResponse(false, responseUtils.getMessage("product.not.found"), null);
            }

            LOG.info(responseUtils.getMessage("products.fetch.success"));
            return createListProductResponse(true, responseUtils.getMessage("products.fetch.success"), products);
        } catch (Exception e) {
            LOG.info(responseUtils.getMessage("products.fetch.error"));
            return createListProductResponse(false, responseUtils.getMessage("products.fetch.error"), null);
        }
    }

    @Override
    public ListProductResponse getTop12SimilarProductByProductId(Long id) {
        try {
            Optional<Product> products = productRepository.findById(id);

            if (products.isEmpty()) {
                LOG.info(responseUtils.getMessage("product.not.found") + "- Id: " + id);
                return createListProductResponse(false, responseUtils.getMessage("product.not.found"), null);
            }

            LOG.info(responseUtils.getMessage("products.fetch.success") + "- Id: " + id);
            return createListProductResponse(true, responseUtils.getMessage("products.fetch.success"), new ArrayList<>(products.get().getSimilarProducts()).subList(0, 12));
        } catch (Exception e) {
            LOG.info(responseUtils.getMessage("products.fetch.error") + "- Id: " + id);
            return createListProductResponse(false, responseUtils.getMessage("products.fetch.error"), null);
        }
    }

    @Override
    public ProductResponse getProductById(Long id) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Id: " + id);
                return createProductDetailResponse(false, responseUtils.getMessage("invalid.request"), null,
                        null, null);
            }

            Optional<Product> product = productRepository.findById(id);

            if (product.isEmpty()) {
                LOG.info(responseUtils.getMessage("product.not.found") + "- Id: " + id);
                return createProductDetailResponse(false, responseUtils.getMessage("product.not.found"), null,
                        null, null);
            }

            LOG.info(responseUtils.getMessage("product.fetch.success") + "- Id: " + id);
            return createProductDetailResponse(true, responseUtils.getMessage("product.fetch.success"), product.get(),
                    x -> true, x -> true);
        } catch (Exception e) {
            LOG.info(responseUtils.getMessage("product.fetch.error") + "- Id: " + id);
            return createProductDetailResponse(false, responseUtils.getMessage("product.fetch.error"), null,
                    null, null);
        }
    }

    @Override
    public ProductResponse getProductByIdAndIsActiveTrue(Long id) {
        try {
            Optional<Product> optionalProduct = productRepository.findByIdAndIsActive(id, true);

            if (optionalProduct.isEmpty()) {
                LOG.info(responseUtils.getMessage("product.not.found") + "- Id: " + id);
                return createProductDetailResponse(false, responseUtils.getMessage("product.not.found"), null,
                        null, null);
            }

            LOG.info(responseUtils.getMessage("products.fetch.success") + "- Id: " + id);
            return createProductDetailResponse(true, responseUtils.getMessage("products.fetch.success"), optionalProduct.get(),
                    Category::getIsActive, Product::getIsActive);
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("products.fetch.error") + "- Id: " + id + "- Exception: " + e.getMessage());
            return createProductDetailResponse(false, responseUtils.getMessage("products.fetch.error"), null,
                    null, null);
        }
    }

    @Override
    public GeneralInformationResponse deleteProductById(Long id) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Id: " + id);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            Optional<Product> optionalProduct = productRepository.findById(id);

            if (optionalProduct.isEmpty()) {
                LOG.info(responseUtils.getMessage("product.not.found") + "- Id: " + id);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("product.not.found"));
            }

            Product product = optionalProduct.get();
            product.getCategories().forEach(category -> category.getProducts().remove(product));

            productRepository.deleteById(product.getId());

            LOG.info(responseUtils.getMessage("product.delete.success") + "- Id: " + id);
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("product.delete.success"));
        } catch (Exception e) {
            LOG.info(responseUtils.getMessage("product.delete.error") + "- Id: " + id);
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("product.delete.error"));
        }
    }

    private Set<Category> getCategories(Set<Long> ids) {
        List<Category> categories = categoryRepository.findAllById(ids);
        return categories.isEmpty() ? new HashSet<>() : new HashSet<>(categories);
    }

    private Set<Product> getSimilarProducts(Set<Long> ids) {
        List<Product> similarProducts = productRepository.findAllById(ids);
        return similarProducts.isEmpty() ? new HashSet<>() : new HashSet<>(similarProducts);
    }

    private Product createProductModel(CreateAndUpdateProductRequest request, Set<Category> categories, Set<Product> similarProducts) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .isActive(request.getIsActive())
                .categories(categories)
                .similarProducts(similarProducts)
                .cdnLinks(imageService.changeUrl(request.getCdnLinks()))
                .timeCreated(new Date())
                .timeModified(new Date())
                .build();
    }

    private void setProductFields(Product product, CreateAndUpdateProductRequest request, Set<Category> categories, Set<Product> similarProducts) {
        product.setDescription(request.getDescription());
        if (!product.getCdnLinks().isEmpty() && !request.getCdnLinks().isEmpty() && !product.getCdnLinks().get(0).equalsIgnoreCase(request.getCdnLinks().get(0))) {
            product.setCdnLinks(imageService.changeUrl(request.getCdnLinks()));
        }
        product.setIsActive(request.getIsActive());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategories(categories);
        product.setSimilarProducts(similarProducts);
    }

    private PageableProductResponse createPageableProductResponse(Boolean isSuccess, String message, Page<Product> products) {
        return PageableProductResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .products(Objects.nonNull(products) ? createExtendedProductDetailPageable(products) : null)
                .build();
    }

    private Page<ProductDetail> createExtendedProductDetailPageable(Page<Product> products) {
        return products.map(product -> ProductDetail.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .stock(product.getStock())
                .price(product.getPrice())
                .cdnLinks(product.getCdnLinks())
                .isActive(product.getIsActive())
                .build()
        );
    }

    private PageableProductResponse getPageableProductResponse(Pageable pageable, Page<Product> products) {
        if (products.isEmpty()) {
            LOG.info(responseUtils.getMessage("product.not.found") + "- Request: " + pageable);
            return createPageableProductResponse(false, responseUtils.getMessage("product.not.found"), null);
        }

        LOG.info(responseUtils.getMessage("products.fetch.success") + "- Request: " + pageable);
        return createPageableProductResponse(true, responseUtils.getMessage("products.fetch.success"), products);
    }

    private Long checkQuery(String query) {
        Long id;
        try {
            id = Long.parseLong(query);
        } catch (NumberFormatException e) {
            id = null;
        }

        return id;
    }

    private ProductResponse createProductDetailResponse(Boolean isSuccess, String message, Product product, Predicate<? super Category> predicateCategory,
                                                        Predicate<? super Product> predicateProduct) {
        return ProductResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .product(Objects.nonNull(product) ? createExtendedProductDetail(product, predicateCategory, predicateProduct) : null)
                .build();
    }

    private ProductDetail createExtendedProductDetail(Product product, Predicate<? super Category> predicateCategory,
                                                      Predicate<? super Product> predicateProduct) {
        return ProductDetail.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .stock(product.getStock())
                .price(product.getPrice())
                .cdnLinks(product.getCdnLinks())
                .categoriesId(product.getCategories().stream().filter(predicateCategory).map(Category::getId).collect(Collectors.toSet()))
                .similarProductsId(product.getSimilarProducts().stream().filter(predicateProduct).map(Product::getId).collect(Collectors.toSet()))
                .isActive(product.getIsActive())
                .build();
    }

    private ListProductResponse createListProductResponse(Boolean isSuccess, String message, List<Product> products) {
        return ListProductResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .products(!CollectionUtils.isEmpty(products) ? createExtendedProductDetailList(products) : null)
                .build();
    }

    private List<ProductDetail> createExtendedProductDetailList(List<Product> products) {
        return products.stream().map(product -> ProductDetail.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .cdnLinks(product.getCdnLinks())
                .stock(product.getStock())
                .build()
        ).collect(Collectors.toList());
    }

}

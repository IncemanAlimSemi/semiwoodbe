package com.alseinn.semiwood.service.product;

import com.alseinn.semiwood.request.product.CreateAndUpdateProductRequest;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.response.product.*;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    GeneralInformationResponse createProduct(CreateAndUpdateProductRequest request);
    GeneralInformationResponse updateProducts(CreateAndUpdateProductRequest request);
    PageableProductResponse getAllProducts(Pageable pageable);
    PageableProductResponse getAllProductsByIsActiveTrue(Pageable pageable);
    PageableProductResponse getAllProductsByNameContainingIgnoreCaseOrIdAndIsActive(String query, Pageable pageable);
    ListProductResponse getRandomProducts();
    ListProductResponse getTop12SimilarProductByProductId(Long id);
    ProductResponse getProductById(Long id);
    ProductResponse getProductByIdAndIsActiveTrue(Long id);
    GeneralInformationResponse deleteProductById(Long id);
}

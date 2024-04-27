package com.alseinn.semiwood.controller.product;

import com.alseinn.semiwood.request.product.CreateAndUpdateProductRequest;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.response.product.*;
import com.alseinn.semiwood.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${default.api.path}/product")
@RequiredArgsConstructor
@CrossOrigin
public class ProductController {

    private final ProductService productService;

    @PostMapping()
    public GeneralInformationResponse createProduct(@RequestBody CreateAndUpdateProductRequest request) {
        return productService.createProduct(request);
    }

    @PutMapping()
    public GeneralInformationResponse updateProduct(@RequestBody CreateAndUpdateProductRequest request) {
        return productService.updateProducts(request);
    }

    @GetMapping()
    public PageableProductResponse getAllProducts(@RequestParam(name = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("name").ascending());
        return productService.getAllProducts(pageable);
    }

    @GetMapping("/active")
    public PageableProductResponse getAllProductsByIsActiveTrue(@RequestParam(name = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("name").ascending());
        return productService.getAllProductsByIsActiveTrue(pageable);
    }

    @GetMapping("/search")
    public PageableProductResponse getAllProductsByNameContainingIgnoreCaseOrIdAndIsActive(@RequestParam(name = "query") String query, @RequestParam(name = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("name").ascending());
        return productService.getAllProductsByNameContainingIgnoreCaseOrIdAndIsActive(query, pageable);
    }

    @GetMapping("/random")
    public ListProductResponse getRandomProducts() {
        return productService.getRandomProducts();
    }

    @GetMapping("/{id}/similar")
    public ListProductResponse getTop12SimilarProductByProductId(@PathVariable(name = "id") Long id) {
        return productService.getTop12SimilarProductByProductId(id);
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id){
        return productService.getProductById(id);
    }

    @GetMapping("/active/{id}")
    public ProductResponse getProductByIdAndIsActiveTrue(@PathVariable(name = "id") Long id) {
        return productService.getProductByIdAndIsActiveTrue(id);
    }

    @DeleteMapping("/{id}")
    public GeneralInformationResponse deleteProductById(@PathVariable Long id) {
        return productService.deleteProductById(id);
    }

}

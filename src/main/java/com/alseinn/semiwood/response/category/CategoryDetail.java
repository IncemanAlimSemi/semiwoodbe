package com.alseinn.semiwood.response.category;

import com.alseinn.semiwood.response.product.ProductDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDetail {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private Set<ProductDetail> products;
}

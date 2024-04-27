package com.alseinn.semiwood.response.category;

import com.alseinn.semiwood.response.product.ProductDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDetailWithPageableProduct {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private Page<ProductDetail> products;
}

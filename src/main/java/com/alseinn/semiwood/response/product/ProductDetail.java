package com.alseinn.semiwood.response.product;

import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetail {
    private Long id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private Set<Long> categoriesId;
    private Set<Long> similarProductsId;
    private boolean isActive;
    private List<String> cdnLinks;
}

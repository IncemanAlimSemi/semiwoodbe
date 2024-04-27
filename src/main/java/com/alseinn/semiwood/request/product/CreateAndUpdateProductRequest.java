package com.alseinn.semiwood.request.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAndUpdateProductRequest {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private Boolean isActive;
    private Set<Long> categoryId;
    private Set<Long> similarProducts;
    private List<String> cdnLinks;
}
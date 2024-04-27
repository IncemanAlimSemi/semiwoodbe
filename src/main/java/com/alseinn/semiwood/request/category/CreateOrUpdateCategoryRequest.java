package com.alseinn.semiwood.request.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrUpdateCategoryRequest {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private Set<Long> productsId;
}

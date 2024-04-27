package com.alseinn.semiwood.response.category;

import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SetCategoryResponse extends GeneralInformationResponse {
    private Set<CategoryDetail> categories;
}
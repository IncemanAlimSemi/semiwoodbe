package com.alseinn.semiwood.response.product;

import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PageableProductResponse extends GeneralInformationResponse {
    private Page<ProductDetail> products;
}

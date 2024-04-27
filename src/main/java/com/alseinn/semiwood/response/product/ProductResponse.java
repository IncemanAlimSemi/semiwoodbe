package com.alseinn.semiwood.response.product;

import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse extends GeneralInformationResponse {
    public ProductDetail product;
}

package com.alseinn.semiwood.response.order;

import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class OrderInformationResponse extends GeneralInformationResponse {
    private Long id;
}
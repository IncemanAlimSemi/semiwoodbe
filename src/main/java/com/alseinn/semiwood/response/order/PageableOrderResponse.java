package com.alseinn.semiwood.response.order;

import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PageableOrderResponse extends GeneralInformationResponse {
        private Page<OrderDetail> orders;
}

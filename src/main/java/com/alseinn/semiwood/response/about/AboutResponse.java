package com.alseinn.semiwood.response.about;

import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AboutResponse extends GeneralInformationResponse {
    private AboutDetail about;
}

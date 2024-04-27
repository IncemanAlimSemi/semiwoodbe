package com.alseinn.semiwood.response.company;

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
public class SessionUserCompanyResponse extends GeneralInformationResponse {
    private CompanyDetail company;
}

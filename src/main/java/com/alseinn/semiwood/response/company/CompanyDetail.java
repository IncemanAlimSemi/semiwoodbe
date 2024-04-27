package com.alseinn.semiwood.response.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDetail {
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private String taxNumber;
    private Boolean isActive;
}
package com.alseinn.semiwood.request.company;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCompanyRequest {
    private String name;
    private String address;
    private String email;
    private String phoneNumber;
    private String taxNumber;
}
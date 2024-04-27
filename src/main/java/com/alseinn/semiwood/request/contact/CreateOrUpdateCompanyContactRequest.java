package com.alseinn.semiwood.request.contact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrUpdateCompanyContactRequest {
    private String name;
    private String email;
    private String address;
    private String phoneNumber;
    private String instagram;
    private String linkedin;
    private String twitter;
    private String facebook;
}

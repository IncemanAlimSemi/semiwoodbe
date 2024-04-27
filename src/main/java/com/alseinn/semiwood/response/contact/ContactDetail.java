package com.alseinn.semiwood.response.contact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactDetail {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String message;
    private Boolean isOkay;
}

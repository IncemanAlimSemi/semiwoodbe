package com.alseinn.semiwood.request.contact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateContactRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private String message;
}

package com.alseinn.semiwood.request.contact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOkFieldContactRequest {
    private Long id;
    private Boolean isOk;
}
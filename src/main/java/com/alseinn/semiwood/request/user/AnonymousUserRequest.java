package com.alseinn.semiwood.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnonymousUserRequest {
    private Long id;
    private String email;
}

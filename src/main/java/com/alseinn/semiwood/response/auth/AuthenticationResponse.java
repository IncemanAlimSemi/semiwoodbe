package com.alseinn.semiwood.response.auth;

import com.alseinn.semiwood.entity.user.enums.Role;
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
public class AuthenticationResponse extends GeneralInformationResponse {
    private String token;
    private String email;
    private Role role;
}

package com.alseinn.semiwood.response.user;

import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse extends GeneralInformationResponse {
    private UserDetail user;
}

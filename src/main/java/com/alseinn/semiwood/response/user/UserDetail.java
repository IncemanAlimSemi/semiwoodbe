package com.alseinn.semiwood.response.user;

import com.alseinn.semiwood.entity.user.enums.Gender;
import com.alseinn.semiwood.entity.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetail {
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String mobileNumber;
    private Gender gender;
    private Role role;
}

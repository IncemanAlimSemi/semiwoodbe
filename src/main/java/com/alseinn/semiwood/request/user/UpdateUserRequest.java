package com.alseinn.semiwood.request.user;


import com.alseinn.semiwood.entity.user.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    private String firstname;
    private String lastname;
    private String username;
    private String mobileNumber;
    private String email;
    private Gender gender;
}

package com.alseinn.semiwood.service.user;

import com.alseinn.semiwood.request.user.UpdateUserRequest;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.response.user.UserResponse;

public interface UserService {
    GeneralInformationResponse updateUser(UpdateUserRequest updateUserRequest);
    UserResponse getSessionUser();
    GeneralInformationResponse hasSessionUser();
}

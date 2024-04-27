package com.alseinn.semiwood.controller.user;

import com.alseinn.semiwood.request.user.UpdateUserRequest;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.response.user.UserResponse;
import com.alseinn.semiwood.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${default.api.path}/user")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserService userService;

    @PutMapping()
    public GeneralInformationResponse updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
        return userService.updateUser(updateUserRequest);
    }
    @GetMapping("/session")
    public UserResponse getSessionUser() {
        return userService.getSessionUser();
    }

    @GetMapping("/check-session-user")
    public GeneralInformationResponse hasSessionUser() {
        return userService.hasSessionUser();
    }

}

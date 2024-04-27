package com.alseinn.semiwood.utils;

import com.alseinn.semiwood.dao.user.UserRepository;
import com.alseinn.semiwood.entity.user.User;
import com.alseinn.semiwood.entity.user.enums.Role;
import com.alseinn.semiwood.request.user.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserUtils {

    private final UserRepository userRepository;
    private final ResponseUtils responseUtils;

    public User getUserFromSecurityContext() {
        try {
            return  userRepository.findByUsername(((User) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal())
                    .getUsername()).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isSessionUser(User user) {
        String sessionUsername = getUserFromSecurityContext().getUsername();
        return Objects.equals(user.getUsername(), sessionUsername);
    }

    public User getUserIfUserRoleAdmin() {
        User user = getUserFromSecurityContext();

        if (Objects.isNull(user)) {
            return null;
        }

        if (!user.getRole().equals(Role.ADMIN)) {
            return null;
        }

        return user;
    }

    public String createRegisterMessage(UpdateUserRequest request, List<User> dbUsers, User sessionUser) {
        List<String> listOfMessage = new ArrayList<>();
        for (User user: dbUsers) {
            if (Objects.nonNull(sessionUser) && user.equals(sessionUser)) {
                continue;
            }

            if (user.getUsername().equalsIgnoreCase(request.getUsername())) {
                listOfMessage.add(responseUtils.getMessage("username.duplicate.message"));
            }

            if (user.getEmail().equalsIgnoreCase(request.getEmail())) {
                listOfMessage.add((responseUtils.getMessage("email.duplicate.message")));

            }

            if (user.getMobileNumber().equalsIgnoreCase(request.getMobileNumber())) {
                listOfMessage.add((responseUtils.getMessage("phone.duplicate.message")));
            }
        }

        return String.join("/", listOfMessage);
    }
}

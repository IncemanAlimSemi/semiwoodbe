package com.alseinn.semiwood.service.auth;

import com.alseinn.semiwood.dao.user.UserRepository;
import com.alseinn.semiwood.entity.user.User;
import com.alseinn.semiwood.entity.user.enums.Role;
import com.alseinn.semiwood.request.auth.AuthenticationRequest;
import com.alseinn.semiwood.request.auth.RegisterRequest;
import com.alseinn.semiwood.response.auth.AuthenticationResponse;
import com.alseinn.semiwood.service.email.EmailService;
import com.alseinn.semiwood.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ResponseUtils responseUtils;
    private final EmailService emailService;

    private static final Logger LOG = Logger.getLogger(AuthenticationService.class.getName());
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";

    public AuthenticationResponse register(RegisterRequest request) {
        try {
            List<User> dbUsers = userRepository.findByUsernameOrEmailOrMobileNumber(request.getUsername(), request.getEmail(), request.getMobileNumber());

            if (!dbUsers.isEmpty()) {
                LOG.info(responseUtils.getMessage("user.exists") + "- Request: " + request);
                return createAuthenticationResponse(false, createRegisterMessage(request, dbUsers), null, null, null);
            }

            User user = userRepository.save(createUser(request));
            var jwtToken = jwtService.generateToken(user);

            emailService.sendRegisterEmail(request.getEmail(), String.join(" ", request.getFirstname().toUpperCase(),
                    request.getLastname().toUpperCase()));

            LOG.info(responseUtils.getMessage("registration.success.message") + "- Request: " + request);
            return createAuthenticationResponse(true, responseUtils.getMessage("registration.success.message"),
                    jwtToken, user.getRole(), user.getEmail());
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("registration.error") + "- Request: " + request + "- Exception: " + e.getMessage());
            return createAuthenticationResponse(false, responseUtils.getMessage("registration.error"), null, null, null);
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            User user = getUserFromUsernameOrEmail(request.getCredential());

            if (Objects.isNull(user) || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                LOG.info(responseUtils.getMessage("invalid.username.or.password") + "- Credential: " + request.getCredential());
                return createAuthenticationResponse(false, responseUtils.getMessage("invalid.username.or.password"),
                        null, null, null);
            }

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), request.getPassword());
            authenticationManager.authenticate(auth);
            var jwtToken = jwtService.generateToken(user);

            LOG.info(responseUtils.getMessage("successfully.logged.in") + "- Credential: " + request.getCredential());
            return createAuthenticationResponse(true, responseUtils.getMessage("successfully.logged.in"),
                    jwtToken, user.getRole(), user.getEmail());
        } catch (Exception e) {
            LOG.info(responseUtils.getMessage("login.error") + "- Credential: " + request.getCredential() + "- Exception: " + e.getMessage());
            return createAuthenticationResponse(false, responseUtils.getMessage("login.error"),
                    null, null, null);
        }
    }

    private String createRegisterMessage(RegisterRequest request, List<User> dbUsers) {
        List<String> listOfMessage = new ArrayList<>();
        for (User user: dbUsers) {
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

    private AuthenticationResponse createAuthenticationResponse(Boolean isSuccess, String message, String token, Role role, String email) {
        return AuthenticationResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .token(token)
                .role(role)
                .email(email)
                .build();
    }

    private User createUser(RegisterRequest request) {
        return User.builder()
                .username(request.getUsername())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .gender(request.getGender())
                .email(request.getEmail())
                .mobileNumber(request.getMobileNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .isActive(true)
                .timeCreated(new Date(System.currentTimeMillis()))
                .timeModified(new Date(System.currentTimeMillis()))
                .build();
    }

    private User getUserFromUsernameOrEmail(String credential) {
        if (credential.matches(EMAIL_REGEX)) {
            return userRepository.findByEmail(credential).orElse(null);
        } else {
            return userRepository.findByUsername(credential).orElse(null);
        }
    }

}

package com.alseinn.semiwood.service.user.impl;

import com.alseinn.semiwood.dao.user.UserRepository;
import com.alseinn.semiwood.entity.user.User;
import com.alseinn.semiwood.request.user.UpdateUserRequest;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.response.user.UserResponse;
import com.alseinn.semiwood.response.user.UserDetail;
import com.alseinn.semiwood.service.user.UserService;
import com.alseinn.semiwood.utils.ResponseUtils;
import com.alseinn.semiwood.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ResponseUtils responseUtils;
    private final UserUtils userUtils;

    private static final Logger LOG = Logger.getLogger(UserServiceImpl.class.getName());

    @Override
    public GeneralInformationResponse updateUser(UpdateUserRequest request) {
        try {
            User user = userUtils.getUserFromSecurityContext();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("user.not.found") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("user.not.found"));
            }

            List<User> users = userRepository.findAllByIdNotAndMobileNumberContainingIgnoreCase(
                    user.getId(), request.getMobileNumber());

            if (!users.isEmpty()) {
                LOG.info(responseUtils.getMessage("phone.duplicate.message") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("phone.duplicate.message"));
            }

            setUserFields(user, request);
            userRepository.save(user);

            LOG.info(responseUtils.getMessage("user.saved.successfully") + "- Request: " + request);
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("user.saved.successfully"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("user.save.error") + "- Request: " + request + " Exception: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("user.save.error"));
        }
    }

    @Override
    public UserResponse getSessionUser() {
        try {
            User user = userUtils.getUserFromSecurityContext();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("user.not.found"));
                return createSessionUserResponse(false, responseUtils.getMessage("user.not.found"), null);
            }

            LOG.info(responseUtils.getMessage("user.fetch.success") + "User: " + user);
            return createSessionUserResponse(true, responseUtils.getMessage("user.fetch.success"), user);
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("user.fetch.error"));
            return createSessionUserResponse(false, responseUtils.getMessage("user.fetch.error"), null);
        }
    }

    @Override
    public GeneralInformationResponse hasSessionUser() {
        try {
            User user = userUtils.getUserFromSecurityContext();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("user.not.found"));
                return responseUtils.createGeneralInformationResponse(
                        false, responseUtils.getMessage("user.not.found"));
            }

            LOG.info(responseUtils.getMessage("session.has.user"));
            return responseUtils.createGeneralInformationResponse(
                    true, responseUtils.getMessage("session.has.user"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("session.check.error"));
            return responseUtils.createGeneralInformationResponse(
                    false, responseUtils.getMessage("session.check.error"));
        }
    }

    private void setUserFields(User user, UpdateUserRequest request) {
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setMobileNumber(request.getMobileNumber());
        user.setGender(request.getGender());
    }

    private UserResponse createSessionUserResponse(boolean isSuccess, String message, User user) {
        return UserResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .user(createUserDetail(user))
                .build();
    }

    private UserDetail createUserDetail(User user) {
        return Objects.isNull(user) ? null :
        UserDetail.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getUsername())
                .mobileNumber(user.getMobileNumber())
                .email(user.getEmail())
                .gender(user.getGender())
                .role(user.getRole())
                .build();
    }
}

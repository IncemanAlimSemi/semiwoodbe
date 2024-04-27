package com.alseinn.semiwood.service.about.impl;

import com.alseinn.semiwood.dao.about.AboutRepository;
import com.alseinn.semiwood.entity.about.About;
import com.alseinn.semiwood.entity.user.User;
import com.alseinn.semiwood.request.about.CreateOrUpdateAboutRequest;
import com.alseinn.semiwood.response.about.AboutDetail;
import com.alseinn.semiwood.response.about.AboutResponse;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.service.about.AboutService;
import com.alseinn.semiwood.service.storage.ImageService;
import com.alseinn.semiwood.utils.ResponseUtils;
import com.alseinn.semiwood.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.alseinn.semiwood.utils.contants.AppTRConstants.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AboutServiceImpl implements AboutService {

    private final AboutRepository aboutRepository;
    private final UserUtils userUtils;
    private final ResponseUtils responseUtils;
    private final ImageService imageService;

    private static final Logger LOG = Logger.getLogger(AboutServiceImpl.class.getName());

    @Override
    public GeneralInformationResponse createAbout(CreateOrUpdateAboutRequest request) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            List<About> abouts = aboutRepository.findAll();

            if (!abouts.isEmpty()) {
                LOG.info(responseUtils.getMessage("about.already.exists") + "- Request: " + request + "- User: " + user.getEmail());
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("about.already.exists"));
            }

            aboutRepository.save(createAboutModel(request));

            LOG.info(responseUtils.getMessage("about.created.with.success") + "- Request: " + request
                    + "- User: " + user.getEmail());
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("about.created.with.success"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("about.save.error") + "- Request: " + request + "- Exception: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("about.save.error"));
        }
    }

    @Override
    public GeneralInformationResponse updateAbout(CreateOrUpdateAboutRequest request) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            List<About> aboutList = aboutRepository.findAll();

            if (CollectionUtils.isEmpty(aboutList)) {
                LOG.info(responseUtils.getMessage("about.can.not.be.empty") + "- Request: " + request + "- User: " + user.getEmail());
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("about.can.not.be.empty"));
            }

            About about = aboutList.get(0);
            about.setTitle(request.getTitle());
            about.setContent(request.getContent());
            about.setCdnLinks(imageService.changeUrl(request.getCdnLinks()));

            aboutRepository.save(about);

            LOG.info(responseUtils.getMessage("about.updated.with.success") + "- Request: " + request + "- User: " + user.getEmail());
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("about.updated.with.success", ABOUT));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("about.update.error") + "- Request: " + request + "- Exception: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("about.update.error"));
        }
    }

    @Override
    public AboutResponse getAbout() {
        try {
            List<About> abouts = aboutRepository.findAll();

            if (CollectionUtils.isEmpty(abouts)) {
                LOG.info(responseUtils.getMessage("about.can.not.be.empty"));
                return createAboutResponse(false, responseUtils.getMessage("about.can.not.be.empty"), null);
            }

            About about = abouts.get(0);

            LOG.info(responseUtils.getMessage("about.fetch.success"));
            return createAboutResponse(true, responseUtils.getMessage("about.fetch.success"), about);
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("about.fetch.error"));
            return createAboutResponse(false, responseUtils.getMessage("about.fetch.error"), null);
        }
    }

    private About createAboutModel(CreateOrUpdateAboutRequest request) {
        return About.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .cdnLinks(imageService.changeUrl(request.getCdnLinks()))
                .timeCreated(new Date())
                .timeModified(new Date())
                .build();
    }

    private AboutResponse createAboutResponse(Boolean isSuccess, String message, About about) {
        return AboutResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .about(Objects.nonNull(about) ? createAboutDetail(about) : null)
                .build();
    }

    private AboutDetail createAboutDetail(About about) {
        return AboutDetail.builder()
                .title(about.getTitle())
                .content(about.getContent())
                .cdnLinks(about.getCdnLinks())
                .build();
    }
}

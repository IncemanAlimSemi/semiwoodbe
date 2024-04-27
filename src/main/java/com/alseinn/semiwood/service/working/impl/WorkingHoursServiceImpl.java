package com.alseinn.semiwood.service.working.impl;

import com.alseinn.semiwood.dao.working.WorkingHoursRepository;
import com.alseinn.semiwood.entity.user.User;
import com.alseinn.semiwood.entity.working.WorkingHours;
import com.alseinn.semiwood.entity.working.enums.Day;
import com.alseinn.semiwood.request.working.WorkingHoursDetailRequest;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.response.working.WorkingHoursDetail;
import com.alseinn.semiwood.response.working.WorkingHoursResponse;
import com.alseinn.semiwood.service.working.WorkingHoursService;
import com.alseinn.semiwood.utils.ResponseUtils;
import com.alseinn.semiwood.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkingHoursServiceImpl implements WorkingHoursService {

    private final WorkingHoursRepository workingHoursRepository;
    private final ResponseUtils responseUtils;
    private final UserUtils userUtils;

    private static final Logger LOG = Logger.getLogger(WorkingHoursServiceImpl.class.getName());

    @Override
    public GeneralInformationResponse createWorkingHours(WorkingHoursDetailRequest request) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            Day day = Day.valueOf(request.getDay().toUpperCase());
            Optional<WorkingHours> optionalWorkingHours = workingHoursRepository.findByDay(day);
            boolean isEqual = Arrays.asList(Day.values()).contains(day);

            if (optionalWorkingHours.isPresent()) {
                LOG.info(responseUtils.getMessage("record.exists.for.working.day") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("record.exists.for.working.day"));
            }

            if (!isEqual) {
                LOG.info(responseUtils.getMessage("invalid.day") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.day"));
            }

            WorkingHours workingHours = WorkingHours.builder()
                    .day(day)
                    .startHours(request.getStartHours())
                    .endHours(request.getEndHours())
                    .timeCreated(new Date())
                    .timeModified(new Date())
                    .build();

            workingHoursRepository.save(workingHours);

            LOG.info(responseUtils.getMessage("working.hour.create.success") + "- Request: " + request);
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("working.hour.create.success"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("working.hour.create.error") + "- Request: " + request);
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("working.hour.create.error"));
        }
    }

    @Override
    public GeneralInformationResponse updateWorkingHours(WorkingHoursDetailRequest request) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            Day day = Day.valueOf(request.getDay().toUpperCase());
            Optional<WorkingHours> optionalWorkingHours = workingHoursRepository.findByDay(day);

            if (optionalWorkingHours.isEmpty()) {
                LOG.info(responseUtils.getMessage("working.hours.not.found") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("working.hours.not.found"));
            }

            WorkingHours workingHours = optionalWorkingHours.get();
            workingHours.setStartHours(request.getStartHours());
            workingHours.setEndHours(request.getEndHours());

            workingHoursRepository.save(workingHours);

            LOG.info(responseUtils.getMessage("working.hours.update.success") + "- Request: " + request);
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("working.hours.update.success"));
        } catch (Exception e) {
            LOG.info(responseUtils.getMessage("working.hours.update.error") + "- Request: " + request);
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("working.hours.update.error"));
        }
    }

    @Override
    public WorkingHoursResponse findAllWorkingHours() {
        try {
            List<WorkingHours> workingHours = workingHoursRepository.findAll();

            if (workingHours.isEmpty()) {
                LOG.info(responseUtils.getMessage("working.hours.not.found"));
                return createWorkingHoursResponse(responseUtils.getMessage("working.hours.not.found"));
            }

            LOG.info(responseUtils.getMessage("working.hours.fetch.success"));
            return createWorkingHoursResponse(workingHours);
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("working.hours.fetch.error") + "- Exception: " + e.getMessage());
            return createWorkingHoursResponse(responseUtils.getMessage("working.hours.fetch.error"));
        }
    }

    private WorkingHoursResponse createWorkingHoursResponse(String message) {
        return WorkingHoursResponse.builder()
                .isSuccess(false)
                .message(message)
                .build();
    }

    private WorkingHoursResponse createWorkingHoursResponse(List<WorkingHours> workingHours) {
        return WorkingHoursResponse.builder()
                .isSuccess(true)
                .message(responseUtils.getMessage("working.hours.fetch.success"))
                .workings(createWorkingDetailResponses(workingHours))
                .build();
    }

    private Set<WorkingHoursDetail> createWorkingDetailResponses(List<WorkingHours> workingHours) {
        return workingHours.stream().map(wH -> WorkingHoursDetail.builder()
                .day(wH.getDay().toString())
                .startHours(wH.getStartHours())
                .endHours(wH.getEndHours())
                .build()
        ).collect(Collectors.toSet());
    }
}

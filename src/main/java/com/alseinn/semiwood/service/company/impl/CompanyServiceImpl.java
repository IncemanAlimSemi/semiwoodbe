package com.alseinn.semiwood.service.company.impl;

import com.alseinn.semiwood.dao.company.CompanyRepository;
import com.alseinn.semiwood.entity.company.Company;
import com.alseinn.semiwood.entity.user.User;
import com.alseinn.semiwood.request.company.CreateCompanyRequest;
import com.alseinn.semiwood.request.company.UpdateCompanyRequest;
import com.alseinn.semiwood.response.company.CompanyDetail;
import com.alseinn.semiwood.response.company.PageableCompanyResponse;
import com.alseinn.semiwood.response.company.SessionUserCompanyResponse;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.service.company.CompanyService;
import com.alseinn.semiwood.utils.ResponseUtils;
import com.alseinn.semiwood.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final ResponseUtils responseUtils;
    private final UserUtils userUtils;

    private static final Logger LOG = Logger.getLogger(CompanyServiceImpl.class.getName());

    @Override
    public GeneralInformationResponse createCompany(CreateCompanyRequest request) {
        try {
            User user = userUtils.getUserFromSecurityContext();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            if (Objects.nonNull(user.getCompany())) {
                LOG.info(responseUtils.getMessage("user.company.exists") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("user.company.exists"));
            }

            List<Company> companyList = companyRepository.findAllByNameOrPhoneNumberOrEmailOrTaxNumber(
                    request.getName(), request.getPhoneNumber(),
                    request.getEmail(), request.getTaxNumber()
            );

            if (!CollectionUtils.isEmpty(companyList)) {
                String message = createMessage(companyList, request);
                LOG.info(responseUtils.getMessage("field.duplicate", message) + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("field.duplicate", message));
            }

            Company company = createCompanyModel(request, user);
            user.setCompany(company);
            companyRepository.save(company);

            LOG.info(responseUtils.getMessage("company.create.success") + "- Request: " + request);
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("company.create.success"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("company.create.error") + "- Request: " + request + "- Exception: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("company.create.error"));
        }
    }

    @Override
    public GeneralInformationResponse updateCompany(UpdateCompanyRequest request) {
        try {
            User user = userUtils.getUserFromSecurityContext();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            if (Objects.isNull(user.getCompany())) {
                LOG.info(responseUtils.getMessage("no.previous.company") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("no.previous.company"));
            }

            Company company = user.getCompany();

            if (checkUpdatedFields(company, request)) {
                LOG.info(responseUtils.getMessage("info.update.duplicate") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("info.update.duplicate"));
            }

            company.setAddress(request.getAddress());
            company.setPhoneNumber(request.getPhoneNumber());
            company.setEmail(request.getEmail());
            company.setTaxNumber(request.getTaxNumber());

            companyRepository.save(company);

            LOG.info(responseUtils.getMessage("company.update.success") + "- Request: " + request);
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("company.update.success"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("company.info.update.error") + "- Request: " + request + "- Exception: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("company.info.update.error"));
        }
    }

    @Override
    public GeneralInformationResponse activateCompany(Long id) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Id: " + id);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            Optional<Company> optionalCompany = companyRepository.findById(id);

            if (optionalCompany.isEmpty()) {
                LOG.info(responseUtils.getMessage("company.not.found") + "- Id: " + id);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("company.not.found"));
            }

            Company company = optionalCompany.get();
            company.setIsActive(!company.getIsActive());
            companyRepository.save(company);

            LOG.info(responseUtils.getMessage("company.activate.success") + "- Id: " + id);
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("company.activate.success"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("company.activate.error") + "- Id: " + id + "- Exception: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("company.activate.error"));
        }
    }

    @Override
    public PageableCompanyResponse getAllCompanies(Pageable pageable) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Request: " + pageable);
                return createPageableCompanyResponse(false, responseUtils.getMessage("invalid.request"), null);
            }

            Page<Company> companies = companyRepository.findAll(pageable);

            if (companies.isEmpty()) {
                LOG.info(responseUtils.getMessage("company.not.found") + "- Request: " + pageable);
                return createPageableCompanyResponse(false, responseUtils.getMessage("company.not.found"), null);
            }

            LOG.info(responseUtils.getMessage("companies.fetch.success") + "- Request: " + pageable);
            return createPageableCompanyResponse(true, responseUtils.getMessage("companies.fetch.success"), companies);
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("companies.fetch.error") + "- Request: " + pageable + "- Exception: " + e.getMessage());
            return createPageableCompanyResponse(false, responseUtils.getMessage("companies.fetch.error"), null);
        }
    }

    @Override
    public SessionUserCompanyResponse getSessionUserCompany() {
        try {
            User user = userUtils.getUserFromSecurityContext();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission"));
                return createSessionUserCompanyResponse(false, responseUtils.getMessage("invalid.request"), null);
            }

            Company company = user.getCompany();

            if (Objects.isNull(company)) {
                LOG.info(responseUtils.getMessage("user.no.company"));
                return createSessionUserCompanyResponse(false, responseUtils.getMessage("user.no.company"), null);
            }

            LOG.info(responseUtils.getMessage("your.company.info.fetch.success"));
            return createSessionUserCompanyResponse(true, responseUtils.getMessage("your.company.info.fetch.success"), company);
        } catch (Exception e) {
            LOG.info(responseUtils.getMessage("your.company.info.fetch.error") + "- Exception: " + e.getMessage());
            return createSessionUserCompanyResponse(false, responseUtils.getMessage("your.company.info.fetch.error"), null);
        }
    }

    @Override
    public GeneralInformationResponse deleteCompanyById(Long id) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Id: " + id);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            Optional<Company> optionalCompany = companyRepository.findById(id);

            if (optionalCompany.isEmpty()) {
                LOG.info(responseUtils.getMessage("company.not.found.by.name") + "- Id: " + id);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("company.not.found.by.name"));
            }

            Company company = optionalCompany.get();
            User companyUser = company.getUser();
            companyUser.setCompany(null);
            companyRepository.delete(company);

            LOG.info(responseUtils.getMessage("company.delete.success") + "- Id: " + id);
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("company.delete.success"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("company.delete.error") + "- Id: " + id + "- Exception: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("company.delete.error"));
        }
    }

    @Override
    public GeneralInformationResponse deleteSessionUserCompany() {
        try {
            User user = userUtils.getUserFromSecurityContext();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission"));
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            if (Objects.isNull(user.getCompany())) {
                LOG.info(responseUtils.getMessage("no.previous.company"));
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("no.previous.company"));
            }

            Company company = user.getCompany();
            user.setCompany(null);
            companyRepository.delete(company);

            LOG.info(responseUtils.getMessage("company.delete.success"));
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("company.delete.success"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("company.delete.error") + "- Exception: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("company.delete.error"));
        }
    }

    private String createMessage(List<Company> companyList, CreateCompanyRequest request) {
        List<String> messages = new ArrayList<>();
        for (Company company : companyList) {
            if (company.getName().equals(request.getName())) {
                messages.add(responseUtils.getMessage("company.name"));
            }
            if (company.getPhoneNumber().equals(request.getPhoneNumber())) {
                messages.add(responseUtils.getMessage("company.phoneNumber"));
            }
            if (company.getEmail().equals(request.getEmail())) {
                messages.add(responseUtils.getMessage("company.email"));
            }
            if (company.getTaxNumber().equals(request.getTaxNumber())) {
                messages.add(responseUtils.getMessage("company.taxNumber"));
            }
        }
        return String.join(", ", messages);
    }

    private Company createCompanyModel(CreateCompanyRequest request, User user) {
        return Company.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .taxNumber(request.getTaxNumber())
                .isActive(false)
                .user(user)
                .timeCreated(new Date())
                .timeModified(new Date())
                .build();
    }

    private boolean checkUpdatedFields(Company company, UpdateCompanyRequest request) {
        Boolean ifPhonePresent = false;
        Boolean ifEmailPresent = false;
        Boolean ifTaxNumberPresent = false;
        if (!company.getPhoneNumber().equals(request.getPhoneNumber())) {
            ifPhonePresent = getFalseIfNotDuplicated("phone", request.getPhoneNumber(), company);
        }

        if (!company.getEmail().equals(request.getEmail())) {
            ifEmailPresent = getFalseIfNotDuplicated("email", request.getEmail(), company);
        }

        if (!company.getTaxNumber().equals(request.getTaxNumber())) {
            ifTaxNumberPresent = getFalseIfNotDuplicated("tax", request.getTaxNumber(), company);
        }

        return ifPhonePresent || ifEmailPresent || ifTaxNumberPresent;
    }

    private Boolean getFalseIfNotDuplicated(String key, String value, Company company) {
        Map<String, Function<String, Optional<Company>>> map = new HashMap<>() {{
            put("phone", companyRepository::findByPhoneNumber);
            put("email", companyRepository::findByEmail);
            put("tax", companyRepository::findByTaxNumber);
        }};

        Optional<Company> optionalCompany = map.get(key).apply(value);

        return optionalCompany.filter(company1 -> !company1.equals(company)).isPresent();
    }

    private PageableCompanyResponse createPageableCompanyResponse(Boolean isSuccess, String message, Page<Company> companies) {
        return PageableCompanyResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .company(Objects.nonNull(companies) ? createCompanyDetail(companies) : null)
                .build();
    }

    private SessionUserCompanyResponse createSessionUserCompanyResponse(Boolean isSuccess, String message, Company company) {
        return SessionUserCompanyResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .company(Objects.nonNull(company) ? createCompanyDetail(company) : null)
                .build();
    }

    private Page<CompanyDetail> createCompanyDetail(Page<Company> companies) {
        return companies.map(company -> CompanyDetail.builder()
                .name(company.getName())
                .address(company.getAddress())
                .phoneNumber(company.getPhoneNumber())
                .email(company.getEmail())
                .taxNumber(company.getTaxNumber())
                .isActive(company.getIsActive())
                .build()
        );
    }

    private CompanyDetail createCompanyDetail(Company company) {
        return CompanyDetail.builder()
                .name(company.getName())
                .address(company.getAddress())
                .phoneNumber(company.getPhoneNumber())
                .email(company.getEmail())
                .taxNumber(company.getTaxNumber())
                .isActive(company.getIsActive())
                .build();
    }

}

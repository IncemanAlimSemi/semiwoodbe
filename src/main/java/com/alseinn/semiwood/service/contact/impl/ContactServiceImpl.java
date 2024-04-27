package com.alseinn.semiwood.service.contact.impl;

import com.alseinn.semiwood.dao.contact.CompanyContactRepository;
import com.alseinn.semiwood.dao.contact.ContactRepository;
import com.alseinn.semiwood.dao.working.WorkingHoursRepository;
import com.alseinn.semiwood.entity.contact.CompanyContact;
import com.alseinn.semiwood.entity.contact.Contact;
import com.alseinn.semiwood.entity.user.User;
import com.alseinn.semiwood.entity.working.WorkingHours;
import com.alseinn.semiwood.request.contact.CreateContactRequest;
import com.alseinn.semiwood.request.contact.CreateOrUpdateCompanyContactRequest;
import com.alseinn.semiwood.request.contact.UpdateOkFieldContactRequest;
import com.alseinn.semiwood.response.contact.*;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.response.working.WorkingHoursDetail;
import com.alseinn.semiwood.service.contact.ContactService;
import com.alseinn.semiwood.utils.ResponseUtils;
import com.alseinn.semiwood.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final CompanyContactRepository companyContactRepository;
    private final WorkingHoursRepository workingHoursRepository;
    private final ResponseUtils responseUtils;
    private final UserUtils userUtils;

    private static final Logger LOG = Logger.getLogger(ContactServiceImpl.class.getName());

    @Override
    public GeneralInformationResponse createCompanyContact(CreateOrUpdateCompanyContactRequest request) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            List<CompanyContact> companyContacts = companyContactRepository.findAll();

            if (!companyContacts.isEmpty()) {
                LOG.info(responseUtils.getMessage("company.contact.exists") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("company.contact.exists"));
            }

            companyContactRepository.save(createCompanyContactModel(request));

            LOG.info(responseUtils.getMessage("company.contact.create.success") + "- Request: " + request);
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("company.contact.create.success"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("company.contact.create.error") + "- Request: " + request + "- Exception: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("company.contact.create.error"));
        }
    }

    @Override
    public GeneralInformationResponse updateCompanyContact(CreateOrUpdateCompanyContactRequest request) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            List<CompanyContact> companyContacts = companyContactRepository.findAll();

            if (CollectionUtils.isEmpty(companyContacts)) {
                LOG.info(responseUtils.getMessage("company.contact.not.found") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("company.contact.not.found"));
            }

            CompanyContact companyContact = companyContacts.get(0);
            setCompanyContactFields(companyContacts.get(0), request);
            companyContactRepository.save(companyContact);

            LOG.info(responseUtils.getMessage("company.contact.update.success") + "- Request: " + request);
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("company.contact.update.success"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("company.contact.update.error") + "- Request: " + request + "- Exception: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("company.contact.update.error"));
        }
    }

    @Override
    public CompanyContactResponse getCompanyContact() {
        try {
            List<CompanyContact> companyContacts = companyContactRepository.findAll();

            if (companyContacts.isEmpty()) {
                LOG.info(responseUtils.getMessage("company.contact.not.found"));
                return createCompanyContactResponse(false, responseUtils.getMessage("company.contact.not.found"), null);
            }

            CompanyContact companyContact = companyContacts.get(0);

            LOG.info(responseUtils.getMessage("company.contact.fetch.success"));
            return createCompanyContactResponse(true, responseUtils.getMessage("company.contact.fetch.success"), companyContact);
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("company.contact.fetch.error") + "- Exception: " + e.getMessage());
            return createCompanyContactResponse(false, responseUtils.getMessage("company.contact.fetch.error"), null);
        }
    }

    @Override
    public GeneralInformationResponse createContact(CreateContactRequest request) {
        try {
            contactRepository.save(createContactModel(request));

            LOG.info(responseUtils.getMessage("request.submit.success") + "- Request: " + request);
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("request.submit.success"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("request.submit.error") + "- Request: " + request + "- Exception: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("request.submit.error"));
        }
    }

    @Override
    public GeneralInformationResponse updateIsOkFieldTheContact(UpdateOkFieldContactRequest request) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            Optional<Contact> optionalContact = contactRepository.findById(request.getId());

            if (optionalContact.isEmpty()) {
                LOG.info(responseUtils.getMessage("contact.request.not.found") + "- Request: " + request);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("contact.request.not.found"));
            }

            Contact contact = optionalContact.get();
            contact.setIsOkay(request.getIsOk());

            contactRepository.save(contact);

            LOG.info(responseUtils.getMessage("contact.update.success") + "- Request: " + request);
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("contact.update.success"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("contact.update.error") + "- Request: " + request + "- Exception: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("contact.update.error"));
        }
    }

    @Override
    public PageableContactResponse getAllContacts(Pageable pageable) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Request: " + pageable);
                return createPageableContactResponse(false, responseUtils.getMessage("invalid.request"), null);
            }

            Page<Contact> contacts = contactRepository.findAll(pageable);

            if (contacts.isEmpty()) {
                LOG.info(responseUtils.getMessage("contact.request.not.found") + "- Request: " + pageable);
                return createPageableContactResponse(false, responseUtils.getMessage("contact.request.not.found"), null);
            }

            LOG.info(responseUtils.getMessage("contact.requests.fetch.success") + "- Request: " + pageable);
            return createPageableContactResponse(true, responseUtils.getMessage("contact.requests.fetch.success"), contacts);
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("contact.requests.fetch.error") + "- Request: " + pageable + "- Exception: " + e.getMessage());
            return createPageableContactResponse(false, responseUtils.getMessage("contact.requests.fetch.error"), null);
        }
    }

    @Override
    public ContactResponse getContactById(Long id) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Id: " + id);
                return createContactResponse(false, responseUtils.getMessage("invalid.request"), null);
            }

            Optional<Contact> contact = contactRepository.findById(id);

            if (contact.isEmpty()) {
                LOG.info(responseUtils.getMessage("contact.request.not.found") + "- Id: " + id);
                return createContactResponse(false, responseUtils.getMessage("contact.request.not.found"), null);
            }

            LOG.info(responseUtils.getMessage("contact.requests.fetch.success") + "- Id: " + id);
            return createContactResponse(true, responseUtils.getMessage("contact.requests.fetch.success"), contact.get());
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("contact.requests.fetch.error") + "- Id: " + id + "- Exception: " + e.getMessage());
            return createContactResponse(false, responseUtils.getMessage("contact.requests.fetch.error"), null);
        }
    }

    @Override
    public GeneralInformationResponse deleteContactById(Long id) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Id: " + id);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            Optional<Contact> optionalContact = contactRepository.findById(id);

            if (optionalContact.isEmpty()) {
                LOG.info(responseUtils.getMessage("contact.request.not.found") + "- Id: " + id);
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("contact.request.not.found"));
            }

            contactRepository.delete(optionalContact.get());

            LOG.info(responseUtils.getMessage("contact.delete.success") + "- Id: " + id);
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("contact.delete.success"));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("contact.delete.error") + "- Id: " + id + "- Exception: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("contact.delete.error"));
        }
    }

    private CompanyContact createCompanyContactModel(CreateOrUpdateCompanyContactRequest request) {
        return CompanyContact.builder()
                .name(request.getName())
                .email(request.getEmail())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .instagram(request.getInstagram())
                .linkedin(request.getLinkedin())
                .twitter(request.getTwitter())
                .facebook(request.getFacebook())
                .workingHours(workingHoursRepository.findAll())
                .timeCreated(new Date())
                .timeModified(new Date())
                .build();
    }

    private void setCompanyContactFields(CompanyContact companyContact, CreateOrUpdateCompanyContactRequest request) {
        companyContact.setName(request.getName());
        companyContact.setEmail(request.getEmail());
        companyContact.setAddress(request.getAddress());
        companyContact.setPhoneNumber(request.getPhoneNumber());
        companyContact.setFacebook(request.getFacebook());
        companyContact.setInstagram(request.getInstagram());
        companyContact.setTwitter(request.getTwitter());
        companyContact.setLinkedin(request.getLinkedin());
    }

    private CompanyContactResponse createCompanyContactResponse(Boolean isSuccess, String message, CompanyContact companyContact) {
        return CompanyContactResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .companyContact(Objects.nonNull(companyContact) ? createCompanyContactDetail(companyContact) : null)
                .build();
    }

    private CompanyContactDetail createCompanyContactDetail(CompanyContact companyContact) {
        return CompanyContactDetail.builder()
                .name(companyContact.getName())
                .email(companyContact.getEmail())
                .address(companyContact.getAddress())
                .phoneNumber(companyContact.getPhoneNumber())
                .instagram(companyContact.getInstagram())
                .linkedin(companyContact.getLinkedin())
                .twitter(companyContact.getTwitter())
                .facebook(companyContact.getFacebook())
                .workingHoursDetail(createWorkingHoursDetail(companyContact.getWorkingHours()))
                .build();
    }

    private List<WorkingHoursDetail> createWorkingHoursDetail(List<WorkingHours> workingHours) {
        List<WorkingHoursDetail> list = new ArrayList<>();

        workingHours.forEach(wH -> list.add(WorkingHoursDetail.builder()
                .day(wH.getDay().toString())
                .startHours(wH.getStartHours())
                .endHours(wH.getEndHours())
                .build())
        );

        return list;
    }

    private Contact createContactModel(CreateContactRequest request) {
        return Contact.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .message(request.getMessage())
                .isOkay(false)
                .timeCreated(new Date())
                .timeModified(new Date())
                .build();
    }

    private PageableContactResponse createPageableContactResponse(Boolean isSuccess, String message, Page<Contact> contacts) {
        return PageableContactResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .contacts(Objects.nonNull(contacts) ? createPageableContactDetail(contacts) : null)
                .build();
    }

    private Page<ContactDetail> createPageableContactDetail(Page<Contact> contacts) {
        return contacts.map(contact -> ContactDetail.builder()
                .id(contact.getId())
                .name(contact.getName())
                .email(contact.getEmail())
                .phoneNumber(contact.getPhoneNumber())
                .isOkay(contact.getIsOkay())
                .build());
    }

    private ContactResponse createContactResponse(Boolean isSuccess, String message, Contact contact) {
        return ContactResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .contact(Objects.nonNull(contact) ? createContactDetail(contact) : null)
                .build();
    }

    private ContactDetail createContactDetail(Contact contact) {
        return ContactDetail.builder()
                .id(contact.getId())
                .message(contact.getMessage())
                .isOkay(contact.getIsOkay())
                .build();
    }

}
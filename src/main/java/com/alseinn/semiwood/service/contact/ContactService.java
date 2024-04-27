package com.alseinn.semiwood.service.contact;

import com.alseinn.semiwood.request.contact.CreateContactRequest;
import com.alseinn.semiwood.request.contact.CreateOrUpdateCompanyContactRequest;
import com.alseinn.semiwood.request.contact.UpdateOkFieldContactRequest;
import com.alseinn.semiwood.response.contact.CompanyContactResponse;
import com.alseinn.semiwood.response.contact.PageableContactResponse;
import com.alseinn.semiwood.response.contact.ContactResponse;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import org.springframework.data.domain.Pageable;

public interface ContactService {
    GeneralInformationResponse createCompanyContact(CreateOrUpdateCompanyContactRequest request);
    GeneralInformationResponse updateCompanyContact(CreateOrUpdateCompanyContactRequest request);
    CompanyContactResponse getCompanyContact();
    GeneralInformationResponse createContact(CreateContactRequest request);
    GeneralInformationResponse updateIsOkFieldTheContact(UpdateOkFieldContactRequest request);
    PageableContactResponse getAllContacts(Pageable pageable);
    ContactResponse getContactById(Long id);
    GeneralInformationResponse deleteContactById(Long id);
}

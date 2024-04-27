package com.alseinn.semiwood.controller.contact;

import com.alseinn.semiwood.request.contact.CreateContactRequest;
import com.alseinn.semiwood.request.contact.CreateOrUpdateCompanyContactRequest;
import com.alseinn.semiwood.request.contact.UpdateOkFieldContactRequest;
import com.alseinn.semiwood.response.contact.CompanyContactResponse;
import com.alseinn.semiwood.response.contact.PageableContactResponse;
import com.alseinn.semiwood.response.contact.ContactResponse;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.service.contact.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${default.api.path}/contact")
@RequiredArgsConstructor
@CrossOrigin
public class ContactController {

    private final ContactService contactService;

    @PostMapping("/company")
    public GeneralInformationResponse createCompanyContact(@RequestBody CreateOrUpdateCompanyContactRequest request) {
        return contactService.createCompanyContact(request);
    }

    @PutMapping("/company")
    public GeneralInformationResponse updateCompanyContact(@RequestBody CreateOrUpdateCompanyContactRequest request) {
        return contactService.updateCompanyContact(request);
    }

    @GetMapping("/company")
    public CompanyContactResponse getCompanyContact() {
        return contactService.getCompanyContact();
    }

    @PostMapping()
    public GeneralInformationResponse createContact(@RequestBody CreateContactRequest request) {
        return contactService.createContact(request);
    }

    @PutMapping()
    public GeneralInformationResponse updateIsOkFieldTheContact(@RequestBody UpdateOkFieldContactRequest request) {
        return contactService.updateIsOkFieldTheContact(request);
    }

    @GetMapping()
    public PageableContactResponse getAllContacts(@RequestParam(name = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("timeCreated").ascending());
        return contactService.getAllContacts(pageable);
    }

    @GetMapping("/{id}")
    public ContactResponse getContactById(@PathVariable Long id) {
        return contactService.getContactById(id);
    }

    @DeleteMapping("/{id}")
    public GeneralInformationResponse deleteContactById(@PathVariable Long id) {
        return contactService.deleteContactById(id);
    }

}
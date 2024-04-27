package com.alseinn.semiwood.controller.company;

import com.alseinn.semiwood.request.company.CreateCompanyRequest;
import com.alseinn.semiwood.request.company.UpdateCompanyRequest;
import com.alseinn.semiwood.response.company.PageableCompanyResponse;
import com.alseinn.semiwood.response.company.SessionUserCompanyResponse;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.service.company.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${default.api.path}/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    @PostMapping()
    public GeneralInformationResponse createCompany(@RequestBody CreateCompanyRequest request) {
        return companyService.createCompany(request);
    }
    @PutMapping()
    public GeneralInformationResponse updateCompany(@RequestBody UpdateCompanyRequest request) {
        return companyService.updateCompany(request);
    }
    @PutMapping("/activate/{id}")
    public GeneralInformationResponse activateCompany(@PathVariable Long id) {
        return companyService.activateCompany(id);
    }
    @GetMapping()
    public PageableCompanyResponse getAllCompanies(@RequestParam(name = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("name").ascending());
        return companyService.getAllCompanies(pageable);
    }
    @GetMapping("/user")
    public SessionUserCompanyResponse getSessionUserCompany() {
        return companyService.getSessionUserCompany();
    }
    @DeleteMapping("/{id}")
    public GeneralInformationResponse deleteCompanyById(@PathVariable Long id) {
        return companyService.deleteCompanyById(id);
    }
    @DeleteMapping("/user")
    public GeneralInformationResponse deleteSessionUserCompany() {
        return companyService.deleteSessionUserCompany();
    }
}
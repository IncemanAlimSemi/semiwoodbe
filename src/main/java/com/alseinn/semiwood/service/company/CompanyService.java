package com.alseinn.semiwood.service.company;

import com.alseinn.semiwood.request.company.CreateCompanyRequest;
import com.alseinn.semiwood.request.company.UpdateCompanyRequest;
import com.alseinn.semiwood.response.company.PageableCompanyResponse;
import com.alseinn.semiwood.response.company.SessionUserCompanyResponse;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import org.springframework.data.domain.Pageable;

public interface CompanyService {
    GeneralInformationResponse createCompany(CreateCompanyRequest request);
    GeneralInformationResponse updateCompany(UpdateCompanyRequest request);
    GeneralInformationResponse activateCompany(Long id);
    PageableCompanyResponse getAllCompanies(Pageable pageable);
    SessionUserCompanyResponse getSessionUserCompany();
    GeneralInformationResponse deleteCompanyById(Long id);
    GeneralInformationResponse deleteSessionUserCompany();
}

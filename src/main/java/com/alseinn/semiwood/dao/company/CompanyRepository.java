package com.alseinn.semiwood.dao.company;

import com.alseinn.semiwood.entity.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findAllByNameOrPhoneNumberOrEmailOrTaxNumber(String name, String phoneNumber, String email, String taxNumber);
    Optional<Company> findByPhoneNumber(String phoneNumber);
    Optional<Company> findByEmail(String email);
    Optional<Company> findByTaxNumber(String taxNumber);
}

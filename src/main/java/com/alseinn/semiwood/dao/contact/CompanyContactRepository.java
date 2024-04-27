package com.alseinn.semiwood.dao.contact;

import com.alseinn.semiwood.entity.contact.CompanyContact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyContactRepository extends JpaRepository<CompanyContact, Long> {
}
